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
    private final int COLOR_BG = new Color(15, 15, 20, 245).getRGB();
    private final int COLOR_SIDE = new Color(25, 25, 32, 245).getRGB();
    private final int COLOR_ACCENT = new Color(138, 43, 226).getRGB();
    private final int COLOR_TEXT = new Color(240, 240, 240).getRGB();
    private final int COLOR_MOD_OFF = new Color(35, 35, 42, 255).getRGB();

    private int x, y;
    private final int width = 420;
    private final int height = 260;
    private final int sidebarWidth = 120;

    private String category = "Combat";
    private final List<String> categories = Arrays.asList("Combat", "Movement", "Render");

    public ClickGUI() { super(new LiteralText("TusiClient")); }

    @Override
    protected void init() {
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, new Color(0, 0, 0, 140).getRGB());
        
        // Тень
        fill(matrices, x - 2, y - 2, x + width + 2, y + height + 2, new Color(0, 0, 0, 80).getRGB());
        
        // Основа и Сайдбар
        fill(matrices, x, y, x + width, y + height, COLOR_BG);
        fill(matrices, x, y, x + sidebarWidth, y + height, COLOR_SIDE);
        fill(matrices, x + sidebarWidth, y, x + sidebarWidth + 2, y + height, COLOR_ACCENT);

        // Логотип
        fill(matrices, x + 15, y + 15, x + 35, y + 35, COLOR_ACCENT);
        textRenderer.drawWithShadow(matrices, "TusiClient", x + 45, y + 23, COLOR_TEXT);

        // Вкладки
        int catY = y + 60;
        for (String cat : categories) {
            boolean active = category.equals(cat);
            if (active) fill(matrices, x + 10, catY - 4, x + sidebarWidth - 10, catY + 15, new Color(45, 45, 60).getRGB());
            textRenderer.drawWithShadow(matrices, cat, x + 20, catY, active ? COLOR_TEXT : new Color(130, 130, 140).getRGB());
            catY += 25;
        }

        // Модули (Красивые кнопки с градиентом)
        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;

            int mX = modX + (col * 150);
            int mY = modY;

            // Фон кнопки
            if (mod.enabled) {
                // Включенный модуль - градиент фиолетовый -> синий
                fillGradient(matrices, mX, mY, mX + 140, mY + 35, new Color(138, 43, 226).getRGB(), new Color(75, 0, 130).getRGB());
            } else {
                // Выключенный - темно-серый
                fill(matrices, mX, mY, mX + 140, mY + 35, COLOR_MOD_OFF);
            }

            // Рамка
            drawBorder(matrices, mX, mY, 140, 35, mod.enabled ? new Color(180, 100, 255).getRGB() : new Color(60, 60, 70).getRGB());

            // Название
            textRenderer.drawWithShadow(matrices, mod.name, mX + 10, mY + 13, mod.enabled ? Color.WHITE.getRGB() : new Color(160, 160, 160).getRGB());

            // Тумблер (Вкл/Выкл)
            int toggleX = mX + 110;
            int toggleY = mY + 14;
            fill(matrices, toggleX, toggleY, toggleX + 20, toggleY + 8, mod.enabled ? Color.WHITE.getRGB() : new Color(80, 80, 90).getRGB());
            
            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Клики по вкладкам
        int catY = y + 60;
        for (String cat : categories) {
            if (mouseX >= x + 10 && mouseX <= x + sidebarWidth - 10 && mouseY >= catY - 4 && mouseY <= catY + 15) {
                category = cat; return true;
            }
            catY += 25;
        }

        // Клики по модулям
        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;

            int mX = modX + (col * 150);
            int mY = modY;

            if (mouseX >= mX && mouseX <= mX + 140 && mouseY >= mY && mouseY <= mY + 35) {
                mod.toggle(); return true;
            }

            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_T) {
            this.client.openScreen(new ChatScreen(""));
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.openScreen(null); return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    private void drawBorder(MatrixStack matrices, int x, int y, int w, int h, int color) {
        fill(matrices, x, y, x + w, y + 1, color);
        fill(matrices, x, y + h - 1, x + w, y + h, color);
        fill(matrices, x, y, x + 1, y + h, color);
        fill(matrices, x + w - 1, y, x + w, y + h, color);
    }
}
