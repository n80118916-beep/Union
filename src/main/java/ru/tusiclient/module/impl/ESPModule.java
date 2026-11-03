package ru.tusiclient.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import ru.tusiclient.module.Module;

public class ESPModule extends Module {
    public ESPModule() {
        super("ESP", "Render");
    }

    public void render3D(MatrixStack matrices, Camera camera, float tickDelta) {
        if (!enabled || mc.world == null || mc.player == null) return;

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
        // Фиолетовый цвет (как в меню)
        buffer.vertex(x1, y1, z1).color(0.54f, 0.34f, 1.0f, 1.0f).next();
        buffer.vertex(x2, y2, z2).color(0.54f, 0.34f, 1.0f, 1.0f).next();
    }
                     }
