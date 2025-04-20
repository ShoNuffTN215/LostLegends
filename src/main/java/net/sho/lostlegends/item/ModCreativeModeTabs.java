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
                        // Normal items
                        output.accept(ModItems.UNOBTANIUM.get());
                        output.accept(ModItems.RAW_UNOBTANIUM.get());

                        // Functional items
                        output.accept(ModItems.METAL_DETECTOR.get());
                        output.accept(ModItems.LIGHTNING_STAFF.get());

                        // Tools
                        output.accept(ModItems.UNOBTANIUM_KATANA.get());
                        output.accept(ModItems.UNOBTANIUM_PICKAXE.get());
                        output.accept(ModItems.UNOBTANIUM_AXE.get());
                        output.accept(ModItems.UNOBTANIUM_SHOVEL.get());
                        output.accept(ModItems.UNOBTANIUM_HOE.get());
                        output.accept(ModItems.UNOBTANIUM_PAXEL.get());
                        output.accept(ModItems.UNOBTANIUM_HAMMER.get());

                        // Armor
                        output.accept(ModItems.UNOBTANIUM_HELMET.get());
                        output.accept(ModItems.UNOBTANIUM_CHESTPLATE.get());
                        output.accept(ModItems.UNOBTANIUM_LEGGING.get());
                        output.accept(ModItems.UNOBTANIUM_BOOTS.get());

                        // Food Items
                        output.accept(ModItems.SCULKBERRY.get());

                        // Spawn Eggs


                        // Fuel Items
                        output.accept(ModItems.UNOBTANIUM_WASTE.get());

                        // Block Items
                        output.accept(ModBlocks.RAW_UNOBTANIUM_BLOCK.get());
                        output.accept(ModBlocks.UNOBTANIUM_BLOCK.get());
                        output.accept(ModBlocks.UNOBTANIUM_ORE.get());
                        output.accept(ModBlocks.SCULK_BRICK.get());
                        output.accept(ModBlocks.SCULK_BRICK_STAIRS.get());
                        output.accept(ModBlocks.SCULK_BRICK_SLAB.get());
                        output.accept(ModBlocks.SCULK_BRICK_BUTTON.get());
                        output.accept(ModBlocks.SCULK_BRICK_PRESSURE_PLATE.get());
                        output.accept(ModBlocks.SCULK_BRICK_FENCE.get());
                        output.accept(ModBlocks.SCULK_BRICK_FENCE_GATE.get());
                        output.accept(ModBlocks.SCULK_BRICK_WALL.get());
                        output.accept(ModBlocks.SCULK_BRICK_DOOR.get());
                        output.accept(ModBlocks.SCULK_BRICK_TRAP_DOOR.get());

                        // Functional Blocks
                        output.accept(ModBlocks.SOUND_BLOCK.get());



                    }).build());
    // Lost Legends Tab
    public static final RegistryObject<CreativeModeTab> LOST_LEGENDS_TAB = CREATIVE_MODE_TABS.register("lost_legends_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FATE_CORE.get()))
                    .title(Component.translatable("creativetab.lost_legends_tab")).displayItems((displayParameters, output) -> {
                        output.accept(ModItems.FATE_CORE.get());
                        output.accept(ModItems.UNBREAKABLES_GAUNTLET.get());
                        output.accept(ModItems.DEVOURERS_PUSTULE.get());
                        output.accept(ModItems.FLAMES_OF_CREATION.get());
                        output.accept(ModItems.COBBLESTONE_GOLEM_SPAWN_EGG.get());


                    }).build());

    public static void  register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
