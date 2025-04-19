package net.sho.lostlegends.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties SCULKBERRY = new FoodProperties.Builder().nutrition(20).saturationMod(1f)
            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 100), 0.5f).build();
}
