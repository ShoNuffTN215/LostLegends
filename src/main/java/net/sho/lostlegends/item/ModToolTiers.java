package net.sho.lostlegends.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.util.ModTags;

import java.util.List;

public class ModToolTiers {
    public static final Tier UNOBTANIUM = TierSortingRegistry.registerTier(new ForgeTier(10, 10000, -1f, -1f, 1000,
            ModTags.Blocks.NEEDS_UNOBTANIUM_TOOL, () -> Ingredient.of(ModItems.UNOBTANIUM.get())),
            new ResourceLocation(LostLegendsMod.MODID, "unobtanium"), List.of(Tiers.NETHERITE), List.of());
}
