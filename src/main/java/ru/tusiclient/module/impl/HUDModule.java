package ru.tusiclient.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import ru.tusiclient.module.Module;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD Box", "Render");
    }

    public void render2D(MatrixStack matrices, Camera camera, float tickDelta) {
        if (!enabled || mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) continue;

            // Позиция над головой
            Vec3d pos = new Vec3d(
                    player.prevX + (player.getX() - player.prevX) * tickDelta,
                    player.prevY + (player.getY() - player.prevY) * tickDelta + player.getHeight() + 0.5,
                    player.prevZ + (player.getZ() - player.prevZ) * tickDelta
            );
            
            Vec3d camPos = camera.getPos();
            Vec3d relative = pos.subtract(camPos);
            
            // Перевод 3D в 2D
            double screenX = relative.x / relative.z;
            double screenY = relative.y / relative.z;
            
            // Если игрок позади камеры, не рисуем
            if (relative.z <= 0) continue;

            int x = (int) (mc.getWindow().getScaledWidth() / 2 + screenX * 500);
            int y = (int) (mc.getWindow().getScaledHeight() / 2 - screenY * 500);

            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            int armor = player.getArmor();

            // 1. Полоска здоровья (Красная) + Flashing (мигает, если хп < 5)
            int barWidth = 50;
            int healthWidth = (int) ((health / maxHealth) * barWidth);
            int healthColor = (health < 5 && System.currentTimeMillis() % 500 > 250) ? 0xFFFF0000 : 0xFFAA0000;
            
            DrawableHelper.fill(matrices, x - barWidth / 2, y, x + barWidth / 2, y + 3, 0xFF000000);
            DrawableHelper.fill(matrices, x - barWidth / 2, y, x - barWidth / 2 + healthWidth, y + 3, healthColor);

            // 2. Полоска брони (Голубая)
            if (armor > 0) {
                DrawableHelper.fill(matrices, x - barWidth / 2, y + 4, x + barWidth / 2, y + 6, 0xFF000000);
                DrawableHelper.fill(matrices, x - barWidth / 2, y + 4, x - barWidth / 2 + Math.min(armor, 20) * (barWidth / 20), y + 6, 0xFF00FFFF);
            }

            // 3. Имя
            String name = player.getName().getString();
            mc.textRenderer.drawWithShadow(matrices, name, x - mc.textRenderer.getWidth(name) / 2f, y - 10, 0xFFFFFFFF);
            
            // 4. Weapon Icon (Оружие в руке)
            ItemStack weapon = player.getMainHandStack();
            if (!weapon.isEmpty()) {
                mc.getItemRenderer().renderInGui(weapon, x + barWidth / 2 + 2, y - 15);
            }
            
            // 5. Scoped (Если в руках лук)
            if (player.getMainHandStack().getItem() instanceof BowItem) {
                 mc.textRenderer.drawWithShadow(matrices, "[SCOPE]", x - barWidth / 2 - 35, y - 5, 0xFFFFAA00);
            }
        }
    }
}
