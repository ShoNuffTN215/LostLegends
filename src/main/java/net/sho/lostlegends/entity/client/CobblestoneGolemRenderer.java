package net.sho.lostlegends.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.custom.CobblestoneGolemEntity;
import net.sho.lostlegends.entity.layers.ModModelLayers;

public class CobblestoneGolemRenderer extends MobRenderer<CobblestoneGolemEntity, CobblestoneGolemModel<CobblestoneGolemEntity>> {
    private static final ResourceLocation COBBLESTONE_GOLEM_LOCATION = new ResourceLocation(LostLegendsMod.MODID, "textures/entity/cobblestone_golem.png");

    public CobblestoneGolemRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new CobblestoneGolemModel<>(pContext.bakeLayer(ModModelLayers.COBBLESTONE_GOLEM_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CobblestoneGolemEntity pEntity) {
        return COBBLESTONE_GOLEM_LOCATION;
    }
}
