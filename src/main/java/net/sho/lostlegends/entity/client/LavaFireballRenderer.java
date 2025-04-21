package net.sho.lostlegends.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.custom.LavaFireballEntity;

public class LavaFireballRenderer extends EntityRenderer<LavaFireballEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LostLegendsMod.MODID, "textures/entity/lava_fireball.png");
    private static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(LostLegendsMod.MODID, "lava_fireball"), "main");

    private final LavaFireballModel model;

    public LavaFireballRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new LavaFireballModel(context.bakeLayer(LAYER_LOCATION));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // Create a sphere-like shape for the fireball
        partDefinition.addOrReplaceChild("main",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void render(LavaFireballEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Scale the model
        poseStack.scale(0.75F, 0.75F, 0.75F);

        // Add a spinning animation
        float spin = (entity.tickCount + partialTicks) * 15;
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.mulPose(Axis.XP.rotationDegrees(spin * 0.7F));

        // Render the model with a glowing effect
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 0.5F, 0.0F, 0.8F);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(LavaFireballEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(LavaFireballEntity entity, BlockPos pos) {
        return 15; // Full brightness like lava
    }

    // Inner model class
    private static class LavaFireballModel extends EntityModel<LavaFireballEntity> {
        private final ModelPart main;

        public LavaFireballModel(ModelPart root) {
            this.main = root.getChild("main");
        }

        @Override
        public void setupAnim(LavaFireballEntity entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            // Pulsating animation
            float scale = 1.0F + 0.1F * (float)Math.sin(ageInTicks * 0.3F);
            this.main.xScale = scale;
            this.main.yScale = scale;
            this.main.zScale = scale;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                                   int packedOverlay, float red, float green, float blue, float alpha) {
            main.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
