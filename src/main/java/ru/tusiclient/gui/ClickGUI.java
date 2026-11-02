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

    private final int COLOR_BG = new Color(15, 15, 20, 230).getRGB();
    private final int COLOR_SIDEBAR = new Color(25, 25, 32, 230).getRGB();
    private final int COLOR_MODULE = new Color(35, 35, 45, 255).getRGB();
    private final int COLOR_ACCENT = new Color(155, 89, 255, 255).getRGB();
    private final int COLOR_TEXT = new Color(250, 250, 250, 255).getRGB();
    private final int COLOR_TEXT_DESC = new Color(160, 160, 170, 255).getRGB();

    private int x, y;
    private final int width = 450;
    private final int height = 280;
    private final int sidebarWidth = 120;

    private String category = "Combat";
    private final List<String> categories = Arrays.asList("Combat", "Render", "Player", "Misc");
    private final List<Module> modules = new ArrayList<>();

    public ClickGUI() {
        super(new LiteralText("TusiClient"));
        modules.add(new Module("KillAura", "Автоматически бьет врагов", "Combat", false));
        modules.add(new Module("Velocity", "Убирает откидывание", "Combat", true));
        modules.add(new Module("ESP", "Подсветка игроков", "Render", false));
        modules.add(new Module("Fullbright", "Максимальная яркость", "Render", true));
        modules.add(new Module("NoFall", "Убирает урон от падения", "Player", false));
        modules.add(new Module("Timer", "Ускоряет время", "Misc", false));
    }

    @Override
    protected void init() {
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, new Color(0, 0, 0, 150).getRGB());
        fill(matrices, x, y, x + width, y + height, COLOR_BG);
        fill(matrices, x, y, x + sidebarWidth, y + height, COLOR_SIDEBAR);
        fill(matrices, x + sidebarWidth, y, x + sidebarWidth + 2, y + height, COLOR_ACCENT);

        fill(matrices, x + 15, y + 15, x + 35, y + 35, COLOR_ACCENT);
        textRenderer.drawWithShadow(matrices, "TusiClient", x + 45, y + 23, COLOR_TEXT);

        int catY = y + 60;
        for (String cat : categories) {
            boolean active = category.equals(cat);
            if (active) {
                fill(matrices, x + 10, catY - 2, x + sidebarWidth - 10, catY + 15, COLOR_ACCENT);
            }
            textRenderer.drawWithShadow(matrices, cat, x + 20, catY + 2, active ? Color.WHITE.getRGB() : COLOR_TEXT_DESC);
            catY += 25;
        }

        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : modules) {
            if (!mod.category.equals(category)) continue;

            int mX = modX + (col * 160);
            int mY = modY;

            fill(matrices, mX, mY, mX + 150, mY + 35, mod.enabled ? COLOR_ACCENT : COLOR_MODULE);
            drawBorder(matrices, mX, mY, 150, 35, new Color(80, 80, 90, 255).getRGB());

            textRenderer.drawWithShadow(matrices, mod.name, mX + 8, mY + 5, COLOR_TEXT);
            textRenderer.drawWithShadow(matrices, mod.description, mX + 8, mY + 18, mod.enabled ? Color.WHITE.getRGB() : COLOR_TEXT_DESC);

            col++;
            if (col >= 2) {
                col = 0;
                modY += 45;
            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int catY = y + 60;
        for (String cat : categories) {
            if (mouseX >= x + 10 && mouseX <= x + sidebarWidth - 10 && mouseY >= catY - 2 && mouseY <= catY + 15) {
                category = cat;
                return true;
            }
            catY += 25;
        }

        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        int col = 0;

        for (Module mod : modules) {
            if (!mod.category.equals(category)) continue;

            int mX = modX + (col * 160);
            int mY = modY;

            if (mouseX >= mX && mouseX <= mX + 150 && mouseY >= mY && mouseY <= mY + 35) {
                mod.enabled = !mod.enabled;
                return true;
            }

            col++;
            if (col >= 2) {
                col = 0;
                modY += 45;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.openScreen(null); // Исправлено: openScreen вместо setScreen
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { // Исправлено: isPauseScreen вместо shouldPause
        return false;
    }

    private void drawBorder(MatrixStack matrices, int x, int y, int width, int height, int color) {
        fill(matrices, x, y, x + width, y + 1, color);
        fill(matrices, x, y + height - 1, x + width, y + height, color);
        fill(matrices, x, y, x + 1, y + height, color);
        fill(matrices, x + width - 1, y, x + width, y + height, color);
    }

    public static class Module {
        public String name;
        public String description;
        public String category;
        public boolean enabled;

        public Module(String name, String description, String category, boolean enabled) {
            this.name = name;
            this.description = description;
            this.category = category;
            this.enabled = enabled;
        }
    }
        }
