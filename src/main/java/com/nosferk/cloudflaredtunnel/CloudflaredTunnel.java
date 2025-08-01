package com.nosferk.cloudflaredtunnel;
//Copyright (c) 2025 Nosferk
//Attribution-NonCommercial-NoDerivatives 4.0 International
import com.nosferk.cloudflaredtunnel.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class CloudflaredTunnel implements ModInitializer {
    public static final String MOD_ID = "cloudflared-tunnel";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static int tunnelPort = -1;
    private static Process currentProcess = null;
    private static final List<String> outputLog = new ArrayList<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Iniciando CloudflaredTunnel...");
        startTunnel();
    }
    
    public static void startTunnel() {
        ModConfig config = ModConfig.getInstance();
        int port = config.getPort();
        tunnelPort = port;

        try {
            stopTunnel(); // Para o tunnel anterior se existir
            startCloudflared(port, config.hostname);
        } catch (IOException e) {
            LOGGER.error("Erro ao iniciar cloudflared", e);
            addToLog("ERRO: " + e.getMessage());
        }

        LOGGER.info("CloudflaredTunnel iniciado na porta {} com hostname {}", port, config.hostname);
        addToLog("Tunnel iniciado na porta " + port + " com hostname " + config.hostname);
    }
    
    public static void stopTunnel() {
        if (currentProcess != null && currentProcess.isAlive()) {
            LOGGER.info("Parando tunnel anterior...");
            addToLog("Parando tunnel anterior...");
            currentProcess.destroyForcibly();
            try {
                currentProcess.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            currentProcess = null;
        }
    }
    
    public static int getTunnelPort() {
        return tunnelPort;
    }
    
    public static List<String> getOutputLog() {
        return new ArrayList<>(outputLog);
    }
    
    private static void addToLog(String message) {
        synchronized (outputLog) {
            outputLog.add("[" + java.time.LocalTime.now().toString() + "] " + message);
            // Manter apenas as últimas 100 linhas
            if (outputLog.size() > 100) {
                outputLog.remove(0);
            }
        }
    }

    private static void startCloudflared(int port, String hostname) throws IOException {
        File exe = getCloudflaredExecutable();
        if (exe == null) {
            LOGGER.error("Não foi possível obter o cloudflared.exe");
            addToLog("ERRO: Não foi possível obter o cloudflared.exe");
            return;
        }

        LOGGER.info("Executando cloudflared na porta {} com hostname {}", port, hostname);
        addToLog("Executando cloudflared na porta " + port + " com hostname " + hostname);

        ProcessBuilder builder = new ProcessBuilder(
                exe.getAbsolutePath(),
                "access", "tcp",
                "--hostname", hostname,
                "--url", "127.0.0.1:" + port
        );
        
        // Captura a saída do processo
        builder.redirectErrorStream(true);
        currentProcess = builder.start();
        
        // Thread para ler a saída do processo
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    addToLog(line);
                }
            } catch (IOException e) {
                addToLog("Erro ao ler saída: " + e.getMessage());
            }
        }).start();
    }

    private static File getCloudflaredExecutable() {
        // Primeiro tenta na pasta data
        File dataDir = new File("data");
        File exeInData = new File(dataDir, "cloudflared.exe");
        
        if (exeInData.exists()) {
            LOGGER.info("Usando cloudflared.exe existente em ./data/");
            return exeInData;
        }

        // Se não existe, tenta extrair do JAR
        try {
            // Cria a pasta data se não existir
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Tenta extrair do JAR
            InputStream resourceStream = CloudflaredTunnel.class.getResourceAsStream("/cloudflared.exe");
            if (resourceStream == null) {
                LOGGER.error("cloudflared.exe não encontrado no JAR. Coloque o arquivo em src/main/resources/");
                return null;
            }

            LOGGER.info("Extraindo cloudflared.exe do JAR para ./data/");
            
            Path targetPath = Paths.get(exeInData.getAbsolutePath());
            Files.copy(resourceStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            resourceStream.close();

            // Torna o arquivo executável (no Windows isso não é necessário, mas não faz mal)
            exeInData.setExecutable(true);

            LOGGER.info("cloudflared.exe extraído com sucesso para ./data/");
            return exeInData;

        } catch (IOException e) {
            LOGGER.error("Erro ao extrair cloudflared.exe do JAR", e);
            return null;
        }
    }
}
