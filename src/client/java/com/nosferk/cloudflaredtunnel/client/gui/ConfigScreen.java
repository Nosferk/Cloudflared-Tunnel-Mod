package com.nosferk.cloudflaredtunnel.client.gui;

import com.nosferk.cloudflaredtunnel.CloudflaredTunnel;
import com.nosferk.cloudflaredtunnel.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;
    
    private TextFieldWidget hostnameField;
    private TextFieldWidget fixedPortField;
    private TextFieldWidget serverNameField;
    private CheckboxWidget useRandomPortCheckbox;
    private CheckboxWidget autoAddServerCheckbox;
    
    private boolean showOutputTab = false;
    private int outputScrollOffset = 0;
    
    public ConfigScreen(Screen parent) {
        super(Text.translatable("cloudflared-tunnel.config.title"));
        this.parent = parent;
        this.config = ModConfig.getInstance();
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        int spacing = 25;
        int fieldWidth = 200;
        
        if (showOutputTab) {
            renderOutputTab();
        } else {
            renderConfigTab(centerX, startY, spacing, fieldWidth);
        }
    }
    
    private void renderConfigTab(int centerX, int startY, int spacing, int fieldWidth) {
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Hostname:"),
                button -> {})
                .dimensions(centerX - fieldWidth/2 - 70, startY, 60, 20)
                .build());
        
        hostnameField = new TextFieldWidget(this.textRenderer, centerX - fieldWidth/2, startY, fieldWidth, 20, Text.literal(""));
        hostnameField.setText(config.hostname);
        this.addSelectableChild(hostnameField);
        this.addDrawableChild(hostnameField);
        
        startY += spacing;
        
        useRandomPortCheckbox = CheckboxWidget.builder(Text.literal("Usar porta aleatória"), this.textRenderer)
                .pos(centerX - fieldWidth/2, startY)
                .checked(config.useRandomPort)
                .build();
        this.addDrawableChild(useRandomPortCheckbox);
        
        startY += spacing;
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Porta fixa:"),
                button -> {})
                .dimensions(centerX - fieldWidth/2 - 80, startY, 70, 20)
                .build());
        
        fixedPortField = new TextFieldWidget(this.textRenderer, centerX - fieldWidth/2, startY, fieldWidth, 20, Text.literal(""));
        fixedPortField.setText(String.valueOf(config.fixedPort));
        fixedPortField.setEditable(!config.useRandomPort);
        this.addSelectableChild(fixedPortField);
        this.addDrawableChild(fixedPortField);
        
        startY += spacing;
        
        autoAddServerCheckbox = CheckboxWidget.builder(Text.literal("Adicionar servidor automaticamente"), this.textRenderer)
                .pos(centerX - fieldWidth/2, startY)
                .checked(config.autoAddToServerList)
                .build();
        this.addDrawableChild(autoAddServerCheckbox);
        
        startY += spacing;
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Nome do servidor:"),
                button -> {})
                .dimensions(centerX - fieldWidth/2 - 110, startY, 100, 20)
                .build());
        
        serverNameField = new TextFieldWidget(this.textRenderer, centerX - fieldWidth/2, startY, fieldWidth, 20, Text.literal(""));
        serverNameField.setText(config.serverName);
        serverNameField.setEditable(config.autoAddToServerList);
        this.addSelectableChild(serverNameField);
        this.addDrawableChild(serverNameField);
        
        startY += spacing * 2;
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Salvar"),
                button -> this.save())
                .dimensions(centerX - 150, startY, 70, 20)
                .build());
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Recarregar Tunnel"),
                button -> this.reloadTunnel())
                .dimensions(centerX - 75, startY, 120, 20)
                .build());
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Cancelar"),
                button -> this.close())
                .dimensions(centerX + 50, startY, 70, 20)
                .build());
    }
    
    private void renderOutputTab() {
        int centerX = this.width / 2;
        int startY = 50;
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Atualizar"),
                button -> {})
                .dimensions(centerX - 50, startY, 100, 20)
                .build());
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("↑"),
                button -> {
                    outputScrollOffset = Math.max(0, outputScrollOffset - 1);
                })
                .dimensions(this.width - 30, startY + 30, 20, 20)
                .build());
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("↓"),
                button -> {
                    List<String> log = CloudflaredTunnel.getOutputLog();
                    int maxLines = (this.height - 120) / 12;
                    outputScrollOffset = Math.min(Math.max(0, log.size() - maxLines), outputScrollOffset + 1);
                })
                .dimensions(this.width - 30, startY + 55, 20, 20)
                .build());
        
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Fechar"),
                button -> this.close())
                .dimensions(centerX - 50, this.height - 40, 100, 20)
                .build());
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!showOutputTab) {
            if (fixedPortField != null) {
                fixedPortField.setEditable(!useRandomPortCheckbox.isChecked());
            }
            
            if (serverNameField != null) {
                serverNameField.setEditable(autoAddServerCheckbox.isChecked());
            }
        }
    }
    
    private void save() {
        try {
            config.hostname = hostnameField.getText();
            config.useRandomPort = useRandomPortCheckbox.isChecked();
            config.autoAddToServerList = autoAddServerCheckbox.isChecked();
            config.serverName = serverNameField.getText();
            
            if (!config.useRandomPort) {
                int port = Integer.parseInt(fixedPortField.getText());
                if (port > 0 && port <= 65535) {
                    config.fixedPort = port;
                } else {
                    fixedPortField.setText(String.valueOf(config.fixedPort));
                }
            }
            
            config.save();
        } catch (NumberFormatException e) {
            fixedPortField.setText(String.valueOf(config.fixedPort));
        }
    }
    
    private void reloadTunnel() {
        save();
        
        new Thread(() -> {
            try {
                CloudflaredTunnel.startTunnel();
            } catch (Exception e) {
                CloudflaredTunnel.LOGGER.error("Erro ao recarregar tunnel", e);
            }
        }).start();
    }
    
    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        String title = showOutputTab ? "Saída do Cloudflared Tunnel" : "Configurações do Cloudflared Tunnel";
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(title), this.width / 2, 35, 0xFFFFFF);
        
        if (showOutputTab) {
            renderOutputContent(context);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderOutputContent(DrawContext context) {
        List<String> log = CloudflaredTunnel.getOutputLog();
        int y = 80;
        int maxLines = (this.height - 120) / 12;
        int startIndex = Math.max(0, Math.min(outputScrollOffset, log.size() - maxLines));
        int endIndex = Math.min(log.size(), startIndex + maxLines);
        
        // Fundo da área de texto
        context.fill(10, y - 5, this.width - 40, this.height - 50, 0x80000000);
        
        for (int i = startIndex; i < endIndex; i++) {
            String line = log.get(i);
            if (line.length() > 80) {
                line = line.substring(0, 77) + "...";
            }
            context.drawTextWithShadow(this.textRenderer, line, 15, y, 0xFFFFFF);
            y += 12;
        }
        
        if (log.size() > maxLines) {
            context.drawTextWithShadow(this.textRenderer, 
                "Linha " + (startIndex + 1) + "-" + endIndex + " de " + log.size(), 
                15, this.height - 45, 0xAAAAAA);
        }
    }
}
