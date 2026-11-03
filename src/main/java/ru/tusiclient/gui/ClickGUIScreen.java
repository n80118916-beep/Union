package ru.tusiclient.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickGUIScreen extends Screen {
    private final Minecraft mc = Minecraft.getInstance();
    private final List<String> categories = Arrays.asList("Combat", "Render", "Player", "Misc");
    private String currentCategory = "Combat";
    
    // Визуал
    private final int COLOR_BG = new Color(15, 15, 20, 240).getRGB();
    private final int COLOR_SIDE = new Color(25, 25, 32, 240).getRGB();
    private final int COLOR_ACCENT = new Color(155, 89, 255, 255).getRGB();
    private final int COLOR_TEXT = new Color(250, 250, 250, 255).getRGB();
    private final int COLOR_MOD = new Color(35, 35, 45, 255).getRGB();

    private int x, y;
    private final int width = 450, height = 280;

    public ClickGUIScreen() {
        super(new StringTextComponent("TusiClient"));
    }

    @Override
    protected void init() {
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        // Задний фон игры
        fill(matrix, 0, 0, this.width, this.height, new Color(0, 0, 0, 150).getRGB());

        // Главное окно
        fill(matrix, x, y, x + width, y + height, COLOR_BG);
        // Сайдбар
        fill(matrix, x, y, x + 120, y + height, COLOR_SIDE);
        // Полоска
        fill(matrix, x + 120, y, x + 122, y + height, COLOR_ACCENT);

        // Логотип
        fill(matrix, x + 15, y + 15, x + 35, y + 35, COLOR_ACCENT);
        mc.fontRenderer.drawStringWithShadow(matrix, "TusiClient", x + 45, y + 23, COLOR_TEXT);

        // Категории
        int catY = y + 60;
        for (String cat : categories) {
            boolean active = currentCategory.equals(cat);
            if (active) fill(matrix, x + 10, catY - 2, x + 110, catY + 15, COLOR_ACCENT);
            mc.fontRenderer.drawStringWithShadow(matrix, cat, x + 20, catY + 2, active ? -1 : new Color(160, 160, 170).getRGB());
            catY += 25;
        }

        // Модули (заглушки)
        int modX = x + 140;
        int modY = y + 20;
        for (int i = 0; i < 4; i++) {
            fill(matrix, modX, modY, modX + 140, modY + 35, COLOR_MOD);
            drawBorder(matrix, modX, modY, 140, 35, new Color(80, 80, 90).getRGB());
            mc.fontRenderer.drawStringWithShadow(matrix, "Module " + (i+1), modX + 8, modY + 8, COLOR_TEXT);
            modY += 45;
        }

        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int catY = y + 60;
        for (String cat : categories) {
            if (mouseX >= x + 10 && mouseX <= x + 110 && mouseY >= catY - 2 && mouseY <= catY + 15) {
                currentCategory = cat;
                return true;
            }
            catY += 25;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Исправление чата: если нажать T, меню закроется и откроется чат
        if (keyCode == 84) { // 'T'
            this.mc.displayGuiScreen(null); // Закрываем GUI
            return false; // Пропускаем нажатие дальше, чтобы открылся чат
        }
        if (keyCode == 256) { // ESC
            this.mc.displayGuiScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawBorder(MatrixStack matrix, int x, int y, int w, int h, int color) {
        fill(matrix, x, y, x + w, y + 1, color);
        fill(matrix, x, y + h - 1, x + w, y + h, color);
        fill(matrix, x, y, x + 1, y + h, color);
        fill(matrix, x + w - 1, y, x + w, y + h, color);
    }
}
