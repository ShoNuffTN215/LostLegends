package net.sho.lostlegends.client.renderer.block;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.sho.lostlegends.block.entity.ForgeOfKnowledgeBlockEntity;
import net.sho.lostlegends.client.model.block.ForgeOfKnowledgeModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ForgeOfKnowledgeRenderer extends GeoBlockRenderer<ForgeOfKnowledgeBlockEntity> {
    public ForgeOfKnowledgeRenderer(BlockEntityRendererProvider.Context context) {
        super(new ForgeOfKnowledgeModel());
        // No custom scaling needed - the model handles the size
    }
}