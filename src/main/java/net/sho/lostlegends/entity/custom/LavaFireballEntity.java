package net.sho.lostlegends.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sho.lostlegends.entity.ModEntities;

public class LavaFireballEntity extends AbstractHurtingProjectile {
    private int explosionPower = 12;
    private static final int FIRE_SECONDS = 8;
    private static final int LAVA_DURATION_TICKS = 100; // 5 seconds

    public LavaFireballEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public LavaFireballEntity(Level level, LivingEntity shooter, double dirX, double dirY, double dirZ) {
        super(ModEntities.LAVA_FIREBALL.get(), shooter, dirX, dirY, dirZ, level);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ExplosionPower", 99)) {
            this.explosionPower = tag.getInt("ExplosionPower");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ExplosionPower", this.explosionPower);
    }

    @Override
    public void tick() {
        super.tick();

        // Spawn lava particles
        if (this.level().isClientSide) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            // Many more particles for the larger fireball
            for (int i = 0; i < 10; i++) {
                // Lava drip particles
                this.level().addParticle(
                        ParticleTypes.FALLING_LAVA,
                        x + (this.random.nextDouble() - 0.5) * 1.5,
                        y + (this.random.nextDouble() - 0.5) * 1.5,
                        z + (this.random.nextDouble() - 0.5) * 1.5,
                        0, 0, 0
                );

                // Flame particles
                this.level().addParticle(
                        ParticleTypes.FLAME,
                        x + (this.random.nextDouble() - 0.5) * 1.5,
                        y + (this.random.nextDouble() - 0.5) * 1.5,
                        z + (this.random.nextDouble() - 0.5) * 1.5,
                        (this.random.nextDouble() - 0.5) * 0.2,
                        (this.random.nextDouble() - 0.5) * 0.2,
                        (this.random.nextDouble() - 0.5) * 0.2
                );
            }

            // Lava particles
            for (int i = 0; i < 3; i++) {
                this.level().addParticle(
                        ParticleTypes.LAVA,
                        x + (this.random.nextDouble() - 0.5) * 0.5,
                        y + (this.random.nextDouble() - 0.5) * 0.5,
                        z + (this.random.nextDouble() - 0.5) * 0.5,
                        0, 0, 0
                );
            }

            // Smoke trail
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        x + (this.random.nextDouble() - 0.5) * 0.8,
                        y + (this.random.nextDouble() - 0.5) * 0.8,
                        z + (this.random.nextDouble() - 0.5) * 0.8,
                        0, 0, 0
                );
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide) {
            // Create explosion
            boolean mobGriefing = this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING);
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.explosionPower, mobGriefing, Level.ExplosionInteraction.MOB);

            // Create a pool of lava around the impact point
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
                createLavaPool(hitPos);
            }

            // Remove the entity
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        if (!this.level().isClientSide) {
            Entity target = hitResult.getEntity();

            // Set the target on fire for longer
            target.setSecondsOnFire(FIRE_SECONDS);
        }
    }

    private void createLavaPool(BlockPos centerPos) {
        // Create a small pool of lava (3x3)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = centerPos.offset(x, 1, z);

                // Only place lava in air blocks
                if (this.level().getBlockState(pos).isAir()) {
                    // Place temporary lava
                    this.level().setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());

                    // Schedule the lava to be removed
                    this.level().scheduleTick(pos, Blocks.LAVA, LAVA_DURATION_TICKS);
                }
            }
        }
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    protected boolean shouldBurn() {
        return true;
    }
}