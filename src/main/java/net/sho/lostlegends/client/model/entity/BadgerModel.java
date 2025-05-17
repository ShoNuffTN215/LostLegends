package net.sho.lostlegends.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.BadgerEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BadgerModel extends GeoModel<BadgerEntity> {
    @Override
    public ResourceLocation getModelResource(BadgerEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "geo/entity/badger.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BadgerEntity object) {
        return new ResourceLocation(LostLegendsMod.MODID, "textures/entity/badger.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BadgerEntity animatable) {
        return new ResourceLocation(LostLegendsMod.MODID, "animations/badger.animation.json");
    }
    @Override
    public void setCustomAnimations(BadgerEntity animatable, long instanceId, AnimationState<BadgerEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // Get the root bone and scale it
        CoreGeoBone root = this.getAnimationProcessor().getBone("Badger");
        if (root != null) {
            root.setScaleX(1.3f);
            root.setScaleY(1.3f);
            root.setScaleZ(1.3f);
        }
    }
}
