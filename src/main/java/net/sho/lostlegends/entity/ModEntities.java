package net.sho.lostlegends.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.custom.CobblestoneGolemEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LostLegendsMod.MODID);

    public static final RegistryObject<EntityType<CobblestoneGolemEntity>> COBBLESTONE_GOLEM =
            ENTITY_TYPES.register("cobblestone_golem", () -> EntityType.Builder.of(CobblestoneGolemEntity::new, MobCategory.CREATURE)
                    .sized(1F, 0.5F).build("cobblestone_golem"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

}
