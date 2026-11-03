package ru.tusiclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.tusiclient.module.Module;
import ru.tusiclient.module.ModuleManager;
import ru.tusiclient.module.impl.NeuroAura;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "handleChatInput(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    private void onHandleChatInput(String message, boolean addToRecentChat, CallbackInfo ci) {
        if (message.startsWith(".aura ")) {
            MinecraftClient mc = MinecraftClient.getInstance();
            Module aura = ModuleManager.getModule("NeuroAura");

            if (aura instanceof NeuroAura) {
                NeuroAura neuro = (NeuroAura) aura;
                String action = message.substring(6).trim().toLowerCase();

                if (action.equals("start")) {
                    neuro.startTraining();
                } else if (action.equals("save")) {
                    neuro.saveTraining();
                } else {
                    if (mc.player != null) {
                        mc.player.sendMessage(new LiteralText("§c[NeuroAura] Неизвестная команда. Используй .aura start или .aura save"), false);
                    }
                }
            }
            ci.cancel(); // Отменяем отправку на сервер
        }
    }
}
