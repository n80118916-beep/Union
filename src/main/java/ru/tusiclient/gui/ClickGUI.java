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
    private final int COLOR_BG = new Color(15, 15, 20, 250).getRGB();
    private final int COLOR_SIDE = new Color(25, 25, 32, 250).getRGB();
    private final int COLOR_ACCENT = new Color(138, 43, 226).getRGB(); 
    private final int COLOR_TEXT = new Color(240, 240, 240).getRGB();
    private final int COLOR_MOD_OFF = new Color(35, 35, 42, 255).getRGB();

    private int x, y;
    private final int width = 450;
    private final int height = 260;
    private final int sidebarWidth = 120;

    private String category = "Combat";
    private final List<String> categories = Arrays.asList("Combat", "Movement");

    public ClickGUI() { super(new LiteralText("TusiClient")); }

    @Override
    protected void init() {
        this.x = (this.width - width) / 2;
        this.y = (this.height - height) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, 0, 0, this.width, this.height, new Color(0, 0, 0, 120).getRGB());
        
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
            if (active) fill(matrices, x + 10, catY - 4, x + sidebarWidth - 10, catY + 15, new Color(40, 40, 50).getRGB());
            textRenderer.drawWithShadow(matrices, cat, x + 20, catY, active ? COLOR_TEXT : new Color(130, 130, 140).getRGB());
            catY += 25;
        }

        // Модули
        int modX = x + sidebarWidth + 20;
        int modY = y + 20;
        
        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;

            // Рисуем кнопку
            fill(matrices, modX, modY, modX + 160, modY + 35, mod.enabled ? COLOR_ACCENT : COLOR_MOD_OFF);
            
            // Текст
            textRenderer.drawWithShadow(matrices, mod.name, modX + 10, modY + 13, mod.enabled ? Color.WHITE.getRGB() : new Color(160,160,160).getRGB());
            
            // Индикатор вкл/выкл
            if (mod instanceof ru.tusiclient.module.impl.NeuroAura) {
                ru.tusiclient.module.impl.NeuroAura aura = (ru.tusiclient.module.impl.NeuroAura) mod;
                String status = aura.isTraining ? "Учится..." : (aura.isTrained ? "Готова" : "Не обучена");
                textRenderer.drawWithShadow(matrices, status, modX + 10, modY + 23, Color.YELLOW.getRGB());
            }
            
            modY += 45;
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int catY = y + 60;
        for (String cat : categories) {
            if (mouseX >= x + 10 && mouseX <= x + sidebarWidth - 10 && mouseY >= catY - 4 && mouseY <= catY + 15) {
                category = cat; return true;
            }
            catY += 25;
        }

        int modY = y + 20;
        for (Module mod : ModuleManager.modules) {
            if (!mod.category.equals(category)) continue;
            if (mouseX >= x + sidebarWidth + 20 && mouseX <= x + sidebarWidth + 180 && mouseY >= modY && mouseY <= modY + 35) {
                mod.toggle(); return true;
            }
            modY += 45;
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
            this.client.openScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
