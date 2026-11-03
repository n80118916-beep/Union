package ru.tusiclient.module.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.tusiclient.module.Module;

import java.util.Random;

public class SpookyAura extends Module {

    private final Random random = new Random();
    private long lastAttackTime = 0;
    private long nextAttackDelay = 100; // Динамическая задержка

    // Настройки для обхода (можно вынести в GUI, но пока захардкодим)
    private final float range = 3.2f; // Дистанция удара (безопасно для 1.16.5)
    private final float maxTurnSpeed = 35.0f; // Максимальная скорость поворота (чтобы не палить Matrix)
    
    private LivingEntity target = null;
    private float silentYaw = 0;
    private float silentPitch = 0;

    public SpookyAura() {
        super("SpookyAura", "Combat");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // 1. Поиск цели
        target = findTarget();
        
        if (target != null) {
            // 2. Вычисляем идеальный угол для удара
            float[] idealRotations = calculateRotations(target);
            
            // 3. Плавно поворачиваем "серверную" ротацию
            silentYaw = smoothRotation(silentYaw, idealRotations[0], maxTurnSpeed);
            silentPitch = smoothRotation(silentPitch, idealRotations[1], maxTurnSpeed);

            // 4. Отправляем поворот на сервер (Silent Rotation)
            // В 1.16.5 это делается через пакет, но для простоты мы просто меняем rotationYaw на время удара
            // В идеале нужно использовать PlayerMoveC2SPacket.LookOnly
            
            // 5. Проверяем видимость и бьем
            if (canSeeTarget(target) && System.currentTimeMillis() - lastAttackTime >= nextAttackDelay) {
                // Меняем ротацию чисто для сервера
                float oldYaw = mc.player.yaw;
                float oldPitch = mc.player.pitch;
                
                mc.player.yaw = silentYaw;
                mc.player.pitch = silentPitch;
                
                // Удар
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                
                // Возвращаем старую ротацию (чтобы камера не дёргалась)
                mc.player.yaw = oldYaw;
                mc.player.pitch = oldPitch;

                lastAttackTime = System.currentTimeMillis();
                
                // Рандомизация CPS: от 90 до 130 мс (примерно 8-11 CPS)
                nextAttackDelay = 90 + random.nextInt(40);
            }
        }
    }

    private LivingEntity findTarget() {
        LivingEntity bestTarget = null;
        double bestDist = range;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player && ((LivingEntity) entity).isAlive()) {
                // Ищем только игроков (или мобов, если нужно)
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

    // Плавный поворот (имитация человеческой руки)
    private float smoothRotation(float current, float target, float maxDelta) {
        float delta = MathHelper.wrapDegrees(target - current);
        delta = MathHelper.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }

    // Проверка, видит ли игрок цель (Raytrace)
    private boolean canSeeTarget(Entity target) {
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
        
        // Простая проверка на пересечение лучей (можно улучшить)
        EntityHitResult hitResult = mc.world.raycast(new net.minecraft.world.RaycastContext(
                eyePos, targetPos, net.minecraft.world.RaycastContext.ShapeType.COLLIDER, 
                net.minecraft.world.RaycastContext.FluidHandling.NONE, mc.player
        ));
        
        // Если луч попал в цель или ни во что не попал (значит, путь свободен)
        return hitResult == null || hitResult.getEntity() == target;
    }
}
