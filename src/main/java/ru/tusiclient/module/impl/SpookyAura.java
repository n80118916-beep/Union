// ... (начало файла без изменений)

    private boolean canSeeTarget(Entity target) {
        // Простая и надежная проверка видимости в Yarn 1.16.5
        return mc.player.canSee(target);
    }

// ...
    // В методе onTick найди строку:
    // Vec3d eyePos = mc.player.getEyePos();
    // Замени её на:
    Vec3d eyePos = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
// ...
