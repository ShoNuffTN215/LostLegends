package net.sho.lostlegends.client.model;

import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.GrindstoneGolemEntity;
import software.bernie.geckolib.model.GeoModel;

public class GrindstoneGolemModel extends GeoModel<GrindstoneGolemEntity> {
    @Override
    public ResourceLocation getModelResource(GrindstoneGolemEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "geo/grindstone_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GrindstoneGolemEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "textures/entity/grindstone_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GrindstoneGolemEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "animations/grindstone_golem.animation.json");
    }
}
