package net.sho.lostlegends.registry;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.GrindstoneGolemEntity;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LostLegendsMod.MODID);

    public static final RegistryObject<EntityType<GrindstoneGolemEntity>> GRINDSTONE_GOLEM =
            ENTITY_TYPES.register("grindstone_golem",
                    () -> EntityType.Builder.of(GrindstoneGolemEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 1.4F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(LostLegendsMod.MODID, "grindstone_golem").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
