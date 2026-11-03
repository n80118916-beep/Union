package ru.tusiclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.tusiclient.gui.ClickGUI;
import ru.tusiclient.module.ModuleManager;

public class TusiClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        // Запускаем систему модулей
        ModuleManager.init();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tusiclient.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.tusiclient"
        ));

        // Слушаем тики игры (для работы AutoSprint и NeuroAura)
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
