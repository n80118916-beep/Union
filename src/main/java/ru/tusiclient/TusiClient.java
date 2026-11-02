package ru.tusiclient;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraft.client.gui.GuiMainMenu;

@Mod(modid = "tusiclient", name = "TusiClient", version = "1.0")
public class TusiClient {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Регистрируем события
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        // Если игра пытается открыть стандартное меню, мы его заменяем на наше
        if (event.getGui() instanceof GuiMainMenu) {
            event.setGui(new CustomMainMenu());
        }
    }
}
