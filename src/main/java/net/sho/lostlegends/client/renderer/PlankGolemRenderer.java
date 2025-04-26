package net.sho.lostlegends.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.sho.lostlegends.client.model.PlankGolemModel;
import net.sho.lostlegends.entity.PlankGolemEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PlankGolemRenderer extends GeoEntityRenderer<PlankGolemEntity> {
    public PlankGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlankGolemModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(PlankGolemEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // Adjust scale if needed
        // poseStack.scale(1.0F, 1.0F, 1.0F);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
