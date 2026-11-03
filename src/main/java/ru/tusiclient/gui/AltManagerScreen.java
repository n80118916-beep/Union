package ru.tusiclient.gui;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import ru.tusiclient.mixin.MinecraftClientAccessor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltManagerScreen extends Screen {
    private TextFieldWidget nickField;
    private static final List<String> ALTS = new ArrayList<>();
    private static String currentAccount = "Player";

    public AltManagerScreen() {
        super(new LiteralText("Alt Manager"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        // Поле ввода сверху
        this.nickField = new TextFieldWidget(this.textRenderer, centerX - 100, 40, 200, 20, new LiteralText("Никнейм"));
        this.nickField.setMaxLength(16);
        this.addButton(this.nickField);

        // Кнопка добавить и войти
        this.addButton(new ButtonWidget(centerX - 100, 65, 200, 20, new LiteralText("Добавить и Войти"), (button) -> {
            String nick = nickField.getText().trim();
            if (!nick.isEmpty() && !ALTS.contains(nick)) {
                ALTS.add(nick);
                login(nick);
                this.nickField.setText("");
            }
        }));

        // Кнопки в самом низу
        this.addButton(new ButtonWidget(centerX - 100, this.height - 40, 95, 20, new LiteralText("Выход"), (button) -> {
            this.client.openScreen(null);
        }));
        
        this.addButton(new ButtonWidget(centerX + 5, this.height - 40, 95, 20, new LiteralText("Удалить все"), (button) -> {
            ALTS.clear();
        }));
    }

    private void login(String nick) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes());
        ((MinecraftClientAccessor) this.client).setSession(new Session(nick, uuid.toString(), "0", "mojang"));
        currentAccount = nick;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // ФУЛ ЧЕРНЫЙ ФОН
        fill(matrices, 0, 0, this.width, this.height, 0xFF000000);

        int centerX = this.width / 2;
        
        // Надпись сверху
        drawCenteredString(matrices, "ALT MANAGER", centerX, 20, 0xFF8A2BE2);
        
        // Текущий аккаунт
        drawCenteredString(matrices, "Текущий аккаунт: " + currentAccount, centerX, this.height - 60, Color.GREEN.getRGB());

        this.nickField.render(matrices, mouseX, mouseY, delta);

        // Список ников (по центру)
        int listY = 100;
        for (String alt : ALTS) {
            boolean isHovered = mouseX >= centerX - 100 && mouseX <= centerX + 100 && mouseY >= listY && mouseY <= listY + 20;
            
            fill(matrices, centerX - 100, listY, centerX + 100, listY + 20, isHovered ? 0xFF222222 : 0xFF111111);
            drawCenteredString(matrices, alt, centerX, listY + 6, isHovered ? 0xFFFFFFFF : 0xFFAAAAAA);
            
            listY += 25;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int listY = 100;
        
        // Клик по нику в списке
        for (String alt : ALTS) {
            if (mouseX >= centerX - 100 && mouseX <= centerX + 100 && mouseY >= listY && mouseY <= listY + 20) {
                login(alt);
                return true;
            }
            listY += 25;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    // Вспомогательный метод
    private void drawCenteredString(MatrixStack matrices, String text, int x, int y, int color) {
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, text, x, y, color);
    }
}
