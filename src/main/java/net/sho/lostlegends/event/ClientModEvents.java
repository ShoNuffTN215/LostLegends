package net.sho.lostlegends.event;

import net.sho.lostlegends.client.renderer.entity.BadgerRenderer;
import net.sho.lostlegends.entity.BadgerEntity;
import net.sho.lostlegends.entity.client.CapybaraRenderer;
import net.sho.lostlegends.registry.EntityRegistry;
import net.sho.lostlegends.client.renderer.GrindstoneGolemRenderer;
import net.sho.lostlegends.client.renderer.PlankGolemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.GRINDSTONE_GOLEM.get(), GrindstoneGolemRenderer::new);
        event.registerEntityRenderer(EntityRegistry.PLANK_GOLEM.get(), PlankGolemRenderer::new);
        event.registerEntityRenderer(EntityRegistry.CAPYBARA.get(), CapybaraRenderer::new);
        event.registerEntityRenderer(EntityRegistry.BADGER.get(), BadgerRenderer::new);
    }
}
