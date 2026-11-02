package ru.tusiclient;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class CustomMainMenu extends GuiScreen {
    
    // Путь к твоей картинке (папка assets/tusiclient/textures/gui/)
    private static final ResourceLocation BACKGROUND = new ResourceLocation("tusiclient", "textures/gui/background.png");

    @Override
    public void initGui() {
        this.buttonList.clear();
        
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 24;
        
        // Смещаем кнопки влево от центра, чтобы не закрывать девушку
        int x = this.width / 2 - 150; 
        int y = this.height / 2 - 40;

        // Добавляем кнопки (ID, X, Y, Ширина, Высота, Текст)
        this.buttonList.add(new GuiButton(0, x, y, buttonWidth, buttonHeight, "Одиночная игра"));
        this.buttonList.add(new GuiButton(1, x, y + spacing, buttonWidth, buttonHeight, "Мультиплеер"));
        this.buttonList.add(new GuiButton(2, x, y + spacing * 2, buttonWidth, buttonHeight, "Настройки"));
        this.buttonList.add(new GuiButton(3, x, y + spacing * 3, buttonWidth, buttonHeight, "Выход"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Растягиваем картинку на весь экран
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
        
        // Рисуем кнопки поверх картинки
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        // Логика нажатия на кнопки
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiSelectWorld(this)); // Одиночная игра
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiMultiplayer(this)); // Мультиплеер
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings)); // Настройки
                break;
            case 3:
                this.mc.shutdown(); // Выход из игры
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
