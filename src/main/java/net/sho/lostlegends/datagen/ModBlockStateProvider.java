package net.sho.lostlegends.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
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
        blockWithItem(ModBlocks.RAW_UNOBTANIUM_BLOCK);
        blockWithItem(ModBlocks.UNOBTANIUM_BLOCK);
        blockWithItem(ModBlocks.SOUND_BLOCK);
        blockWithItem(ModBlocks.SCULK_BRICK);
        blockWithItem(ModBlocks.UNOBTANIUM_ORE);

        stairsBlock(((StairBlock) ModBlocks.SCULK_BRICK_STAIRS.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));
        slabBlock(((SlabBlock) ModBlocks.SCULK_BRICK_SLAB.get()), blockTexture(ModBlocks.SCULK_BRICK.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));

        buttonBlock(((ButtonBlock) ModBlocks.SCULK_BRICK_BUTTON.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.SCULK_BRICK_PRESSURE_PLATE.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));

        fenceBlock(((FenceBlock) ModBlocks.SCULK_BRICK_FENCE.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.SCULK_BRICK_FENCE_GATE.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));
        wallBlock(((WallBlock) ModBlocks.SCULK_BRICK_WALL.get()), blockTexture(ModBlocks.SCULK_BRICK.get()));

        doorBlockWithRenderType(((DoorBlock) ModBlocks.SCULK_BRICK_DOOR.get()), modLoc("block/sculk_door_bottom"), modLoc("block/sculk_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.SCULK_BRICK_TRAP_DOOR.get()), modLoc("block/sculk_brick_trap_door"), true, "cutout");

        blockItem(ModBlocks.SCULK_BRICK_STAIRS);
        blockItem(ModBlocks.SCULK_BRICK_SLAB);
        blockItem(ModBlocks.SCULK_BRICK_PRESSURE_PLATE);
        blockItem(ModBlocks.SCULK_BRICK_FENCE_GATE);
        blockItem(ModBlocks.SCULK_BRICK_TRAP_DOOR, "_bottom");
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject, String appendix) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile("lostlegends:block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath() + appendix));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile("lostlegends:block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}