package net.sho.lostlegends.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sho.lostlegends.entity.ModEntities;

public class LavaFireballEntity extends AbstractHurtingProjectile {
    private int explosionPower = 12;
    private static final int FIRE_SECONDS = 8;

    public LavaFireballEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);
        // Set a smaller bounding box to match the 8x8x8 pixel size
        this.setBoundingBox(this.getBoundingBox().inflate(-0.75));
    }

    public LavaFireballEntity(Level level, LivingEntity shooter, double dirX, double dirY, double dirZ) {
        super(ModEntities.LAVA_FIREBALL.get(), shooter, dirX, dirY, dirZ, level);
        // Set a smaller bounding box to match the 8x8x8 pixel size
        this.setBoundingBox(this.getBoundingBox().inflate(-0.75));
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

        // Spawn cube-shaped lava particles (much larger now)
        if (this.level().isClientSide) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            // Create a cube-like shape with particles
            // Size is approximately 24x24x24 pixels (1.5 blocks)
            double size = 0.75; // Half the size of the cube (24 pixels = 1.5 blocks)

            // Create the cube outline with lava particles
            for (int i = 0; i < 24; i++) { // More particles for larger cube
                // Choose random positions on the cube surface
                double offsetX = (random.nextBoolean() ? -1 : 1) * size;
                double offsetY = (random.nextBoolean() ? -1 : 1) * size;
                double offsetZ = (random.nextBoolean() ? -1 : 1) * size;

                // Randomly select which face of the cube
                int face = random.nextInt(3);
                if (face == 0) offsetX = random.nextDouble() * 2 * size - size;
                else if (face == 1) offsetY = random.nextDouble() * 2 * size - size;
                else offsetZ = random.nextDouble() * 2 * size - size;

                // Add lava particles only (no flame particles)
                this.level().addParticle(
                        ParticleTypes.LAVA,
                        x + offsetX,
                        y + offsetY,
                        z + offsetZ,
                        0, 0, 0
                );
            }

            // Add some particles inside the cube for more volume
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(
                        ParticleTypes.LAVA,
                        x + (random.nextDouble() * 2 - 1) * (size * 0.8),
                        y + (random.nextDouble() * 2 - 1) * (size * 0.8),
                        z + (random.nextDouble() * 2 - 1) * (size * 0.8),
                        0, 0, 0
                );
            }

            // Add a few smoke particles for the trail
            this.level().addParticle(
                    ParticleTypes.LARGE_SMOKE,
                    x,
                    y,
                    z,
                    0, 0, 0
            );
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide) {
            // Create explosion
            boolean mobGriefing = this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING);
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.explosionPower, mobGriefing, Level.ExplosionInteraction.MOB);

            // Spawn a medium magma cube
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
                spawnMagmaCube(hitPos.above());
            } else {
                // If we hit an entity or miss, spawn at current position
                spawnMagmaCube(this.blockPosition());
            }

            // Remove the entity
            this.discard();
        }
    }

    private void spawnMagmaCube(BlockPos pos) {
        MagmaCube magmaCube = new MagmaCube(EntityType.MAGMA_CUBE, this.level());

        // Set position
        magmaCube.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        // Set size to medium (size 2)
        magmaCube.setSize(2, true);

        // Add to world
        this.level().addFreshEntity(magmaCube);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        if (!this.level().isClientSide) {
            Entity target = hitResult.getEntity();

            // Set the target on fire
            target.setSecondsOnFire(FIRE_SECONDS);
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