package ru.tusiclient.module.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.tusiclient.module.Module;

import java.util.Random;

public class SpookyAura extends Module {

    private final Random random = new Random();
    private long lastAttackTime = 0;
    private long nextAttackDelay = 100;

    private final float range = 3.2f;
    private final float maxTurnSpeed = 35.0f;
    
    private LivingEntity target = null;
    private float silentYaw = 0;
    private float silentPitch = 0;

    public SpookyAura() {
        super("SpookyAura", "Combat");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        target = findTarget();
        
        if (target != null) {
            float[] idealRotations = calculateRotations(target);
            
            silentYaw = smoothRotation(silentYaw, idealRotations[0], maxTurnSpeed);
            silentPitch = smoothRotation(silentPitch, idealRotations[1], maxTurnSpeed);

            if (canSeeTarget(target) && System.currentTimeMillis() - lastAttackTime >= nextAttackDelay) {
                float oldYaw = mc.player.yaw;
                float oldPitch = mc.player.pitch;
                
                mc.player.yaw = silentYaw;
                mc.player.pitch = silentPitch;
                
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                
                mc.player.yaw = oldYaw;
                mc.player.pitch = oldPitch;

                lastAttackTime = System.currentTimeMillis();
                nextAttackDelay = 90 + random.nextInt(40);
            }
        }
    }

    private LivingEntity findTarget() {
        LivingEntity bestTarget = null;
        double bestDist = range;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player && ((LivingEntity) entity).isAlive()) {
                if (!(entity instanceof PlayerEntity)) continue;

                double dist = mc.player.distanceTo(entity);
                if (dist <= bestDist) {
                    bestDist = dist;
                    bestTarget = (LivingEntity) entity;
                }
            }
        }
        return bestTarget;
    }

    private float[] calculateRotations(Entity entity) {
        double diffX = entity.getX() - mc.player.getX();
        double diffY = entity.getY() + entity.getEyeHeight(entity.getPose()) / 2 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = entity.getZ() - mc.player.getZ();
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return new float[]{yaw, pitch};
    }

    private float smoothRotation(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        delta = MathHelper.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }

    private boolean canSeeTarget(Entity target) {
        return mc.player.canSee(target);
    }
}
