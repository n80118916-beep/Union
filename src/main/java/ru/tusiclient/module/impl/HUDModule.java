package ru.tusiclient.module.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import ru.tusiclient.module.Module;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD Box", "Render");
    }

    public void render2D(MatrixStack matrices, float tickDelta) {
        if (!enabled || mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) continue;

            // Получаем 2D координаты на экране
            double px = MathHelper.lerp(tickDelta, player.prevX, player.getX());
            double py = MathHelper.lerp(tickDelta, player.prevY, player.getY());
            double pz = MathHelper.lerp(tickDelta, player.prevZ, player.getZ());
            Vec3d target = new Vec3d(px, py + player.getHeight() + 0.5, pz);
            
            Vector2f pos = project2D(target);
            if (pos == null) continue;

            int x = (int) pos.x;
            int y = (int) pos.y;

            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            int armor = player.getArmor();

            // 1. Полоска здоровья (Красная)
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
                mc.getItemRenderer().renderInGui(weapon, x + barWidth / 2 + 2, y - 10);
            }
            
            // 5. Scoped (Если в руках лук)
            if (player.getMainHandStack().getItem() instanceof BowItem) {
                 mc.textRenderer.drawWithShadow(matrices, "[SCOPE]", x - barWidth / 2 - 35, y - 5, 0xFFFFAA00);
            }
        }
    }

    private Vector2f project2D(Vec3d target) {
        Vec3d cam = mc.gameRenderer.getCamera().getPos();
        double dx = target.x - cam.x;
        double dy = target.y - cam.y;
        double dz = target.z - cam.z;
        
        double yaw = Math.toRadians(mc.gameRenderer.getCamera().getYaw());
        double pitch = Math.toRadians(mc.gameRenderer.getCamera().getPitch());
        
        double xRot = dz * Math.sin(yaw) + dx * Math.cos(yaw);
        double zRot = dz * Math.cos(yaw) - dx * Math.sin(yaw);
        double yRot = dy * Math.cos(pitch) - zRot * Math.sin(pitch);
        double zDepth = dy * Math.sin(pitch) + zRot * Math.cos(pitch);
        
        if (zDepth > 0.1) {
            double fov = Math.tan(Math.toRadians(MinecraftClient.getInstance().options.fov) / 2.0);
            float screenX = (float) (mc.getWindow().getScaledWidth() / 2.0 + (xRot / zDepth * (mc.getWindow().getScaledHeight() / 2.0) / fov));
            float screenY = (float) (mc.getWindow().getScaledHeight() / 2.0 - (yRot / zDepth * (mc.getWindow().getScaledHeight() / 2.0) / fov));
            return new Vector2f(screenX, screenY);
        }
        return null;
    }
}
