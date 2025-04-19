package net.sho.lostlegends.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LostLegendsMod.MODID);

    // Unobtanium Tab
    public static final RegistryObject<CreativeModeTab> UNOBTANIUM_TAB = CREATIVE_MODE_TABS.register("unobtanium_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.UNOBTANIUM.get()))
                    .title(Component.translatable("creativetab.unobtanium_tab"))
                    .displayItems((displayParameters, output) -> {
                        output.accept(ModItems.UNOBTANIUM.get());
                        output.accept(ModItems.RAW_UNOBTANIUM.get());

                        output.accept(ModItems.METAL_DETECTOR.get());

                        output.accept(ModBlocks.RAW_UNOBTANIUM_BLOCK.get());
                        output.accept(ModBlocks.UNOBTANIUM_BLOCK.get());
                        output.accept(ModBlocks.UNOBTANIUM_ORE.get());



                    }).build());
    // Lost Legends Tab
    public static final RegistryObject<CreativeModeTab> LOST_LEGENDS_TAB = CREATIVE_MODE_TABS.register("lost_legends_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FATE_CORE.get()))
                    .title(Component.translatable("creativetab.lost_legends_tab")).displayItems((displayParameters, output) -> {
                        output.accept(ModItems.FATE_CORE.get());


                    }).build());

    public static void  register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
