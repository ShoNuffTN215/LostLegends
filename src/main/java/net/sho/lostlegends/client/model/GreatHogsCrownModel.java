package net.sho.lostlegends.client.model;

import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.custom.GreatHogsCrownItem;
import software.bernie.geckolib.model.GeoModel;

public class GreatHogsCrownModel extends GeoModel<GreatHogsCrownItem> {
    @Override
    public ResourceLocation getModelResource(GreatHogsCrownItem object) {
        return new ResourceLocation(LostLegendsMod.MODID, "geo/great_hogs_crown.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GreatHogsCrownItem object) {
        return new ResourceLocation(LostLegendsMod.MODID, "textures/item/armor/great_hogs_crown.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GreatHogsCrownItem animatable) {
        // Since we have no animations, we'll use a simple empty animation file
        return new ResourceLocation(LostLegendsMod.MODID, "animations/great_hogs_crown.animation.json");
    }
}