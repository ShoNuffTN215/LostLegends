package net.sho.lostlegends.painting;

import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;

public class ModPaintings {
    public static final DeferredRegister<PaintingVariant> PAINTING_VARIANTS =
            DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, LostLegendsMod.MODID);

    public static final RegistryObject<PaintingVariant> UNBREAKABLES_FURY = PAINTING_VARIANTS.register("unbreakables_fury",
            () -> new PaintingVariant(96, 64));
    public static final RegistryObject<PaintingVariant> CAT = PAINTING_VARIANTS.register("cat",
            () -> new PaintingVariant(32, 16));

    public static void register(IEventBus eventBus) {
        PAINTING_VARIANTS.register(eventBus);
    }
}
