package net.sho.lostlegends.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
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

        simpleItem(ModItems.METAL_DETECTOR);
        simpleItem(ModItems.LIGHTNING_STAFF);

        simpleItem(ModItems.UNOBTANIUM_WASTE);

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(LostLegendsMod.MODID,"item/" + item.getId().getPath()));
    }
}