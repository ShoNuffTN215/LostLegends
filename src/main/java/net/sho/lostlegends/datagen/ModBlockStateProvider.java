package net.sho.lostlegends.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, LostLegendsMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.UNOBTANIUM_BLOCK);
        blockWithItem(ModBlocks.RAW_UNOBTANIUM_BLOCK);
        blockWithItem(ModBlocks.SCULK_BRICK);
        blockItem(ModBlocks.SCULK_BRICK_STAIRS);
        blockItem(ModBlocks.SCULK_BRICK_SLAB);
        stairsBlock((StairBlock) ModBlocks.SCULK_BRICK_STAIRS.get(), blockTexture(ModBlocks.SCULK_BRICK.get()));
        slabBlock(((SlabBlock) ModBlocks.SCULK_BRICK_SLAB.get()), blockTexture(ModBlocks.SCULK_BRICK.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));

        blockWithItem(ModBlocks.UNOBTANIUM_ORE);

        blockWithItem(ModBlocks.SOUND_BLOCK);
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile("lostlegends:block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
