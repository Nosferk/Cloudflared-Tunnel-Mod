package com.nosferk.cloudflaredtunnel;

import com.nosferk.cloudflaredtunnel.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

public class CloudflaredTunnelClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Aguarda um pouco para garantir que o tunnel seja iniciado primeiro
		new Thread(() -> {
			try {
				Thread.sleep(3000); // 3 segundos de delay
				
				ModConfig config = ModConfig.getInstance();
				
				// Só adiciona servidor se estiver habilitado na configuração
				if (config.autoAddToServerList) {
					int port = CloudflaredTunnel.getTunnelPort();
					if (port > 0) {
						addServer(config.serverName, "127.0.0.1:" + port);
					}
				}
			} catch (InterruptedException e) {
				CloudflaredTunnel.LOGGER.error("Thread interrompida", e);
			}
		}).start();
	}
	
	private void addServer(String name, String address) {
        MinecraftClient client = MinecraftClient.getInstance();
        ServerList list = new ServerList(client);
        list.loadFile();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).address.equals(address)) {
                CloudflaredTunnel.LOGGER.info("Servidor já existe na lista.");
                return;
            }
        }

        // Em 1.21.x o construtor agora pede um tipo usando enum interno ServerInfo.Type
        ServerInfo serverInfo = new ServerInfo(
                name,
                address,
                ServerInfo.ServerType.OTHER
        );

        list.add(serverInfo, false);
        list.saveFile();
        CloudflaredTunnel.LOGGER.info("Servidor adicionado: {} ({})", name, address);
    }
}