package net.sho.lostlegends.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.sho.lostlegends.effect.ModEffects;

import java.util.List;

public class DevourersPustuleItem extends Item {
    // Constants for the effect field
    public static final int EFFECT_RADIUS = 5; // Radius in blocks
    public static final int EFFECT_DURATION = 60; // Duration in ticks (3 seconds, refreshed frequently)
    public static final int EFFECT_AMPLIFIER = 0; // Effect level (0 = level 1)

    // Constants for food properties
    private static final int HUNGER_RESTORED = 4; // Hunger points restored
    private static final float SATURATION_RESTORED = 0.3F; // Saturation restored
    private static final int DURABILITY_DAMAGE = 1; // Durability consumed per use

    public DevourersPustuleItem(Item.Properties properties) {
        super(properties
                .durability(32) // Total uses
                .food(new FoodProperties.Builder()
                        .nutrition(HUNGER_RESTORED)
                        .saturationMod(SATURATION_RESTORED)
                        .alwaysEat() // Can eat even when not hungry
                        .build())
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Start using the item like food
        if (isEdible()) {
            if (player.canEat(itemStack.getFoodProperties(player).canAlwaysEat())) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemStack);
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }

        // Apply the base food effects
        super.finishUsingItem(stack, level, entity);

        // Play a sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 0.5F, 0.4F);

        // Damage the item
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(DURABILITY_DAMAGE, player, (p) ->
                    p.broadcastBreakEvent(p.getUsedItemHand()));
        }

        // Update stats
        player.awardStat(Stats.ITEM_USED.get(this));

        return stack;
    }

    /**
     * Apply the Fungal Acid effect to entities in a radius around the player.
     * This is called from the tick handler, not directly from the item.
     */
    public static void applyEffectInRadius(ServerLevel level, Player player) {
        // Get all living entities within radius
        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(EFFECT_RADIUS),
                entity -> entity != player && entity.isAffectedByPotions() // Exclude the player
        );

        // Apply the effect to all entities
        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(
                    ModEffects.FUNGAL_ACID.get(),
                    EFFECT_DURATION,
                    EFFECT_AMPLIFIER
            ));
        }
    }

    /**
     * Spawn particles around the player to visualize the effect field.
     * This is called from the tick handler, not directly from the item.
     */
    public static void spawnFieldParticles(ServerLevel level, Player player) {
        // Only spawn particles occasionally to reduce visual noise
        if (level.getGameTime() % 10 == 0) {
            // Spawn fewer particles for performance
            for (int i = 0; i < 5; i++) {
                double angle = Math.random() * Math.PI * 2;
                double distance = EFFECT_RADIUS * 0.8; // Slightly inside the radius
                double x = player.getX() + Math.cos(angle) * distance;
                double z = player.getZ() + Math.sin(angle) * distance;

                level.sendParticles(
                        ParticleTypes.SNEEZE, // Or another appropriate particle
                        x,
                        player.getY() + 0.5,
                        z,
                        1, // particle count
                        0, 0.1, 0, // offset
                        0.01 // speed
                );
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32; // Time in ticks to consume the item
    }
}