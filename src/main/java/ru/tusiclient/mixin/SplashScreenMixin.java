package ru.tusiclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashScreen.class)
public class SplashScreenMixin {
    
    private static final Identifier SPLASH_BG = new Identifier("tusiclient", "textures/gui/splash.png");

    // Внедряемся в самом начале отрисовки экрана загрузки
    @Inject(method = "render", at = @At("HEAD"))
    private void drawCustomSplash(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int width = mc.getWindow().getScaledWidth();
        int height = mc.getWindow().getScaledHeight();
        
        // Рисуем картинку на весь экран
        mc.getTextureManager().bindTexture(SPLASH_BG);
        DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, width, height, width, height);
    }
}
