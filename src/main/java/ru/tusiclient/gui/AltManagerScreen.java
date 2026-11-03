package ru.tusiclient.gui;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.awt.Color;
import java.util.UUID;

public class AltManagerScreen extends Screen {
    private TextFieldWidget nickField;
    private String status = "Введите ник и нажмите Войти";

    public AltManagerScreen() {
        super(new LiteralText("Alt Manager"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.nickField = new TextFieldWidget(this.textRenderer, centerX - 100, centerY - 20, 200, 20, new LiteralText("Никнейм"));
        this.nickField.setMaxLength(16);
        this.addButton(this.nickField);

        this.addButton(new ButtonWidget(centerX - 100, centerY + 10, 200, 20, new LiteralText("Войти в аккаунт"), (button) -> {
            String nick = nickField.getText().trim();
            if (!nick.isEmpty()) {
                // Генерируем оффлайн UUID
                UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes());
                // Создаем новую сессию
                this.client.session = new Session(nick, uuid.toString(), "0", "mojang");
                this.status = "Успешно вошли как: " + nick;
                this.nickField.setText("");
            } else {
                this.status = "Никнейм не может быть пустым!";
            }
        }));

        this.addButton(new ButtonWidget(centerX - 100, centerY + 40, 200, 20, new LiteralText("Назад"), (button) -> {
            this.client.openScreen(null); // Закрыть меню
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        fill(matrices, centerX - 120, centerY - 60, centerX + 120, centerY + 90, new Color(20, 20, 25, 255).getRGB());
        drawCenteredString(matrices, "Alt Manager", centerX, centerY - 50, Color.WHITE.getRGB());
        drawCenteredString(matrices, status, centerX, centerY - 35, Color.GREEN.getRGB());

        this.nickField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
