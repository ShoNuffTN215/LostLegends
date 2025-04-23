package net.sho.lostlegends.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LostLegendsMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.UNOBTANIUM);
        simpleItem(ModItems.RAW_UNOBTANIUM);
        simpleItem(ModItems.FATE_CORE);

        simpleItem(ModItems.SCULKBERRY);

        simpleItem(ModItems.LIGHTNING_STAFF);

        handheldItem(ModItems.UNOBTANIUM_PICKAXE);
        handheldItem(ModItems.UNOBTANIUM_AXE);
        handheldItem(ModItems.UNOBTANIUM_SHOVEL);
        handheldItem(ModItems.UNOBTANIUM_HOE);
        handheldItem(ModItems.UNOBTANIUM_PAXEL);
        handheldItem(ModItems.UNOBTANIUM_HAMMER);

        simpleItem(ModItems.UNOBTANIUM_WASTE);

        simpleItem(ModItems.UNOBTANIUM_HELMET);
        simpleItem(ModItems.UNOBTANIUM_CHESTPLATE);
        simpleItem(ModItems.UNOBTANIUM_LEGGING);
        simpleItem(ModItems.UNOBTANIUM_BOOTS);

        simpleItem(ModItems.GREAT_HOGS_CROWN);
        simpleItem(ModItems.BEAST_MASK);

        buttonItem(ModBlocks.SCULK_BRICK_BUTTON, ModBlocks.SCULK_BRICK);
        fenceItem(ModBlocks.SCULK_BRICK_FENCE, ModBlocks.SCULK_BRICK);
        wallItem(ModBlocks.SCULK_BRICK_WALL, ModBlocks.SCULK_BRICK);

        simpleBlockItem(ModBlocks.SCULK_BRICK_DOOR);

        withExistingParent(ModItems.COBBLESTONE_GOLEM_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(LostLegendsMod.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(LostLegendsMod.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(LostLegendsMod.MODID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(LostLegendsMod.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(LostLegendsMod.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(LostLegendsMod.MODID,"item/" + item.getId().getPath()));
    }
}