package net.sho.lostlegends.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class PlankGolemEntity extends TamableAnimal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> DATA_IS_ATTACKING =
            SynchedEntityData.defineId(PlankGolemEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int attackCooldown = 0;
    private static final int ATTACK_COOLDOWN_MAX = 40; // 2 seconds
    private static final int ATTACK_RANGE = 10; // blocks
    private int attackAnimationTicks = 0;

    // Animation definitions
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.plank_golem.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.plank_golem.walk");
    private static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("animation.plank_golem.sit");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("animation.plank_golem.attack");

    public PlankGolemEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new RangedAttackGoal());
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        // Modified HurtByTargetGoal to exclude entities with same owner
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                LivingEntity attacker = this.mob.getLastHurtByMob();
                if (attacker != null && PlankGolemEntity.this.hasSameOwnerAs(attacker)) {
                    return false;
                }
                return super.canUse();
            }
        }).setAlertOthers());

        // Modified NearestAttackableTargetGoal to exclude entities with same owner
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                (entity) -> {
                    // Don't target players or tamable animals with the same owner
                    if (entity instanceof Player) return false;

                    // Check if both entities are tamable and have the same owner
                    if (entity instanceof TamableAnimal tamable) {
                        UUID targetOwner = tamable.getOwnerUUID();
                        UUID thisOwner = this.getOwnerUUID();

                        // If both have owners, and they're the same, don't target
                        if (targetOwner != null && thisOwner != null && targetOwner.equals(thisOwner)) {
                            return false;
                        }
                    }

                    return !(entity instanceof TamableAnimal) || ((TamableAnimal) entity).getOwnerUUID() == null;
                }));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 11.75D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.2f);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_ATTACKING, false);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_IS_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_IS_ATTACKING, attacking);
    }

    @Override
    public void tick() {
        super.tick();

        // Handle attack animation timing
        if (attackAnimationTicks > 0) {
            attackAnimationTicks--;
            if (attackAnimationTicks == 0) {
                setAttacking(false);
            }
        }

        if (!this.level().isClientSide) {
            if (attackCooldown > 0) {
                attackCooldown--;
            }

            // Check for targets and attack if possible
            if (this.getTarget() != null && !this.isInSittingPose() && attackCooldown <= 0) {
                // Additional check to prevent attacking entities with same owner
                if (!hasSameOwnerAs(this.getTarget())) {
                    double distance = this.distanceToSqr(this.getTarget());
                    if (distance < ATTACK_RANGE * ATTACK_RANGE) {
                        this.attackWithArrow(this.getTarget());
                        attackCooldown = ATTACK_COOLDOWN_MAX;
                    }
                } else {
                    // Clear target if it has the same owner
                    this.setTarget(null);
                }
            }
        }
    }

    // New method to check if an entity has the same owner
    public boolean hasSameOwnerAs(Entity entity) {
        if (!(entity instanceof TamableAnimal tamable)) {
            return false;
        }

        UUID thisOwner = this.getOwnerUUID();
        UUID otherOwner = tamable.getOwnerUUID();

        return thisOwner != null && otherOwner != null && thisOwner.equals(otherOwner);
    }

    private void attackWithArrow(LivingEntity target) {
        if (!this.level().isClientSide()) {
            // Final check before attacking
            if (target instanceof TamableAnimal tamable && this.getOwnerUUID() != null &&
                    this.getOwnerUUID().equals(tamable.getOwnerUUID())) {
                return;
            }

            // Set attacking state
            this.setAttacking(true);
            this.attackAnimationTicks = 11;

            // Calculate spawn position
            Vec3 lookVec = this.getLookAngle();
            double spawnDistance = 0.8;
            double arrowX = this.getX() + lookVec.x * spawnDistance;
            double arrowY = this.getEyeY() - 0.1;
            double arrowZ = this.getZ() + lookVec.z * spawnDistance;

            // Create arrow
            Arrow arrow = new Arrow(this.level(), this);
            arrow.setPos(arrowX, arrowY, arrowZ);
            arrow.setOwner(this);

            // Store owner UUID
            CompoundTag tag = arrow.getPersistentData();
            if (this.getOwnerUUID() != null) {
                tag.putUUID("OwnerUUID", this.getOwnerUUID());
            }

            // Enhance arrow properties
            arrow.setBaseDamage(6.0); // Significantly increased damage
            arrow.setPierceLevel((byte)1);
            arrow.pickup = Arrow.Pickup.DISALLOWED;

            // Calculate trajectory to target's center mass
            double targetX = target.getX();
            double targetY = target.getY() + (target.getBbHeight() / 2.0);
            double targetZ = target.getZ();

            double d0 = targetX - arrow.getX();
            double d1 = targetY - arrow.getY();
            double d2 = targetZ - arrow.getZ();

            // Shoot with improved parameters - higher speed, lower inaccuracy
            arrow.shoot(d0, d1, d2, 2.5F, 0.5F);

            this.level().addFreshEntity(arrow);
            this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

            // BACKUP: Apply direct damage if we have line of sight
            // This ensures damage is applied even if the arrow misses
            if (this.hasLineOfSight(target)) {
                // Schedule damage to be applied after a short delay (simulating arrow travel time)
                this.level().getServer().tell(new net.minecraft.server.TickTask(5, () -> {
                    if (target.isAlive() && !target.isDeadOrDying() && this.distanceToSqr(target) <= ATTACK_RANGE * ATTACK_RANGE) {
                        // Apply damage directly as a fallback
                        target.hurt(this.damageSources().mobAttack(this), 4.0F);
                    }
                }));
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Get the entity that caused the damage
        Entity attacker = source.getEntity();

        // Prevent self-damage completely
        if (attacker == this) {
            return false;
        }

        // Check if the direct entity is a projectile
        Entity directEntity = source.getDirectEntity();
        if (directEntity instanceof Projectile projectile) {
            // If this entity is the owner of the projectile, prevent damage
            if (projectile.getOwner() == this) {
                return false;
            }

            // Check for stored owner UUID in projectile's tag
            CompoundTag tag = projectile.getPersistentData();
            if (tag.hasUUID("OwnerUUID")) {
                UUID projectileOwnerUUID = tag.getUUID("OwnerUUID");
                if (this.getUUID().equals(projectileOwnerUUID)) {
                    return false; // Prevent self-damage
                }

                // Also check if we have the same owner
                UUID thisOwner = this.getOwnerUUID();
                if (thisOwner != null && thisOwner.equals(projectileOwnerUUID)) {
                    return false; // Prevent damage from projectiles with same owner UUID
                }
            }
        }

        // Rest of your existing hurt method...
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!this.isTame() && player.getUUID().equals(this.getOwnerUUID())) {
            this.setTame(true);
            this.setOwnerUUID(player.getUUID());
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && player.getUUID().equals(this.getOwnerUUID())) {
            if (!this.level().isClientSide) {
                this.setInSittingPose(!this.isInSittingPose());
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setInSittingPose(tag.getBoolean("Sitting"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Sitting", this.isInSittingPose());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WOOD_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOD_BREAK;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return null; // Cannot breed
    }

    // GeckoLib animation methods

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", 0, this::animationPredicate) // Changed from 5 to 0 for immediate response
                        .triggerableAnim("attack", ATTACK_ANIM)
        );
    }

    private PlayState animationPredicate(AnimationState<PlankGolemEntity> state) {
        if (this.isAttacking()) {
            state.getController().setAnimation(ATTACK_ANIM);
            return PlayState.CONTINUE;
        }

        if (this.isInSittingPose()) {
            state.getController().setAnimation(SIT_ANIM);
            return PlayState.CONTINUE;
        }

        if (state.isMoving()) {
            state.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // Custom goal for ranged attack
    private class RangedAttackGoal extends Goal {
        public RangedAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = PlankGolemEntity.this.getTarget();
            // Don't attack if target has same owner
            if (target != null && PlankGolemEntity.this.hasSameOwnerAs(target)) {
                PlankGolemEntity.this.setTarget(null);
                return false;
            }
            return target != null && !PlankGolemEntity.this.isInSittingPose();
        }

        @Override
        public void tick() {
            LivingEntity target = PlankGolemEntity.this.getTarget();
            if (target != null) {
                // Double-check target doesn't have same owner
                if (PlankGolemEntity.this.hasSameOwnerAs(target)) {
                    PlankGolemEntity.this.setTarget(null);
                    return;
                }

                double distance = PlankGolemEntity.this.distanceToSqr(target);
                PlankGolemEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

                if (distance <= ATTACK_RANGE * ATTACK_RANGE) {
                    PlankGolemEntity.this.getNavigation().stop();
                } else {
                    PlankGolemEntity.this.getNavigation().moveTo(target, 1.0D);
                }
            }
        }
    }

    // Global event handler to prevent damage between entities with the same owner
    @Mod.EventBusSubscriber
    public static class GolemDamageHandler {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            // Check if the target is a PlankGolemEntity
            if (event.getEntity() instanceof TamableAnimal target) {
                Entity attacker = event.getSource().getEntity();

                // Check if the attacker is a tamable entity
                if (attacker instanceof TamableAnimal tamableAttacker) {
                    // Check if they have the same owner
                    UUID targetOwner = target.getOwnerUUID();
                    UUID attackerOwner = tamableAttacker.getOwnerUUID();

                    if (targetOwner != null && attackerOwner != null && targetOwner.equals(attackerOwner)) {
                        // Cancel the damage event
                        event.setCanceled(true);
                    }
                }

                // Check for projectile damage
                Entity directEntity = event.getSource().getDirectEntity();
                if (directEntity instanceof Projectile projectile) {
                    Entity projectileOwner = projectile.getOwner();

                    // Check if projectile owner is a tamable with same owner
                    if (projectileOwner instanceof TamableAnimal tamableProjectileOwner) {
                        UUID projectileOwnerUUID = tamableProjectileOwner.getOwnerUUID();
                        UUID thisOwner = target.getOwnerUUID();

                        if (thisOwner != null && projectileOwnerUUID != null && thisOwner.equals(projectileOwnerUUID)) {
                            event.setCanceled(true); // Prevent projectile damage from entities with same owner
                        }
                    }

                    // Check for stored owner UUID in projectile's tag
                    CompoundTag tag = projectile.getPersistentData();
                    if (tag.hasUUID("OwnerUUID")) {
                        UUID projectileOwnerUUID = tag.getUUID("OwnerUUID");
                        UUID thisOwner = target.getOwnerUUID();

                        if (thisOwner != null && thisOwner.equals(projectileOwnerUUID)) {
                            event.setCanceled(true); // Prevent damage from projectiles with same owner UUID
                        }
                    }
                }
            }
        }
    }
}