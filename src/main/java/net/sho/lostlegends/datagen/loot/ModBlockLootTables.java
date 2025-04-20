package net.sho.lostlegends.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.item.ModItems;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.UNOBTANIUM_BLOCK.get());
        this.dropSelf(ModBlocks.RAW_UNOBTANIUM_BLOCK.get());
        this.dropSelf(ModBlocks.SOUND_BLOCK.get());
        this.dropSelf(ModBlocks.SCULK_BRICK.get());
        this.dropSelf(ModBlocks.SCULK_BRICK_STAIRS.get());
        this.dropSelf(ModBlocks.SCULK_BRICK_BUTTON.get());
        this.dropSelf(ModBlocks.SCULK_BRICK_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.SCULK_BRICK_FENCE.get());
        this.dropSelf(ModBlocks.SCULK_BRICK_FENCE_GATE.get());
        this.dropSelf(ModBlocks.SCULK_BRICK_WALL.get());
        this.dropSelf(ModBlocks.SCULK_BRICK_TRAP_DOOR.get());

        this.add(ModBlocks.SCULK_BRICK_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.SCULK_BRICK_SLAB.get()));
        this.add(ModBlocks.SCULK_BRICK_DOOR.get(),
                block -> createDoorTable(ModBlocks.SCULK_BRICK_DOOR.get()));


        this.add(ModBlocks.UNOBTANIUM_ORE.get(),
                block -> createOreDrop(ModBlocks.UNOBTANIUM_ORE.get(), ModItems.RAW_UNOBTANIUM.get()));

        this.add(ModBlocks.SCULK_BRICK_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.SCULK_BRICK_SLAB.get()));


    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
