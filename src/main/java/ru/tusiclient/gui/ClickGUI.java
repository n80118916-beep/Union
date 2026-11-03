package ru.tusiclient.gui;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import ru.tusiclient.module.Module;
import ru.tusiclient.module.ModuleManager;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class ClickGUI extends Screen {
    // Цветовая палитра (Премиум фиолетовый)
    private final int COLOR_BG = new Color(15, 15, 20, 240).getRGB();
    private final int COLOR_SIDE = new Color(22, 22, 28, 240).getRGB();
    private final int COLOR_ACCENT = new Color(138, 43, 226).getRGB();
    private final int COLOR_TEXT = new Color(240, 240, 240).getRGB();
    private final int COLOR_MOD_ON = new Color(50, 30, 80, 255).getRGB();
    private final int COLOR_MOD_OFF = new Color(30, 30, 38, 255).getRGB();

    private int x, y;
    private final int width = 420;
    private final int height = 260;
    private final int sidebarWidth = 110;

    private String currentCategory = "Movement";
    private final List<String> categories = Arrays.asList("Combat", "Movement", "Render", "Player");

    public ClickGUI() {
        super(new LiteralText("TusiClient"));
    }

    @Override
    protected void init() {
        // Центрируем меню ровно по середине экрана
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // Затемнение фона
        fill(matrices, 0, 0, this.width, this.height, new Color(0, 0, 0, 150).getRGB());

        // Тень позади меню
        fill(matrices, x - 2, y - 2, x + width + 2, y + height + 2, new Color(0, 0, 0, 100).getRGB());
        
        // Основной фон
        fill(matrices, x, y, x + width, y + height, COLOR_BG);
        // Сайдбар
        fill(matrices, x, y, x + sidebarWidth, y + height, COLOR_SIDE);
        
        // Акцентная полоска
        fill(matrices, x + sidebarWidth, y, x + sidebarWidth + 2, y + height, COLOR_ACCENT);

        // Заголовок
        textRenderer.drawWithShadow(matrices, "TusiClient", x + 15, y + 15, COLOR_ACCENT);
        textRenderer.drawWithShadow(matrices, "v1.0", x + 15, y + 28, new Color(120, 120, 120).getRGB());
        fill(matrices, x + 15, y + 40, x + sidebarWidth - 15, y + 41, new Color(50, 50, 60).getRGB());

        // Отрисовка вкладок
        int catY = y + 55;
        for (String cat : categories) {
            boolean active = currentCategory.equals(cat);
            if (active) fill(matrices, x + 5, catY - 4, x + sidebarWidth - 5, catY + 14, new Color(40, 40, 50).getRGB());
            textRenderer.drawWithShadow(matrices, cat, x + 15, catY, active ? COLOR_TEXT : new Color(130, 130, 140).getRGB());
            catY += 25;
        }

        // Отрисовка модулей
        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(currentCategory)) continue;

            int mX = modX + (col * 150);
            int mY = modY;

            // Кнопка модуля
            fill(matrices, mX, mY, mX + 140, mY + 35, mod.enabled ? COLOR_MOD_ON : COLOR_MOD_OFF);
            // Рамка кнопки
            drawBorder(matrices, mX, mY, 140, 35, mod.enabled ? COLOR_ACCENT : new Color(50, 50, 60).getRGB());

            // Название модуля
            textRenderer.drawWithShadow(matrices, mod.name, mX + 10, mY + 8, mod.enabled ? COLOR_TEXT : new Color(150, 150, 150).getRGB());

            // Тумблер (Вкл/Выкл)
            int toggleX = mX + 115;
            int toggleY = mY + 14;
            fill(matrices, toggleX, toggleY, toggleX + 18, toggleY + 8, mod.enabled ? COLOR_ACCENT : new Color(60, 60, 70).getRGB());
            fill(matrices, toggleX + (mod.enabled ? 10 : 1), toggleY - 2, toggleX + (mod.enabled ? 18 : 9), toggleY + 10, Color.WHITE.getRGB());

            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Клики по вкладкам
        int catY = y + 55;
        for (String cat : categories) {
            if (mouseX >= x + 5 && mouseX <= x + sidebarWidth - 5 && mouseY >= catY - 4 && mouseY <= catY + 14) {
                currentCategory = cat;
                return true;
            }
            catY += 25;
        }

        // Клики по модулям (включение/выключение)
        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(currentCategory)) continue;

            int mX = modX + (col * 150);
            int mY = modY;

            if (mouseX >= mX && mouseX <= mX + 140 && mouseY >= mY && mouseY <= mY + 35) {
                mod.toggle(); // Включаем/выключаем функцию
                return true;
            }

            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ФИКС ЧАТА: При нажатии T открываем чат напрямую
        if (keyCode == GLFW.GLFW_KEY_T) {
            this.client.openScreen(new ChatScreen(""));
            return true;
        }
        // Закрытие на Escape
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.openScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Игра не встает на паузу
    }

    // Рисуем рамку вокруг кнопки
    private void drawBorder(MatrixStack matrices, int x, int y, int w, int h, int color) {
        fill(matrices, x, y, x + w, y + 1, color);
        fill(matrices, x, y + h - 1, x + w, y + h, color);
        fill(matrices, x, y, x + 1, y + h, color);
        fill(matrices, x + w - 1, y, x + w, y + h, color);
    }
}
