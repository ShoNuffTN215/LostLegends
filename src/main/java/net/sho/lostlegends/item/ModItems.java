package net.sho.lostlegends.item;

import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.custom.*;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LostLegendsMod.MODID);

    public static final RegistryObject<Item> FATE_CORE = ITEMS.register("fate_core",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM = ITEMS.register("unobtanium",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_UNOBTANIUM = ITEMS.register("raw_unobtanium",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> METAL_DETECTOR = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties()));
    public static final RegistryObject<Item> LIGHTNING_STAFF = ITEMS.register("lightning_staff",
            LightningStaffItem::new);
    public static final RegistryObject<Item> UNBREAKABLES_GAUNTLET = ITEMS.register("unbreakables_gauntlet",
            () -> new UnbreakablesGauntletItem(new Item.Properties()
                    .durability(100).rarity(Rarity.UNCOMMON)// Set durability
                    .fireResistant(), // Optional: make it fire-resistant
                    20, // 1 second cooldown (20 ticks)
                    10.0F)); // Fireball power/explosion radius
    public static final RegistryObject<Item> DEVOURERS_PUSTULE = ITEMS.register("devourers_pustule",
            () -> new DevourersPustuleItem(new Item.Properties()
                    .durability(10000) // Set durability - this is how many times it can restore hunger
                    .rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> SCULKBERRY = ITEMS.register("sculkberry",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SCULKBERRY)));

    public static final RegistryObject<Item> UNOBTANIUM_KATANA = ITEMS.register("unobtanium_katana",
            () -> new SwordItem(ModToolTiers.UNOBTANIUM, 100, 10, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_PICKAXE = ITEMS.register("unobtanium_pickaxe",
            () -> new PickaxeItem(ModToolTiers.UNOBTANIUM, 30, 10, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_AXE = ITEMS.register("unobtanium_axe",
            () -> new AxeItem(ModToolTiers.UNOBTANIUM, 60, 10, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_SHOVEL = ITEMS.register("unobtanium_shovel",
            () -> new ShovelItem(ModToolTiers.UNOBTANIUM, 15, 10, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_HOE = ITEMS.register("unobtanium_hoe",
            () -> new HoeItem(ModToolTiers.UNOBTANIUM, 10, 10, new Item.Properties()));

    public static final RegistryObject<Item> UNOBTANIUM_WASTE = ITEMS.register("unobtanium_waste",
            () -> new FuelItem(new Item.Properties(), 2000));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
