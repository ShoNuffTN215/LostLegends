package net.sho.lostlegends.event;


import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.CapybaraEntity;
import net.sho.lostlegends.entity.GrindstoneGolemEntity;
import net.sho.lostlegends.entity.PlankGolemEntity;
import net.sho.lostlegends.registry.EntityRegistry;

import static net.sho.lostlegends.registry.EntityRegistry.PLANK_GOLEM;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.GRINDSTONE_GOLEM.get(), GrindstoneGolemEntity.createAttributes().build());
        event.put(PLANK_GOLEM.get(), PlankGolemEntity.createAttributes().build());
        event.put(EntityRegistry.CAPYBARA.get(), CapybaraEntity.createAttributes().build());
    }
}
