package net.sho.lostlegends.client.model;

import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.custom.BeastMaskItem;
import software.bernie.geckolib.model.GeoModel;

public class BeastMaskModel extends GeoModel<BeastMaskItem> {
    @Override
    public ResourceLocation getModelResource(BeastMaskItem object) {
        ResourceLocation location = new ResourceLocation(LostLegendsMod.MODID, "geo/beast_mask.geo.json");
        System.out.println("Loading Beast Mask model from: " + location);
        return location;
    }

    @Override
    public ResourceLocation getTextureResource(BeastMaskItem object) {
        ResourceLocation location = new ResourceLocation(LostLegendsMod.MODID, "textures/item/armor/beast_mask.png");
        System.out.println("Loading Beast Mask texture from: " + location);
        return location;
    }

    @Override
    public ResourceLocation getAnimationResource(BeastMaskItem animatable) {
        ResourceLocation location = new ResourceLocation(LostLegendsMod.MODID, "animations/beast_mask.animation.json");
        System.out.println("Loading Beast Mask animation from: " + location);
        return location;
    }
}