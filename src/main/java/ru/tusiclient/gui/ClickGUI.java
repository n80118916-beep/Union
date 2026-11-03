package ru.tusiclient.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickGUI extends Screen {
    // Цвета в стиле Aoba (темный, минималистичный)
    private final int COLOR_BG = new Color(20, 20, 25, 240).getRGB();
    private final int COLOR_HEADER = new Color(30, 30, 38, 240).getRGB();
    private final int COLOR_ACCENT = new Color(155, 89, 255, 255).getRGB(); // Фиолетовый
    private final int COLOR_TEXT = new Color(240, 240, 240, 255).getRGB();
    private final int COLOR_MOD = new Color(40, 40, 50, 255).getRGB();

    private int x, y;
    private final int width = 200; // Ширина одного таба
    private final int headerHeight = 25;
    
    // Позиции табов
    private int tab1X, tab1Y;
    private int tab2X, tab2Y;

    private String category1 = "Combat";
    private final List<String> categories = Arrays.asList("Combat", "Render");

    public ClickGUI() {
        super(new LiteralText("Aoba Style"));
    }

    @Override
    protected void init() {
        // Центрируем два таба
        this.tab1X = this.width / 2 - width - 10;
        this.tab1Y = this.height / 2 - 100;
        this.tab2X = this.width / 2 + 10;
        this.tab2Y = this.height / 2 - 100;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // Затемнение
        fill(matrices, 0, 0, this.width, this.height, new Color(0, 0, 0, 120).getRGB());

        // Рисуем два окна (Таба)
        drawTab(matrices, tab1X, tab1Y, "Combat", new String[]{"KillAura", "Velocity", "Critical"});
        drawTab(matrices, tab2X, tab2Y, "Render", new String[]{"ESP", "Fullbright", "Tracers"});

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawTab(MatrixStack matrices, int x, int y, String title, String[] modules) {
        // Шапка окна
        fill(matrices, x, y, x + width, y + headerHeight, COLOR_HEADER);
        fill(matrices, x, y, x + 3, y + headerHeight, COLOR_ACCENT); // Акцентная полоска слева
        textRenderer.drawWithShadow(matrices, title, x + 10, y + 8, COLOR_TEXT);

        // Тело окна
        int currentY = y + headerHeight;
        for (String mod : modules) {
            fill(matrices, x, currentY, x + width, currentY + 20, COLOR_MOD);
            textRenderer.drawWithShadow(matrices, mod, x + 10, currentY + 6, COLOR_TEXT);
            // Разделитель
            fill(matrices, x, currentY + 19, x + width, currentY + 20, new Color(60, 60, 70).getRGB());
            currentY += 20;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Исправление чата: если нажать T, меню закроется
        if (keyCode == GLFW.GLFW_KEY_T) {
            this.client.openScreen(null);
            return false; 
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.openScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
