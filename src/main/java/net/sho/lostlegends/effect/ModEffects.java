package net.sho.lostlegends.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, LostLegendsMod.MODID);

    public static final RegistryObject<MobEffect> FUNGAL_ACID =
            MOB_EFFECTS.register("fungal_acid", FungalAcidEffect::new);

    public static final RegistryObject<MobEffect> STUN =
            MOB_EFFECTS.register("stun", StunEffect::new);

    public static final RegistryObject<MobEffect> KING_OF_THE_NETHER = MOB_EFFECTS.register(
            "king_of_the_nether", KingOfTheNetherEffect::new);
}
