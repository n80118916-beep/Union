package ru.tusiclient.module.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import ru.tusiclient.module.Module;

public class NeuroAura extends Module {
    public boolean isTraining = false;
    public boolean isTrained = false;
    public long recordedDelay = 500; // Задержка удара по умолчанию
    private long lastRecordedAttack = 0;
    private long lastAttack = 0;

    public NeuroAura() {
        super("NeuroAura", "Combat");
    }

    public void startTraining() {
        isTraining = true;
        mc.player.sendMessage(new net.minecraft.text.LiteralText("§a[NeuroAura] Обучение начато! Бейте мобов/игроков руками."), false);
    }

    public void saveTraining() {
        isTraining = false;
        isTrained = true;
        mc.player.sendMessage(new net.minecraft.text.LiteralText("§a[NeuroAura] Обучение сохранено! Задержка: " + recordedDelay + "ms"), false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Если учимся - записываем тайминги ударов
        if (isTraining) {
            if (mc.options.keyAttack.isPressed() && mc.player.getAttackCooldownProgress(0) >= 0.9f) {
                long now = System.currentTimeMillis();
                if (lastRecordedAttack != 0) {
                    recordedDelay = (recordedDelay + (now - lastRecordedAttack)) / 2;
                }
                lastRecordedAttack = now;
            }
            return;
        }

        // Если включено и обучено - ебашим
        if (!enabled || !isTrained) return;

        LivingEntity target = findTarget(4.5);
        if (target != null) {
            double diffX = target.getX() - mc.player.getX();
            double diffZ = target.getZ() - mc.player.getZ();
            double diffY = target.getY() + target.getEyeHeight(target.getPose()) / 2 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
            double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
            float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
            float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

            mc.player.yaw = yaw;
            mc.player.pitch = pitch;

            if (System.currentTimeMillis() - lastAttack >= recordedDelay) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                lastAttack = System.currentTimeMillis();
            }
        }
    }

    private LivingEntity findTarget(double range) {
        LivingEntity best = null;
        double bestDist = range;
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof LivingEntity && e != mc.player && ((LivingEntity) e).isAlive()) {
                if (!(e instanceof PlayerEntity) && !(e instanceof Monster)) continue;
                double d = mc.player.distanceTo(e);
                if (d < bestDist) {
                    bestDist = d;
                    best = (LivingEntity) e;
                }
            }
        }
        return best;
    }
}
