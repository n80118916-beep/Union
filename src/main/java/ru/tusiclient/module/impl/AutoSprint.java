package ru.tusiclient.module.impl;

import ru.tusiclient.module.Module;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", "Movement");
        this.enabled = true; // Включен по умолчанию
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            // Если игрок двигается вперед и не сидит в приседе, включаем бег
            if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking()) {
                mc.player.setSprinting(true);
            }
        }
    }
}
