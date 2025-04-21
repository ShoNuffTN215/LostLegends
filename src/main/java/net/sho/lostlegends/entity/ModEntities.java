package net.sho.lostlegends.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.custom.CobblestoneGolemEntity;
import net.sho.lostlegends.entity.custom.LavaFireballEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LostLegendsMod.MODID);

    public static final RegistryObject<EntityType<CobblestoneGolemEntity>> COBBLESTONE_GOLEM =
            ENTITY_TYPES.register("cobblestone_golem", () -> EntityType.Builder.of(CobblestoneGolemEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 0.6F).build("cobblestone_golem"));

    public static final RegistryObject<EntityType<LavaFireballEntity>> LAVA_FIREBALL =
            ENTITY_TYPES.register("lava_fireball",
                    () -> EntityType.Builder.<LavaFireballEntity>of(LavaFireballEntity::new, MobCategory.MISC)
                            .sized(3F, 3F) // Much larger than normal fireballs
                            .clientTrackingRange(8)
                            .updateInterval(1)
                            .fireImmune() // Make it immune to fire
                            .build(new ResourceLocation(LostLegendsMod.MODID, "lava_fireball").toString()));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
