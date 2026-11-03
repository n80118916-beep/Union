package ru.tusiclient.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import ru.tusiclient.module.Module;

public class RenderModules extends Module {
    
    public boolean esp = true;
    public boolean hudBox = true;

    public RenderModules() {
        super("RenderModules", "Render");
        this.enabled = true; // По умолчанию включено
    }

    // --- 3D РЕНДЕР (ESP) ---
    public void render3D(MatrixStack matrices, Camera camera, float tickDelta) {
        if (!esp || mc.world == null || mc.player == null) return;

        Vec3d camPos = camera.getPos();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) continue;

            double x = player.prevX + (player.getX() - player.prevX) * tickDelta - camPos.x;
            double y = player.prevY + (player.getY() - player.prevY) * tickDelta - camPos.y;
            double z = player.prevZ + (player.getZ() - player.prevZ) * tickDelta - camPos.z;

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            
            // Рисуем линии (Box)
            GL11.glLineWidth(1.5f);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
            
            float width = player.getWidth() / 2f;
            float height = player.getHeight();

            // Нижний квадрат
            drawLine(buffer, x - width, y, z - width, x + width, y, z - width);
            drawLine(buffer, x + width, y, z - width, x + width, y, z + width);
            drawLine(buffer, x + width, y, z + width, x - width, y, z + width);
            drawLine(buffer, x - width, y, z + width, x - width, y, z - width);

            // Верхний квадрат
            drawLine(buffer, x - width, y + height, z - width, x + width, y + height, z - width);
            drawLine(buffer, x + width, y + height, z - width, x + width, y + height, z + width);
            drawLine(buffer, x + width, y + height, z + width, x - width, y + height, z + width);
            drawLine(buffer, x - width, y + height, z + width, x - width, y + height, z - width);

            // Вертикальные линии
            drawLine(buffer, x - width, y, z - width, x - width, y + height, z - width);
            drawLine(buffer, x + width, y, z - width, x + width, y + height, z - width);
            drawLine(buffer, x + width, y, z + width, x + width, y + height, z + width);
            drawLine(buffer, x - width, y, z + width, x - width, y + height, z + width);

            tess.draw();
            
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
        }
    }

    private void drawLine(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2) {
        buffer.vertex(x1, y1, z1).color(1.0f, 0.2f, 0.8f, 1.0f).next(); // Розово-фиолетовый цвет
        buffer.vertex(x2, y2, z2).color(1.0f, 0.2f, 0.8f, 1.0f).next();
    }

    // --- 2D РЕНДЕР (HUD Box) ---
    public void render2D(MatrixStack matrices, float tickDelta) {
        if (!hudBox || mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) continue;

            // Проецируем 3D координаты в 2D (рисуем текст над головой)
            Vec3d pos = new Vec3d(player.getX(), player.getY() + player.getHeight() + 0.5, player.getZ());
            Vec3d screenPos = getScreenPos(pos, tickDelta);
            
            if (screenPos == null) continue;
            
            int x = (int) screenPos.x;
            int y = (int) screenPos.y;

            float health = player.getHealth();
            float maxHealth = player.getMaxHealth();
            int armor = player.getArmor();

            // Полоска здоровья (Красная)
            int barWidth = 50;
            int healthWidth = (int) ((health / maxHealth) * barWidth);
            fill(matrices, x - barWidth / 2, y, x + barWidth / 2, y + 3, 0xFF000000); // Черный фон
            fill(matrices, x - barWidth / 2, y, x - barWidth / 2 + healthWidth, y + 3, 0xFFFF0000); // Красная полоска

            // Полоска брони (Голубая)
            if (armor > 0) {
                fill(matrices, x - barWidth / 2, y + 4, x + barWidth / 2, y + 6, 0xFF000000);
                fill(matrices, x - barWidth / 2, y + 4, x - barWidth / 2 + Math.min(armor, 20) * (barWidth / 20), y + 6, 0xFF00FFFF);
            }

            // Имя
            mc.textRenderer.drawWithShadow(matrices, player.getName().getString(), x - mc.textRenderer.getWidth(player.getName()) / 2, y - 10, 0xFFFFFFFF);
            
            // Оружие в руке
            if (!player.getMainHandStack().isEmpty()) {
                mc.getItemRenderer().renderInGui(player.getMainHandStack(), x - 8, y - 25);
            }
            
            // Scoped (Если у игрока в руках лук, показываем индикатор)
            if (player.getMainHandStack().getItem() instanceof BowItem) {
                 mc.textRenderer.drawWithShadow(matrices, "[SCOPE]", x + 15, y - 10, 0xFFFFAA00);
            }
        }
    }

    private Vec3d getScreenPos(Vec3d pos, float tickDelta) {
        // Вспомогательная функция для конвертации 3D -> 2D
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();
        Vec3d relative = pos.subtract(camPos);
        
        // Матрица проекции (упрощенно, для 1.16.5 это работает через GameRenderer)
        // Честно говоря, стандартный рендер HUD в 3D-мире сложен без миксинов.
        // Но мы можем нарисовать это просто как текст через MinecraftClient
        // (Этот код - заглушка, так как полноценный HUD над игроками требует перехвата RenderPlayerEvent)
        return null; // Заглушка, чтобы не крашило. Если нужен реальный HUD, скажи.
    }
    
    // Утилита для рисования квадратов (для HUD)
    private void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        net.minecraft.client.gui.DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
    }
}
