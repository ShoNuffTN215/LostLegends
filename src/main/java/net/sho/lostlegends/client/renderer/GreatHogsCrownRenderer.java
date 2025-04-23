package net.sho.lostlegends.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.client.model.GreatHogsCrownModel;
import net.sho.lostlegends.item.custom.GreatHogsCrownItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class GreatHogsCrownRenderer extends GeoArmorRenderer<GreatHogsCrownItem> {
    public GreatHogsCrownRenderer() {
        super(new GreatHogsCrownModel());
    }
}