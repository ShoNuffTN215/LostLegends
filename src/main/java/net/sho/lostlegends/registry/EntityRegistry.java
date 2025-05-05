package net.sho.lostlegends.registry;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.CapybaraEntity;
import net.sho.lostlegends.entity.GrindstoneGolemEntity;
import net.sho.lostlegends.entity.PlankGolemEntity;

import static software.bernie.example.registry.EntityRegistry.ENTITIES;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LostLegendsMod.MODID);

    public static final RegistryObject<EntityType<GrindstoneGolemEntity>> GRINDSTONE_GOLEM =
            ENTITY_TYPES.register("grindstone_golem",
                    () -> EntityType.Builder.of(GrindstoneGolemEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 1.4F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(LostLegendsMod.MODID, "grindstone_golem").toString()));

    public static final RegistryObject<EntityType<PlankGolemEntity>> PLANK_GOLEM = ENTITIES.register("plank_golem",
            () -> EntityType.Builder.of(PlankGolemEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.2F)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(LostLegendsMod.MODID, "plank_golem").toString()));

    public static final RegistryObject<EntityType<CapybaraEntity>> CAPYBARA =
            ENTITY_TYPES.register("capybara",
                    () -> EntityType.Builder.of(CapybaraEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 0.6F)
                            .clientTrackingRange(8)
                            .build(new ResourceLocation(LostLegendsMod.MODID, "capybara").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
