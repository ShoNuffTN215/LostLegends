package net.sho.lostlegends.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.CapybaraEntity;
import software.bernie.geckolib.model.GeoModel;

public class CapybaraModel extends GeoModel<CapybaraEntity> {
    @Override
    public ResourceLocation getModelResource(CapybaraEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "geo/capybara.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CapybaraEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "textures/entity/capybara.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CapybaraEntity animatable) {
        return new ResourceLocation(LostLegendsMod.MODID, "animations/capybara.animation.json");
    }
}