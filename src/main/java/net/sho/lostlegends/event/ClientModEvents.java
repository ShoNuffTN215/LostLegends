package net.sho.lostlegends.event;

import net.sho.lostlegends.registry.EntityRegistry;
import net.sho.lostlegends.client.renderer.GrindstoneGolemRenderer;
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
    }
}
