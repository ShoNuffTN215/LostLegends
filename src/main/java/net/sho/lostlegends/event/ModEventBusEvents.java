package net.sho.lostlegends.event;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.entity.client.CobblestoneGolemModel;
import net.sho.lostlegends.entity.custom.CobblestoneGolemEntity;
import net.sho.lostlegends.entity.layers.ModModelLayers;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.COBBLESTONE_GOLEM_LAYER, CobblestoneGolemModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COBBLESTONE_GOLEM.get(), CobblestoneGolemEntity.createAttributes().build());
    }

}
