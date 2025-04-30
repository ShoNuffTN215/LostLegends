package net.sho.lostlegends.block;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.sho.lostlegends.client.renderer.block.ForgeOfKnowledgeRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.entity.ForgeOfKnowledgeBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LostLegendsMod.MODID);

    public static final RegistryObject<BlockEntityType<ForgeOfKnowledgeBlockEntity>> FORGE_OF_KNOWLEDGE =
            BLOCK_ENTITIES.register("forge_of_knowledge",
                    () -> BlockEntityType.Builder.of(ForgeOfKnowledgeBlockEntity::new,
                            ModBlocks.FORGE_OF_KNOWLEDGE.get()).build(null));

    @Mod.EventBusSubscriber(modid = LostLegendsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(FORGE_OF_KNOWLEDGE.get(), ForgeOfKnowledgeRenderer::new);
        }
    }
}
