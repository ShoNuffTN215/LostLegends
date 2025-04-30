package net.sho.lostlegends.client.model.block;

import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.entity.ForgeOfKnowledgeBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ForgeOfKnowledgeModel extends DefaultedBlockGeoModel<ForgeOfKnowledgeBlockEntity> {
    public ForgeOfKnowledgeModel() {
        super(new ResourceLocation(LostLegendsMod.MODID, "forge_of_knowledge"));
    }

    @Override
    public ResourceLocation getModelResource(ForgeOfKnowledgeBlockEntity animatable) {
        return new ResourceLocation(LostLegendsMod.MODID, "geo/block/forge_of_knowledge.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ForgeOfKnowledgeBlockEntity animatable) {
        return new ResourceLocation(LostLegendsMod.MODID, "textures/block/forge_of_knowledge.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ForgeOfKnowledgeBlockEntity animatable) {
        return new ResourceLocation(LostLegendsMod.MODID, "animations/block/forge_of_knowledge.animation.json");
    }
}
