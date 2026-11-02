package ru.tusiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    
    private static final Identifier BACKGROUND = new Identifier("tusiclient", "textures/gui/background.png");

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.buttons.clear();
        this.children.clear();

        int x = this.width / 2 - 150;
        int y = this.height / 2 - 40;
        int w = 200;
        int h = 20;

        this.addButton(new ButtonWidget(x, y, w, h, new LiteralText("Одиночная игра"), b -> this.client.openScreen(new SelectWorldScreen(this))));
        this.addButton(new ButtonWidget(x, y + 24, w, h, new LiteralText("Мультиплеер"), b -> this.client.openScreen(new MultiplayerScreen(this))));
        this.addButton(new ButtonWidget(x, y + 48, w, h, new LiteralText("Настройки"), b -> this.client.openScreen(new OptionsScreen(this, this.client.options))));
        this.addButton(new ButtonWidget(x, y + 72, w, h, new LiteralText("Выход"), b -> this.client.scheduleStop()));
    }

    // Внедряемся в метод render, а не renderBackground
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Рисуем картинку на весь экран
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(BACKGROUND);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();

        // Рисуем кнопки
        super.render(matrices, mouseX, mouseY, delta);
        
        // Отменяем стандартный рендер (панораму и т.д.)
        ci.cancel();
    }
}
