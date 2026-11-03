package ru.tusiclient.module.impl;

import net.minecraft.client.util.math.MatrixStack;
import ru.tusiclient.module.Module;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD Box", "Render");
    }

    public void render2D(MatrixStack matrices, float tickDelta) {
        // Заглушка для стабильной сборки
    }
}
