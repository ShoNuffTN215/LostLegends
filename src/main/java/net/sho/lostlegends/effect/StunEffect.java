package net.sho.lostlegends.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class StunEffect extends MobEffect {
    // Unique UUID for the movement speed modifier
    private static final UUID MOVEMENT_SPEED_MODIFIER_UUID = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890");

    public StunEffect() {
        // MobEffectCategory.HARMFUL makes the effect red in the inventory
        super(MobEffectCategory.HARMFUL, 0xFFD700); // Gold color for stun

        // Add attribute modifier to completely stop movement
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_UUID.toString(),
                -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Additional per-tick effects could be added here
        // For example, particles or sounds
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Apply the effect every tick
        return true;
    }
}