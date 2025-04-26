package net.sho.lostlegends.event;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.entity.client.LavaFireballRenderer;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib.GeckoLib;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.LAVA_FIREBALL.get(), LavaFireballRenderer::new);



    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                new ModelLayerLocation(new ResourceLocation(LostLegendsMod.MODID, "lava_fireball"), "main"),
                LavaFireballRenderer::createBodyLayer);




        // Other layer definitions...
    }
}