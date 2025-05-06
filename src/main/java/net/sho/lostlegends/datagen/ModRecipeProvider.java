package net.sho.lostlegends.datagen;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.datagen.custom.ForgeOfKnowledgeRecipeBuilder;
import net.sho.lostlegends.datagen.custom.ForgeOfKnowledgeShapelessRecipeBuilder;
import net.sho.lostlegends.item.ModItems;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private static final List<ItemLike> UNOBTANIUM_SMELTABLES = List.of(ModItems.RAW_UNOBTANIUM.get(),
            ModBlocks.UNOBTANIUM_ORE.get());

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        // Existing recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.UNOBTANIUM_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.UNOBTANIUM.get())
                .unlockedBy("has_unobtanium", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ModItems.UNOBTANIUM.get()).build()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.UNOBTANIUM.get(), 9)
                .requires(ModBlocks.UNOBTANIUM_BLOCK.get())
                .unlockedBy("has_unobtanium", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ModItems.UNOBTANIUM.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RAW_UNOBTANIUM_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.RAW_UNOBTANIUM.get())
                .unlockedBy("has_unobtanium", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ModItems.RAW_UNOBTANIUM.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SCULK_BRICK_STAIRS.get(), 4)
                .pattern("A  ")
                .pattern("AA ")
                .pattern("AAA")
                .define('A', ModBlocks.SCULK_BRICK.get())
                .unlockedBy("has_sculk_brick", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ModBlocks.SCULK_BRICK.get()).build()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SCULK_BRICK_SLAB.get(), 6)
                .pattern("   ")
                .pattern("   ")
                .pattern("AAA")
                .define('A', ModBlocks.SCULK_BRICK.get())
                .unlockedBy("has_sculk_brick", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ModBlocks.SCULK_BRICK.get()).build()))
                .save(pWriter);

        oreSmelting(pWriter, UNOBTANIUM_SMELTABLES, RecipeCategory.MISC, ModItems.UNOBTANIUM.get(), 10, 200, "unobtanium");
        oreBlasting(pWriter, UNOBTANIUM_SMELTABLES, RecipeCategory.MISC, ModItems.UNOBTANIUM.get(), 10, 200, "unobtanium");

        // Add Forge of Knowledge recipes
        buildForgeOfKnowledgeRecipes(pWriter);
    }

    // New method for Forge of Knowledge recipes
    private void buildForgeOfKnowledgeRecipes(Consumer<FinishedRecipe> pWriter) {
        ForgeOfKnowledgeRecipeBuilder.forgeOfKnowledge(ModItems.PRISMARINE_ALLOY.get(), 1)
                .setIngredient(0, ModItems.PURE_PRISMARINE.get())  // Top-left
                .setIngredient(1, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(2, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(3, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(4, Items.IRON_INGOT)  // Top-middle
                .setIngredient(5, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(6, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(7, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(8, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setFateCore(ModItems.FATE_CORE.get())  // Specify the Fate Core
                .unlockedBy("has_pure_prismarine", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ModItems.PURE_PRISMARINE.get()).build()))
                .save(pWriter, new ResourceLocation(LostLegendsMod.MODID, "prismarine_alloy"));

        ForgeOfKnowledgeRecipeBuilder.forgeOfKnowledge(ModItems.FLAMES_OF_CREATION.get(), 1)
                .setIngredient(0, ModItems.PURE_PRISMARINE.get())  // Top-left
                .setIngredient(1, ModItems.PRISMARINE_ALLOY.get())  // Top-middle
                .setIngredient(2, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(3, ModItems.PRISMARINE_ALLOY.get())  // Top-middle
                .setIngredient(4, ModItems.RUINED_FLAMES_OF_CREATION.get())  // Top-middle
                .setIngredient(5, ModItems.PRISMARINE_ALLOY.get())  // Top-middle
                .setIngredient(6, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setIngredient(7, ModItems.PRISMARINE_ALLOY.get())  // Top-middle
                .setIngredient(8, ModItems.PURE_PRISMARINE.get())  // Top-middle
                .setFateCore(ModItems.FATE_CORE.get())  // Specify the Fate Core
                .unlockedBy("ruined_flames_of_creation", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(ModItems.RUINED_FLAMES_OF_CREATION.get()).build()))
                .save(pWriter, new ResourceLocation(LostLegendsMod.MODID, "flames_repair"));

        ForgeOfKnowledgeShapelessRecipeBuilder.shapeless(ModItems.PURE_PRISMARINE.get())
                .requires(Items.PRISMARINE_SHARD)
                .setFateCore(ModItems.FATE_CORE.get())  // Explicitly set the Fate Core
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(pWriter, new ResourceLocation(LostLegendsMod.MODID, "prismarine_purify"));
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer,
                                     List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime,
                            pCookingSerializer).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, LostLegendsMod.MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}