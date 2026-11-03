package ru.tusiclient.module;

import net.minecraft.client.MinecraftClient;

public class Module {
    public String name;
    public String category;
    public boolean enabled;
    public static MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, String category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public void onTick() {}
    
    public void toggle() {
        this.enabled = !this.enabled;
    }
}
