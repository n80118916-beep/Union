package ru.tusiclient.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import ru.tusiclient.module.Module;

public class ESPModule extends Module {
    public ESPModule() {
        super("ESP", "Render");
    }

    public void render3D(MatrixStack stack, Camera camera, float tickDelta) {
        if (!enabled || mc.world == null || mc.player == null) return;

        Vec3d camPos = camera.getPos();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        GL11.glLineWidth(1.5f);

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) continue;

            double x = player.prevX + (player.getX() - player.prevX) * tickDelta - camPos.x;
            double y = player.prevY + (player.getY() - player.prevY) * tickDelta - camPos.y;
            double z = player.prevZ + (player.getZ() - player.prevZ) * tickDelta - camPos.z;

            stack.push();
            stack.translate(x, y, z); // Сдвигаем матрицу прямо к игроку
            Matrix4f matrix = stack.peek().getModel();
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
            
            float width = player.getWidth() / 2f;
            float height = player.getHeight();

            // Координаты теперь рисуются от 0,0,0 (относительно матрицы игрока)
            drawLine(buffer, matrix, -width, 0, -width, width, 0, -width);
            drawLine(buffer, matrix, width, 0, -width, width, 0, width);
            drawLine(buffer, matrix, width, 0, width, -width, 0, width);
            drawLine(buffer, matrix, -width, 0, width, -width, 0, -width);

            drawLine(buffer, matrix, -width, height, -width, width, height, -width);
            drawLine(buffer, matrix, width, height, -width, width, height, width);
            drawLine(buffer, matrix, width, height, width, -width, height, width);
            drawLine(buffer, matrix, -width, height, width, -width, height, -width);

            drawLine(buffer, matrix, -width, 0, -width, -width, height, -width);
            drawLine(buffer, matrix, width, 0, -width, width, height, -width);
            drawLine(buffer, matrix, width, 0, width, width, height, width);
            drawLine(buffer, matrix, -width, 0, width, -width, height, width);

            tessellator.draw();
            stack.pop();
        }
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void drawLine(BufferBuilder buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2) {
        buffer.vertex(matrix, x1, y1, z1).color(0.54f, 0.34f, 1.0f, 1.0f).next();
        buffer.vertex(matrix, x2, y2, z2).color(0.54f, 0.34f, 1.0f, 1.0f).next();
    }
}
