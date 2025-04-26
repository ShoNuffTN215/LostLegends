package net.sho.lostlegends.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.sho.lostlegends.client.model.GrindstoneGolemModel;
import net.sho.lostlegends.entity.GrindstoneGolemEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GrindstoneGolemRenderer extends GeoEntityRenderer<GrindstoneGolemEntity> {
    public GrindstoneGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GrindstoneGolemModel());
        this.shadowRadius = 0.5f;
    }
}