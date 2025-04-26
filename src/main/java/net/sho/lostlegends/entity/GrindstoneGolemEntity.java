package net.sho.lostlegends.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sho.lostlegends.effect.ModEffects;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GrindstoneGolemEntity extends TamableAnimal implements GeoEntity {
    // Animation states
    private static final String CONTROLLER_NAME = "moveController";
    private static final String ATTACK_CONTROLLER_NAME = "attackController";

    // Animation definitions
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.grindstone_golem.idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.grindstone_golem.walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenLoop("animation.grindstone_golem.attack");

    // Entity data
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(GrindstoneGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Target tracking
    private LivingEntity previousTarget = null;
    private int attackAnimationTimeout = 0;
    private boolean returningToOwner = false;
    private int attackCooldown = 0;

    public GrindstoneGolemEntity(EntityType<? extends GrindstoneGolemEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 22.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 6.0f)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_SPEED, 4.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new GrindstoneGolemAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, Monster.class, false, null));
    }

    @Override
    public void tick() {
        super.tick();

        // Get current target
        LivingEntity currentTarget = this.getTarget();

        // Check if we just acquired a target (target changed from null to something)
        if (currentTarget != null && previousTarget == null) {
            // We just got a target, start attacking animation immediately
            setAttacking(true);
            returningToOwner = false;
            attackAnimationTimeout = 0;
        }
        // Check if we just lost our target (target changed from something to null)
        else if (currentTarget == null && previousTarget != null) {
            // We just lost our target, we're returning to owner
            returningToOwner = true;

            // Set a short timeout for the attack animation
            attackAnimationTimeout = 10; // Half a second at 20 ticks per second
        }

        // Handle attack animation timeout
        if (attackAnimationTimeout > 0) {
            attackAnimationTimeout--;
            if (attackAnimationTimeout == 0 && currentTarget == null) {
                // If timeout expired and we still have no target, stop attacking
                setAttacking(false);
                returningToOwner = false;
            }
        }

        // Force check if target is dead or invalid
        if (currentTarget != null && (currentTarget.isDeadOrDying() || !currentTarget.isAlive())) {
            this.setTarget(null);
            setAttacking(false);
            attackAnimationTimeout = 5; // Very short transition
        }

        // Check for collision with target and apply damage if needed
        if (currentTarget != null && isAttacking() && attackCooldown <= 0) {
            if (isCollidingWithTarget(currentTarget)) {
                // Apply damage and stun effect only when colliding
                applyAttackEffects(currentTarget);
                attackCooldown = 10; // Half a second cooldown between collision attacks
            }
        }

        // Decrease attack cooldown
        if (attackCooldown > 0) {
            attackCooldown--;
        }

        // Update previous target for next tick comparison
        previousTarget = currentTarget;
    }

    /**
     * Checks if this entity is colliding with the target entity
     */
    private boolean isCollidingWithTarget(Entity target) {
        // Get the bounding boxes
        AABB thisBox = this.getBoundingBox();
        AABB targetBox = target.getBoundingBox();

        // Check if they intersect
        return thisBox.intersects(targetBox);
    }

    /**
     * Apply attack effects to the target
     */
    private void applyAttackEffects(LivingEntity target) {
        if (!this.level().isClientSide()) {
            // Apply damage
            target.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));

            // Apply stun effect
            target.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 3, 3));

            // Apply knockback
            double knockbackStrength = 0.5;
            Vec3 knockback = target.position().subtract(this.position()).normalize().scale(knockbackStrength);
            target.push(knockback.x, 0.2, knockback.z);
        }
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        // We're overriding this to NOT apply damage here
        // Instead, damage is applied in the tick method when collision is detected

        // Still return true to indicate the attack attempt was made
        // This keeps the AI working properly
        return true;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        LivingEntity oldTarget = this.getTarget();
        super.setTarget(target);

        // When target is set, immediately start attack animation
        if (target != null) {
            setAttacking(true);
            returningToOwner = false;
            attackAnimationTimeout = 0;
        }
        // When target is cleared and we had one before
        else if (oldTarget != null) {
            attackAnimationTimeout = 10; // Short timeout before stopping animation
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!this.level().isClientSide() && this.isOwnedBy(player)) {
            this.setOrderedToSit(!this.isOrderedToSit());
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setAttacking(tag.getBoolean("Attacking"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Attacking", isAttacking());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Movement controller - handles idle and walking animations
        controllers.add(new AnimationController<>(this, CONTROLLER_NAME, 5, this::moveController));

        // Attack controller - only handles attack animations with higher priority and faster transitions
        controllers.add(new AnimationController<>(this, ATTACK_CONTROLLER_NAME, 0, this::attackController)
                .setAnimationSpeed(2.5f)); // Make attack animation 2.5x faster
    }

    private <T extends GeoAnimatable> PlayState moveController(AnimationState<T> state) {
        if (isAttacking()) {
            return PlayState.STOP; // Let the attack controller handle it
        }

        if (state.isMoving()) {
            state.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState attackController(AnimationState<T> state) {
        if (isAttacking()) {
            state.getController().setAnimation(ATTACK_ANIM);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP; // Let the move controller handle it
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return null; // Grindstone golems cannot breed
    }

    // Custom attack goal that handles attack timing and animation
    static class GrindstoneGolemAttackGoal extends MeleeAttackGoal {
        private final GrindstoneGolemEntity golem;
        private int ticksUntilNextPathRecalculation = 0;

        public GrindstoneGolemAttackGoal(GrindstoneGolemEntity golem, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(golem, speedModifier, followingTargetEvenIfNotSeen);
            this.golem = golem;
        }

        @Override
        public void start() {
            super.start();
            // Start attack animation as soon as we begin pursuing a target
            golem.setAttacking(true);
        }

        @Override
        public void tick() {
            LivingEntity target = this.golem.getTarget();
            if (target == null) {
                return;
            }

            // Check if target is dead or invalid
            if (target.isDeadOrDying() || !target.isAlive()) {
                this.golem.setTarget(null);
                return;
            }

            // Ensure attack animation is playing while we have a target
            if (!golem.isAttacking()) {
                golem.setAttacking(true);
            }

            // Move towards the target
            this.golem.getLookControl().setLookAt(target, 30.0F, 30.0F);

            // Path recalculation logic
            if (--this.ticksUntilNextPathRecalculation <= 0) {
                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(4);
                if (!this.golem.isLeashed() && !this.golem.isPassenger()) {
                    // Move to the target
                    this.golem.getNavigation().moveTo(target, 1.0D);
                }
            }

            // The actual attack is now handled in the tick method of the golem
            // when collision is detected
        }

        @Override
        public void stop() {
            super.stop();
            // When we stop pursuing, check if we need to stop the animation
            if (golem.getTarget() == null) {
                // Only set a short timeout - the tick method will handle the actual stopping
                golem.attackAnimationTimeout = 10;
            }
        }
    }
}
