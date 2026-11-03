package ru.tusiclient.module.impl;

import org.lwjgl.opengl.GL11; // <--- ВОТ ИМПОРТ
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.math.Vec3d;
import ru.tusiclient.module.Module;

public class RenderModules extends Module {
    
    public boolean esp = true;
    public boolean hudBox = true;

    public RenderModules() {
        super("RenderModules", "Render");
        this.enabled = true;
    }

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
            
            GL11.glLineWidth(1.5f);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
            
            float width = player.getWidth() / 2f;
            float height = player.getHeight();

            drawLine(buffer, x - width, y, z - width, x + width, y, z - width);
            drawLine(buffer, x + width, y, z - width, x + width, y, z + width);
            drawLine(buffer, x + width, y, z + width, x - width, y, z + width);
            drawLine(buffer, x - width, y, z + width, x - width, y, z - width);

            drawLine(buffer, x - width, y + height, z - width, x + width, y + height, z - width);
            drawLine(buffer, x + width, y + height, z - width, x + width, y + height, z + width);
            drawLine(buffer, x + width, y + height, z + width, x - width, y + height, z + width);
            drawLine(buffer, x - width, y + height, z + width, x - width, y + height, z - width);

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
        buffer.vertex(x1, y1, z1).color(1.0f, 0.2f, 0.8f, 1.0f).next();
        buffer.vertex(x2, y2, z2).color(1.0f, 0.2f, 0.8f, 1.0f).next();
    }

    public void render2D(MatrixStack matrices, float tickDelta) {}
    
    private void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        net.minecraft.client.gui.DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
    }
}
