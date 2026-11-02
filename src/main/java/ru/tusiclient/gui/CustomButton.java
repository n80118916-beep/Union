package ru.tusiclient.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CustomButton extends ButtonWidget {
    
    public CustomButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Цвет фона (полупрозрачный черный, при наведении чуть светлее)
        int bgColor = this.isHovered() ? 0xB0000000 : 0x80000000; 
        fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, bgColor);
        
        // Цвет обводки (белая)
        int borderColor = this.isHovered() ? 0xFFFFFFFF : 0xCCCCCCCC; // При наведении ярче
        
        // Рисуем обводку (верх, низ, лево, право)
        fill(matrices, this.x, this.y, this.x + this.width, this.y + 1, borderColor);
        fill(matrices, this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, borderColor);
        fill(matrices, this.x, this.y, this.x + 1, this.y + this.height, borderColor);
        fill(matrices, this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, borderColor);
        
        // Рисуем текст по центру (с тенью)
        String text = this.getMessage().getString();
        int textWidth = mc.textRenderer.getWidth(text);
        mc.textRenderer.drawWithShadow(
            matrices, 
            text, 
            this.x + (this.width - textWidth) / 2f, 
            this.y + (this.height - 8) / 2f, 
            0xFFFFFF // Белый цвет
        );
    }
}
