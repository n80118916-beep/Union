package ru.tusiclient.mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.tusiclient.module.Module;
import ru.tusiclient.module.ModuleManager;
import ru.tusiclient.module.impl.NeuroAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String message, boolean addToHistory, CallbackInfo ci) {
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
                    mc.player.sendMessage(new LiteralText("§c[NeuroAura] Неизвестная команда. Используй .aura start или .aura save"), false);
                }
            }
            // Отменяем отправку сообщения на сервер
            ci.cancel();
        }
    }
}
