package net.sho.lostlegends.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.entity.custom.LavaFireballEntity;

import java.util.UUID;

public class UnbreakablesGauntletItem extends Item {
    private static final int COOLDOWN_TICKS = 60; // 3 second cooldown
    private static final float ATTACK_DAMAGE = 20.0F; // Melee damage
    private static final float ATTACK_SPEED = 2F; // Attack speed modifier

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public UnbreakablesGauntletItem(Properties properties) {
        super(properties.fireResistant());

        // Create attribute modifiers for melee damage
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(),
                "Weapon modifier", ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(),
                "Weapon modifier", ATTACK_SPEED, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // Get the direction the player is looking
            Vec3 lookVec = player.getLookAngle();

            // Create and shoot a lava fireball
            LavaFireballEntity fireball = new LavaFireballEntity(
                    level,
                    player,
                    lookVec.x,
                    lookVec.y,
                    lookVec.z
            );

            // Position the fireball closer to the player's eye position since it's smaller now
            fireball.setPos(
                    player.getX() + lookVec.x * 1.0,
                    player.getEyeY() - 0.1,
                    player.getZ() + lookVec.z * 1.0
            );

            // Set the fireball's velocity (slightly faster for the smaller projectile)
            fireball.setDeltaMovement(lookVec.scale(1.8));

            // Spawn the fireball in the world
            level.addFreshEntity(fireball);

            // Play a more dramatic sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.7F, 0.8F);

            // Set cooldown
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

            // Damage the item
            if (!player.getAbilities().instabuild) {
                itemStack.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(hand));
            }

            // Add knockback to the player (recoil from the powerful shot)
            player.push(-lookVec.x * 0.3, 0.1, -lookVec.z * 0.3);

            // Update stats
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Damage the item when used in melee
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        // Set the target on fire for longer
        target.setSecondsOnFire(6);

        // Add knockback
        Vec3 knockbackDir = target.position().subtract(attacker.position()).normalize();
        target.push(knockbackDir.x * 0.8, 0.2, knockbackDir.z * 0.8);

        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        // Return the damage modifiers only when held in the main hand
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }
}