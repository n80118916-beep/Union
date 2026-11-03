package ru.tusiclient.module.impl;

import ru.tusiclient.module.Module;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", "Movement");
        this.enabled = true;
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.player.forwardSpeed > 0 && !mc.player.isSneaking()) {
            mc.player.setSprinting(true);
        }
    }
}
