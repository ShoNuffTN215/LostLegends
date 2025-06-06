package net.sho.lostlegends;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.sho.lostlegends.block.ModBlockEntities;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.effect.ModEffects;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.entity.client.CobblestoneGolemRenderer;
import net.sho.lostlegends.item.ModCreativeModeTabs;
import net.sho.lostlegends.item.ModItems;
import net.sho.lostlegends.loot.ModLootModifiers;
import net.sho.lostlegends.painting.ModPaintings;
import net.sho.lostlegends.recipe.ModRecipes;
import net.sho.lostlegends.registry.EntityRegistry;
import net.sho.lostlegends.screen.ForgeOfKnowledgeScreen;
import net.sho.lostlegends.screen.ModMenuTypes;
import net.sho.lostlegends.sound.ModSounds;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LostLegendsMod.MODID)
public class LostLegendsMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "lostlegends";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public LostLegendsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModEntities.register(modEventBus);
        EntityRegistry.register(modEventBus);

        ModEffects.MOB_EFFECTS.register(modEventBus);

        ModPaintings.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        GeckoLib.initialize();


        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);
        ModLootModifiers.register(modEventBus);


        modEventBus.addListener(this::addCreative);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static DeferredRegister<Item> registry(IForgeRegistry<Item> items) {

        return null;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                EntityRenderers.register(ModEntities.COBBLESTONE_GOLEM.get(), CobblestoneGolemRenderer::new);

                MenuScreens.register(ModMenuTypes.FORGE_OF_KNOWLEDGE_MENU.get(), ForgeOfKnowledgeScreen::new);

        });

        }
    }
}
