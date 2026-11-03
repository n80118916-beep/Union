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
    private final int COLOR_BG = new Color(10, 10, 10, 255).getRGB();
    private final int COLOR_SIDE = new Color(20, 20, 20, 255).getRGB();
    private final int COLOR_ACCENT = new Color(138, 43, 226).getRGB();
    private final int COLOR_TEXT = new Color(240, 240, 240).getRGB();

    private int x, y;
    private final int width = 420, height = 260;
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
        fill(matrices, 0, 0, this.width, this.height, new Color(0, 0, 0, 180).getRGB());
        
        // Основное окно
        fill(matrices, x, y, x + width, y + height, COLOR_BG);
        fill(matrices, x, y, x + 120, y + height, COLOR_SIDE);
        fill(matrices, x + 120, y, x + 122, y + height, COLOR_ACCENT); // Полоска

        // Логотип
        fill(matrices, x + 15, y + 15, x + 35, y + 35, COLOR_ACCENT);
        textRenderer.drawWithShadow(matrices, "TusiClient", x + 45, y + 23, COLOR_TEXT);

        // Категории
        int catY = y + 60;
        for (String cat : categories) {
            boolean active = category.equals(cat);
            if (active) fill(matrices, x + 10, catY - 4, x + 110, catY + 15, COLOR_ACCENT);
            textRenderer.drawWithShadow(matrices, cat, x + 20, catY, active ? COLOR_TEXT : new Color(130, 130, 140).getRGB());
            catY += 25;
        }

        // Модули
        int modX = x + 140;
        int modY = y + 20;
        int col = 0;

        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;
            int mX = modX + (col * 130);
            int mY = modY;

            // Фон кнопки
            if (mod.enabled) {
                fillGradient(matrices, mX, mY, mX + 120, mY + 35, COLOR_ACCENT, new Color(75, 0, 130).getRGB());
            } else {
                fill(matrices, mX, mY, mX + 120, mY + 35, new Color(30, 30, 30).getRGB());
            }
            drawBorder(matrices, mX, mY, 120, 35, mod.enabled ? Color.WHITE.getRGB() : new Color(60,60,60).getRGB());

            textRenderer.drawWithShadow(matrices, mod.name, mX + 10, mY + 13, mod.enabled ? Color.WHITE.getRGB() : new Color(160,160,160).getRGB());
            
            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int catY = y + 60;
        for (String cat : categories) {
            if (mouseX >= x + 10 && mouseX <= x + 110 && mouseY >= catY - 4 && mouseY <= catY + 15) { category = cat; return true; }
            catY += 25;
        }

        int modX = x + 140;
        int modY = y + 20;
        int col = 0;
        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;
            int mX = modX + (col * 130);
            int mY = modY;
            if (mouseX >= mX && mouseX <= mX + 120 && mouseY >= mY && mouseY <= mY + 35) { mod.toggle(); return true; }
            col++;
            if (col >= 2) { col = 0; modY += 45; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_T) { this.client.openScreen(new ChatScreen("")); return true; }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { this.client.openScreen(null); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    private void drawBorder(MatrixStack matrices, int x, int y, int w, int h, int color) {
        fill(matrices, x, y, x + w, y + 1, color); fill(matrices, x, y + h - 1, x + w, y + h, color);
        fill(matrices, x, y, x + 1, y + h, color); fill(matrices, x + w - 1, y, x + w, y + h, color);
    }
}
