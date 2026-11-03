package ru.tusiclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.tusiclient.module.Module;
import ru.tusiclient.module.ModuleManager;
import ru.tusiclient.module.impl.NeuroAura;

@Mixin(ClientPlayNetworkHandler.class)
public class NetworkHandlerMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(net.minecraft.network.Packet<?> packet, CallbackInfo ci) {
        // Проверяем, что это именно пакет с сообщением в чат
        if (packet instanceof ChatMessageC2SPacket) {
            String message = ((ChatMessageC2SPacket) packet).getChatMessage();

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
                // Отменяем отправку пакета на сервер
                ci.cancel();
            }
        }
    }
}
