package com.nosferk.cloudflaredtunnel.client.mixin;

import com.nosferk.cloudflaredtunnel.client.gui.ConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    
    protected TitleScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void addConfigButton(CallbackInfo ci) {
        // Adiciona botão de configuração no canto inferior direito
        ButtonWidget configButton = ButtonWidget.builder(
                Text.literal("Tunnel Config"),
                button -> {
                    if (this.client != null) {
                        this.client.setScreen(new ConfigScreen((TitleScreen) (Object) this));
                    }
                })
                .dimensions(this.width - 120, this.height - 30, 115, 20)
                .build();
        
        this.addDrawableChild(configButton);
    }
}
