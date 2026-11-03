package ru.tusiclient.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import ru.tusiclient.module.Module;

public class WingsModule extends Module {
    private static final float DEFAULT_SPREAD = 8.0f;
    private static final int DEFAULT_ALPHA = 220;
    private static final WingPoint[] SHAPE = {
            new WingPoint(0.08f, 0.10f, 0.88f), new WingPoint(0.28f, 0.34f, 0.78f),
            new WingPoint(0.56f, 0.82f, 0.62f), new WingPoint(0.86f, 0.30f, 0.52f),
            new WingPoint(1.14f, 0.46f, 0.40f), new WingPoint(1.24f, 0.04f, 0.30f),
            new WingPoint(1.02f, -0.18f, 0.28f), new WingPoint(1.18f, -0.64f, 0.22f),
            new WingPoint(0.86f, -0.46f, 0.20f), new WingPoint(0.80f, -0.98f, 0.14f),
            new WingPoint(0.54f, -0.74f, 0.16f), new WingPoint(0.30f, -1.16f, 0.12f),
            new WingPoint(0.10f, -0.54f, 0.18f)
    };
    private float selfBodyYaw;
    private boolean selfBodyYawInitialized;

    public WingsModule() {
        super("Wings", "Render");
    }

    public void render3D(MatrixStack matrices, Camera camera, float tickDelta) {
        if (!enabled || mc.player == null || mc.world == null) return;

        Vec3d cameraPos = camera.getPos();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                if (player != mc.player && !player.isAlive()) continue;
                
                renderWings(matrices, player, tickDelta, cameraPos);
            }
        }

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private void renderWings(MatrixStack stack, PlayerEntity player, float tickDelta, Vec3d camera) {
        double x = MathHelper.lerp(tickDelta, player.prevX, player.getX()) - camera.x;
        double y = MathHelper.lerp(tickDelta, player.prevY, player.getY()) - camera.y;
        double z = MathHelper.lerp(tickDelta, player.prevZ, player.getZ()) - camera.z;

        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        float move = MathHelper.clamp(player.limbDistance - player.limbSwingAmount, 0f, 1f);
        
        float flap = (float) Math.sin((player.age + tickDelta) * 0.12f) * 0.18f;
        float open = DEFAULT_SPREAD + flap + move * 4.5f;
        float scale = 1.0f;

        int baseColor = 0xFF8A2BE2;
        int glowColor = 0xFFFFFF;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(180f - bodyYaw, 0f, 1f, 0f);
        GL11.glTranslated(0f, 1.38f, 0.10f);
        GL11.glScalef(scale, scale, scale);

        renderWingSide(stack, -1f, open, baseColor, glowColor);
        renderWingSide(stack, 1f, open, baseColor, glowColor);
        GL11.glPopMatrix();
    }

    private void renderWingSide(MatrixStack stack, float side, float open, int baseColor, int glowColor) {
        GL11.glPushMatrix();
        GL11.glTranslated(side * 0.18f, 0f, 0.06f);
        GL11.glRotatef(side * open, 0f, 1f, 0f);
        GL11.glRotatef(side * -11f, 0f, 0f, 1f);
        GL11.glRotatef(-4f, 1f, 0f, 0f);

        drawWingLayer(stack, side, 1.0f, setAlpha(baseColor, 180), setAlpha(baseColor, 10));
        drawWingOutline(stack, side, 1.0f, setAlpha(glowColor, 120));
        drawWingRibs(stack, side, 0.96f, setAlpha(glowColor, 50));

        GL11.glPopMatrix();
    }

    private void drawWingLayer(MatrixStack stack, float side, float scale, int rootColor, int edgeColor) {
        Matrix4f matrix = stack.peek().getModel();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        // ИСПОЛЬЗУЕМ ПОЛНЫЙ ПУТЬ VertexFormat.DrawMode
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < SHAPE.length; i++) {
            WingPoint cur = SHAPE[i];
            WingPoint next = SHAPE[(i + 1) % SHAPE.length];
            vertex(buffer, matrix, 0f, 0f, 0f, rootColor);
            vertex(buffer, matrix, side * cur.x * scale, cur.y * scale, 0f, applyPointAlpha(edgeColor, cur.alphaMul));
            vertex(buffer, matrix, side * next.x * scale, next.y * scale, 0f, applyPointAlpha(edgeColor, next.alphaMul));
        }
        tessellator.draw();
    }

    private void drawWingOutline(MatrixStack stack, float side, float scale, int color) {
        Matrix4f matrix = stack.peek().getModel();
        RenderSystem.lineWidth(1.35f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);

        for (WingPoint point : SHAPE)
            vertex(buffer, matrix, side * point.x * scale, point.y * scale, 0f, color);
        vertex(buffer, matrix, side * SHAPE[0].x * scale, SHAPE[0].y * scale, 0f, color);
        tessellator.draw();
    }

    private void drawWingRibs(MatrixStack stack, float side, float scale, int color) {
        Matrix4f matrix = stack.peek().getModel();
        int[] ribIndices = {2, 4, 7, 9, 11};
        RenderSystem.lineWidth(0.9f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

        for (int idx : ribIndices) {
            WingPoint point = SHAPE[idx];
            vertex(buffer, matrix, 0f, 0f, 0f, setAlpha(color, Math.max(8, (int)(alpha(color) * 0.75f))));
            vertex(buffer, matrix, side * point.x * scale, point.y * scale, 0f, applyPointAlpha(color, point.alphaMul));
        }
        tessellator.draw();
    }

    private int applyPointAlpha(int color, float multiplier) { return setAlpha(color, Math.max(0, Math.min(255, (int)(alpha(color) * multiplier)))); }
    private static int setAlpha(int color, int a) { return (MathHelper.clamp(a, 0, 255) << 24) | (color & 0x00FFFFFF); }
    private static int alpha(int color) { return (color >> 24) & 0xFF; }
    private static int red(int color) { return (color >> 16) & 0xFF; }
    private static int green(int color) { return (color >> 8) & 0xFF; }
    private static int blue(int color) { return color & 0xFF; }

    private void vertex(BufferBuilder buffer, Matrix4f matrix, float x, float y, float z, int color) {
        buffer.vertex(matrix, x, y, z).color(red(color) / 255f, green(color) / 255f, blue(color) / 255f, alpha(color) / 255f).next();
    }

    private static final class WingPoint {
        final float x, y, alphaMul;
        WingPoint(float x, float y, float alphaMul) { this.x = x; this.y = y; this.alphaMul = alphaMul; }
    }
            }
