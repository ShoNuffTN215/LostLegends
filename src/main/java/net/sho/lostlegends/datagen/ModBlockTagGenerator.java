package net.sho.lostlegends.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.util.ModTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LostLegendsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Blocks.METAL_DETECTOR_VALUABLES)
                .add(ModBlocks.UNOBTANIUM_ORE.get()).addTag(Tags.Blocks.ORES);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.UNOBTANIUM_BLOCK.get(),
                        ModBlocks.RAW_UNOBTANIUM_BLOCK.get(),
                        ModBlocks.UNOBTANIUM_BLOCK.get(),
                        ModBlocks.UNOBTANIUM_ORE.get(),
                        ModBlocks.SOUND_BLOCK.get());
                        ModBlocks.FORGE_OF_KNOWLEDGE.get();

        this.tag(BlockTags.NEEDS_STONE_TOOL);

        this.tag(ModTags.Blocks.PAXEL_MINEABLE)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(BlockTags.MINEABLE_WITH_SHOVEL)
                .addTag(BlockTags.MINEABLE_WITH_AXE);

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SOUND_BLOCK.get())
                .add(ModBlocks.SCULK_BRICK.get())
                .add(ModBlocks.SCULK_BRICK_STAIRS.get())
                .add(ModBlocks.SCULK_BRICK_SLAB.get())
                .add(ModBlocks.SCULK_BRICK_PRESSURE_PLATE.get())
                .add(ModBlocks.SCULK_BRICK_WALL.get())
                .add(ModBlocks.SCULK_BRICK_FENCE.get())
                .add(ModBlocks.SCULK_BRICK_BUTTON.get())
                .add(ModBlocks.SCULK_BRICK_FENCE_GATE.get())
                .add(ModBlocks.SCULK_BRICK_DOOR.get())
                .add(ModBlocks.SCULK_BRICK_TRAP_DOOR.get())
                .add(ModBlocks.FORGE_OF_KNOWLEDGE.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.UNOBTANIUM_BLOCK.get(),
                        ModBlocks.RAW_UNOBTANIUM_BLOCK.get(),
                        ModBlocks.UNOBTANIUM_ORE.get());
    }

    @Override
    public String getName() {
        return "Block Tags";
    }
}
