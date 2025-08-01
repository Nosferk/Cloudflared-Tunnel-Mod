package com.nosferk.cloudflaredtunnel.config;
//Copyright (c) 2025 Nosferk
//Attribution-NonCommercial-NoDerivatives 4.0 International
import com.nosferk.cloudflaredtunnel.CloudflaredTunnel;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ModConfig {
    private static final Path CONFIG_PATH = Paths.get("config", "cloudflared-tunnel.properties");
    
    private static ModConfig INSTANCE;
    
    // Configurações padrão
    public boolean useRandomPort = true;
    public int fixedPort = 25565;
    public String hostname = "play.exemple.com";
    public boolean autoAddToServerList = true;
    public String serverName = "Nosferk";
    
    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }
    
    public static ModConfig load() {
        File configFile = CONFIG_PATH.toFile();
        ModConfig config = new ModConfig();
        
        if (!configFile.exists()) {
            config.save();
            return config;
        }
        
        try (FileInputStream fis = new FileInputStream(configFile)) {
            Properties props = new Properties();
            props.load(fis);
            
            config.useRandomPort = Boolean.parseBoolean(props.getProperty("useRandomPort", "true"));
            config.fixedPort = Integer.parseInt(props.getProperty("fixedPort", "25565"));
            config.hostname = props.getProperty("hostname", "play.exemple.com");
            config.autoAddToServerList = Boolean.parseBoolean(props.getProperty("autoAddToServerList", "true"));
            config.serverName = props.getProperty("serverName", "Nosferk");
            
            return config;
        } catch (Exception e) {
            CloudflaredTunnel.LOGGER.error("Erro ao carregar configuração, usando padrão", e);
            config.save();
            return config;
        }
    }
    
    public void save() {
        try {
            File configFile = CONFIG_PATH.toFile();
            configFile.getParentFile().mkdirs();
            
            Properties props = new Properties();
            props.setProperty("useRandomPort", String.valueOf(useRandomPort));
            props.setProperty("fixedPort", String.valueOf(fixedPort));
            props.setProperty("hostname", hostname);
            props.setProperty("autoAddToServerList", String.valueOf(autoAddToServerList));
            props.setProperty("serverName", serverName);
            
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos, "Cloudflared Tunnel Configuration");
            }
            
            CloudflaredTunnel.LOGGER.info("Configuração salva em {}", CONFIG_PATH);
        } catch (Exception e) {
            CloudflaredTunnel.LOGGER.error("Erro ao salvar configuração", e);
        }
    }
    
    public int getPort() {
        if (useRandomPort) {
            return 25500 + new java.util.Random().nextInt(100);
        }
        return fixedPort;
    }
}
