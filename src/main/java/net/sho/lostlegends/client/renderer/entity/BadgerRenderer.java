package net.sho.lostlegends.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.client.model.entity.BadgerModel;
import net.sho.lostlegends.entity.BadgerEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BadgerRenderer extends GeoEntityRenderer<BadgerEntity> {
    public BadgerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BadgerModel());
        this.shadowRadius = 0.6f;

        this.scaleWidth = 1.3f;
        this.scaleHeight = 1.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(BadgerEntity instance) {
        return new ResourceLocation(LostLegendsMod.MODID, "textures/entity/badger.png");
    }
}
