package net.sho.lostlegends.client.renderer;

import net.sho.lostlegends.client.model.BeastMaskModel;
import net.sho.lostlegends.client.model.GreatHogsCrownModel;
import net.sho.lostlegends.item.custom.BeastMaskItem;
import net.sho.lostlegends.item.custom.GreatHogsCrownItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class BeastMaskRenderer extends GeoArmorRenderer<BeastMaskItem> {
    public BeastMaskRenderer() {
        super(new BeastMaskModel());
    }
}