package ru.tusiclient.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import ru.tusiclient.module.Module;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD Box", "Render");
    }

    public void render2D(MatrixStack matrices, float tickDelta) {
        if (!enabled || mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) continue;

            double px = MathHelper.lerp(tickDelta, player.prevX, player.getX());
            double py = MathHelper.lerp(tickDelta, player.prevY, player.getY());
            double pz = MathHelper.lerp(tickDelta, player.prevZ, player.getZ());
            Vec3d target = new Vec3d(px, py + player.getHeight() + 0.5, pz);
            
            double[] pos = project2D(target);
            if (pos == null) continue;

            int x = (int) pos[0];
            int y = (int) pos[1];

            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            int armor = player.getArmor();

            // 1. Полоска здоровья
            int barWidth = 50;
            int healthWidth = (int) ((health / maxHealth) * barWidth);
            int healthColor = (health < 5 && System.currentTimeMillis() % 500 > 250) ? 0xFFFF0000 : 0xFFAA0000;
            
            DrawableHelper.fill(matrices, x - barWidth / 2, y, x + barWidth / 2, y + 3, 0xFF000000);
            DrawableHelper.fill(matrices, x - barWidth / 2, y, x - barWidth / 2 + healthWidth, y + 3, healthColor);

            // 2. Полоска брони
            if (armor > 0) {
                DrawableHelper.fill(matrices, x - barWidth / 2, y + 4, x + barWidth / 2, y + 6, 0xFF000000);
                DrawableHelper.fill(matrices, x - barWidth / 2, y + 4, x - barWidth / 2 + Math.min(armor, 20) * (barWidth / 20), y + 6, 0xFF00FFFF);
            }

            // 3. Имя
            String name = player.getName().getString();
            mc.textRenderer.drawWithShadow(matrices, name, x - mc.textRenderer.getWidth(name) / 2f, y - 10, 0xFFFFFFFF);
            
            // 4. Weapon Icon
            ItemStack weapon = player.getMainHandStack();
            if (!weapon.isEmpty()) {
                mc.getItemRenderer().renderInGui(weapon, x + barWidth / 2 + 2, y - 10);
            }
            
            // 5. Scoped
            if (player.getMainHandStack().getItem() instanceof BowItem) {
                 mc.textRenderer.drawWithShadow(matrices, "[SCOPE]", x - barWidth / 2 - 35, y - 5, 0xFFFFAA00);
            }
        }
    }

    private double[] project2D(Vec3d target) {
        Matrix4f projection = RenderSystem.getProjectionMatrix();
        Matrix4f modelView = RenderSystem.getModelViewMatrix();
        Matrix4f combined = projection.copy();
        combined.multiply(modelView);

        Vector4f pos = new Vector4f((float) target.x, (float) target.y, (float) target.z, 1.0f);
        pos.transform(combined);

        if (pos.getW() <= 0.0f) return null;

        float ndcX = pos.getX() / pos.getW();
        float ndcY = pos.getY() / pos.getW();

        double screenX = (ndcX + 1.0) * 0.5 * mc.getWindow().getScaledWidth();
        double screenY = (1.0 - ndcY) * 0.5 * mc.getWindow().getScaledHeight();

        return new double[]{screenX, screenY};
    }
                                    }
