package net.sho.lostlegends.item;

import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.item.custom.*;
import net.sho.lostlegends.registry.EntityRegistry;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LostLegendsMod.MODID);

    public static final RegistryObject<Item> UNOBTANIUM = ITEMS.register("unobtanium",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_UNOBTANIUM = ITEMS.register("raw_unobtanium",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> METAL_DETECTOR = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LIGHTNING_STAFF = ITEMS.register("lightning_staff",
            LightningStaffItem::new);

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
    public static final RegistryObject<Item> UNOBTANIUM_PAXEL = ITEMS.register("unobtanium_paxel",
            () -> new PaxelItem(ModToolTiers.UNOBTANIUM, 80, 10, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_HAMMER = ITEMS.register("unobtanium_hammer",
            () -> new HammerItem(ModToolTiers.UNOBTANIUM, 40, 10, new Item.Properties()));

    public static final RegistryObject<Item> UNOBTANIUM_HELMET = ITEMS.register("unobtanium_helmet",
            () -> new ArmorItem(ModArmorMaterials.UNOBTANIUM, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_CHESTPLATE = ITEMS.register("unobtanium_chestplate",
            () -> new ArmorItem(ModArmorMaterials.UNOBTANIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_LEGGING = ITEMS.register("unobtanium_leggings",
            () -> new ArmorItem(ModArmorMaterials.UNOBTANIUM, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> UNOBTANIUM_BOOTS = ITEMS.register("unobtanium_boots",
            () -> new ArmorItem(ModArmorMaterials.UNOBTANIUM, ArmorItem.Type.BOOTS, new Item.Properties()));


    public static final RegistryObject<Item> UNOBTANIUM_WASTE = ITEMS.register("unobtanium_waste",
            () -> new FuelItem(new Item.Properties(), 2000));

    // Armor Items
    public static final RegistryObject<Item> GREAT_HOGS_CROWN = ITEMS.register("great_hogs_crown",
            () -> new GreatHogsCrownItem(ModArmorMaterials.ROYAL, new Item.Properties()));
    public static final RegistryObject<Item> BEAST_MASK = ITEMS.register("beast_mask",
            () -> new BeastMaskItem(ModArmorMaterials.BEAST, ArmorItem.Type.HELMET,
                    new Item.Properties()));

    // Spawn Eggs
    public static final RegistryObject<Item> COBBLESTONE_GOLEM_SPAWN_EGG = ITEMS.register("cobblestone_golem_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.COBBLESTONE_GOLEM, 0x7e9680, 0xc5d1c5, new Item.Properties()));
    public static final RegistryObject<Item> GRINDSTONE_GOLEM_SPAWN_EGG = ITEMS.register("grindstone_golem_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.GRINDSTONE_GOLEM, 0x7e9680, 2986, new Item.Properties()));
    public static final RegistryObject<Item> PLANK_GOLEM_SPAWN_EGG = ITEMS.register("plank_golem_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.PLANK_GOLEM, 0xC7A06D, 0x8B5A2B, new Item.Properties()));
    public static final RegistryObject<Item> CAPYBARA_SPAWN_EGG = ITEMS.register("capybara_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityRegistry.CAPYBARA, 0x8B6D3F, 0x5C4A32, new Item.Properties()));

    //Repairable Items

    public static final RegistryObject<Item> RUINED_FLAMES_OF_CREATION = ITEMS.register("ruined_flames_of_creation",
            () -> new Item(new Item.Properties().stacksTo(1).stacksTo(1)));


    // Functional Items
    public static final RegistryObject<Item> UNBREAKABLES_GAUNTLET = ITEMS.register("unbreakables_gauntlet",
            () -> new UnbreakablesGauntletItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DEVOURERS_PUSTULE = ITEMS.register("devourers_pustule",
            () -> new DevourersPustuleItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FLAMES_OF_CREATION = ITEMS.register("flames_of_creation",
            () -> new FlamesOfCreationItem(new Item.Properties().stacksTo(1).durability(64)));
    public static final RegistryObject<Item> LUTE = ITEMS.register("lute",
            () -> new LuteItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    // Normal Items/Crafting Materials
    public static final RegistryObject<Item> FATE_CORE = ITEMS.register("fate_core",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PURE_PRISMARINE = ITEMS.register("pure_prismarine",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> PRISMARINE_ALLOY = ITEMS.register("prismarine_alloy",
            () -> new Item(new Item.Properties().stacksTo(64)));

    // Block Entity Icons
    public static final RegistryObject<Item> FORGE_OF_KNOWLEDGE_ICON = ITEMS.register("forge_of_knowledge_icon",
            () -> new Item(new Item.Properties()));















    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
