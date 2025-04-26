package net.sho.lostlegends.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class KingOfTheNetherEffect extends MobEffect {
    public KingOfTheNetherEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFA500); // Orange color for the effect
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // This method is called every tick when the effect is active
        // We don't need to do anything here as the peaceful behavior is handled in the event handler
        super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Return true to have applyEffectTick called every tick
        return true;
    }
}