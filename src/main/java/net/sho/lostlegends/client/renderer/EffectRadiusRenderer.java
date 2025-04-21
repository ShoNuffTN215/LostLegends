package net.sho.lostlegends.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.ModItems;
import net.sho.lostlegends.item.custom.DevourersPustuleItem;

import java.awt.*;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID, value = Dist.CLIENT)
public class EffectRadiusRenderer {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        // Check if the player has the Devourer's Pustule
        if (hasDevourersPustuleInInventory(player)) {
            // Render a circle around the player
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();

            // Adjust for camera position
            Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            // Draw the circle
            drawCircle(poseStack, player.position(), DevourersPustuleItem.EFFECT_RADIUS, 0.1f, new Color(0, 150, 0, 100));

            poseStack.popPose();
        }
    }

    private static void drawCircle(PoseStack poseStack, Vec3 center, float radius, float height, Color color) {
        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());

        int segments = 32;
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            float x1 = (float) (center.x + radius * Math.cos(angle1));
            float z1 = (float) (center.z + radius * Math.sin(angle1));
            float y1 = (float) center.y + height;

            float x2 = (float) (center.x + radius * Math.cos(angle2));
            float z2 = (float) (center.z + radius * Math.sin(angle2));
            float y2 = (float) center.y + height;

            builder.vertex(poseStack.last().pose(), x1, y1, z1).color(r, g, b, a).normal(0, 1, 0).endVertex();
            builder.vertex(poseStack.last().pose(), x2, y2, z2).color(r, g, b, a).normal(0, 1, 0).endVertex();
        }

        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
    }

    private static boolean hasDevourersPustuleInInventory(Player player) {
        // Check main inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() == ModItems.DEVOURERS_PUSTULE.get()) {
                return true;
            }
        }

        return false;
    }
}