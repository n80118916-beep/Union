package ru.tusiclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.tusiclient.gui.ClickGUI;
import ru.tusiclient.module.Module;
import ru.tusiclient.module.ModuleManager;
import ru.tusiclient.module.impl.NeuroAura;

public class TusiClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        // Обработка команд в чате (.Aura start / .Aura save)
        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            if (message.equalsIgnoreCase(".aura start")) {
                Module aura = ModuleManager.getModule("NeuroAura");
                if (aura instanceof NeuroAura) ((NeuroAura) aura).startTraining();
                return false; // Отменяем отправку на сервер
            }
            if (message.equalsIgnoreCase(".aura save")) {
                Module aura = ModuleManager.getModule("NeuroAura");
                if (aura instanceof NeuroAura) ((NeuroAura) aura).saveTraining();
                return false;
            }
            return true;
        });

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tusiclient.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.tusiclient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModuleManager.onTick();

            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.openScreen(new ClickGUI());
                }
            }
        });
    }
}
