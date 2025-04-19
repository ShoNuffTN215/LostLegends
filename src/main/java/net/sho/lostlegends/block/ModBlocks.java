package net.sho.lostlegends.block;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.custom.SoundBlock;
import net.sho.lostlegends.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, LostLegendsMod.MODID);

    public static final RegistryObject<Block> UNOBTANIUM_BLOCK = registerBlock("unobtanium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RAW_UNOBTANIUM_BLOCK = registerBlock("raw_unobtanium_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).sound(SoundType.AMETHYST)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SCULK_BRICK = registerBlock("sculk_brick",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_STAIRS)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SCULK_BRICK_STAIRS = registerBlock("sculk_brick_stairs",
            () -> new StairBlock(() -> ModBlocks.SCULK_BRICK.get().defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SCULK_BRICK_SLAB = registerBlock("sculk_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_SLAB)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> UNOBTANIUM_ORE = registerBlock("unobtanium_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).sound(SoundType.AMETHYST)
                    .strength(5f).requiresCorrectToolForDrops(), UniformInt.of(10, 20)));

    public static final RegistryObject<Block> SOUND_BLOCK = registerBlock("sound_block",
            () -> new SoundBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));




    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}