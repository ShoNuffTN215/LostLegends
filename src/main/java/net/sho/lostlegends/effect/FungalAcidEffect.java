package net.sho.lostlegends.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class FungalAcidEffect extends MobEffect {
    // Unique IDs for the attribute modifiers
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("9e6d1d04-f8e5-4196-a1fc-8d6f3c4a7e45");
    private static final UUID ARMOR_TOUGHNESS_MODIFIER_UUID = UUID.fromString("8d3e1ab6-94c9-4794-9be3-6c77b3a58a38");

    // Damage amount per tick
    private static final float DAMAGE_PER_TICK = 1.0F;

    // Defense reduction amounts (per amplifier level)
    private static final double BASE_ARMOR_REDUCTION = -2.0D;
    private static final double BASE_TOUGHNESS_REDUCTION = -1.0D;

    public FungalAcidEffect() {
        // MobEffectCategory.HARMFUL makes the effect red in the inventory
        super(MobEffectCategory.HARMFUL, 0x4D8A36); // Green-brown color for fungal acid

        // Add attribute modifiers for armor and armor toughness
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER_UUID.toString(),
                BASE_ARMOR_REDUCTION, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_MODIFIER_UUID.toString(),
                BASE_TOUGHNESS_REDUCTION, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Apply damage over time
        // The damage increases with the amplifier level (effect level - 1)
        entity.hurt(entity.damageSources().magic(), DAMAGE_PER_TICK * (amplifier + 1));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Apply the effect every 20 ticks (1 second)
        // For more frequent damage, reduce this number
        int tickInterval = 20;

        // For more severe effect levels, apply damage more frequently
        if (amplifier > 0) {
            tickInterval = Math.max(10, 20 - (amplifier * 2));
        }

        return duration % tickInterval == 0;
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        // Scale the attribute modifier based on the effect amplifier
        // This makes higher levels of the effect reduce defense more
        if (modifier.getId().equals(ARMOR_MODIFIER_UUID)) {
            return BASE_ARMOR_REDUCTION * (amplifier + 1);
        } else if (modifier.getId().equals(ARMOR_TOUGHNESS_MODIFIER_UUID)) {
            return BASE_TOUGHNESS_REDUCTION * (amplifier + 1);
        }

        return modifier.getAmount();
    }
}
