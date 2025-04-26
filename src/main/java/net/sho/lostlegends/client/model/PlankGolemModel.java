package net.sho.lostlegends.client.model;


import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.PlankGolemEntity;
import software.bernie.geckolib.model.GeoModel;

public class PlankGolemModel extends GeoModel<PlankGolemEntity> {
    @Override
    public ResourceLocation getModelResource(PlankGolemEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "geo/plank_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PlankGolemEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "textures/entity/plank_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PlankGolemEntity animatable) {
        return new ResourceLocation(LostLegendsMod.MODID, "animations/plank_golem.animation.json");
    }
}
