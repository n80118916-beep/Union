package ru.tusiclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.tusiclient.gui.ClickGUI;
import ru.tusiclient.module.Module;
import ru.tusiclient.module.ModuleManager;
import ru.tusiclient.module.impl.ESPModule;
import ru.tusiclient.module.impl.HUDModule;
import ru.tusiclient.module.impl.WingsModule;

public class TusiClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        // 1. Рендер в 3D-мире (ESP и Крылья)
        WorldRenderEvents.LAST.register((context) -> {
            for (Module mod : ModuleManager.modules) {
                if (!mod.enabled) continue;
                if (mod instanceof ESPModule) ((ESPModule) mod).render3D(context.matrixStack(), context.camera(), context.tickDelta());
                if (mod instanceof WingsModule) ((WingsModule) mod).render3D(context.matrixStack(), context.camera(), context.tickDelta());
            }
        });

        // 2. Рендер HUD (2D на экране)
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            for (Module mod : ModuleManager.modules) {
                if (!mod.enabled) continue;
                if (mod instanceof HUDModule) ((HUDModule) mod).render2D(matrixStack, tickDelta);
            }
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
