package net.sho.lostlegends.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.recipe.ForgeOfKnowledgeRecipe;

import java.util.ArrayList;
import java.util.List;

public class ForgeOfKnowledgeRecipeCategory implements IRecipeCategory<ForgeOfKnowledgeRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(LostLegendsMod.MODID, "forge_knowledge");
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(LostLegendsMod.MODID, "textures/gui/forge_of_knowledge_jei.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public ForgeOfKnowledgeRecipeCategory(IGuiHelper helper, RecipeType<ForgeOfKnowledgeRecipe> forgeOfKnowledgeType) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FORGE_OF_KNOWLEDGE.get()));
        this.title = Component.translatable("block.lostlegends.forge_of_knowledge");
    }

    @Override
    public RecipeType<ForgeOfKnowledgeRecipe> getRecipeType() {
        return JEILostLegendsPlugin.FORGE_OF_KNOWLEDGE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgeOfKnowledgeRecipe recipe, IFocusGroup focuses) {
        if (recipe.isShapeless()) {
            // For shapeless recipes, display non-empty ingredients in a compact way
            List<Ingredient> nonEmptyIngredients = new ArrayList<>();
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient != Ingredient.EMPTY) {
                    nonEmptyIngredients.add(ingredient);
                }
            }

            // Arrange non-empty ingredients in a grid
            int index = 0;
            for (Ingredient ingredient : nonEmptyIngredients) {
                int row = index / 3;
                int col = index % 3;
                builder.addSlot(RecipeIngredientRole.INPUT, 30 + col * 18, 16 + row * 18)
                        .addIngredients(ingredient);
                index++;
            }

            // Add a shapeless recipe indicator
            // You could add a custom drawable here to indicate it's shapeless
        } else {
            // For shaped recipes, use the standard 3x3 grid layout
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int index = row * 3 + col;
                    if (index < recipe.getIngredients().size()) {
                        builder.addSlot(RecipeIngredientRole.INPUT, 30 + col * 18, 16 + row * 18)
                                .addIngredients(recipe.getIngredients().get(index));
                    }
                }
            }
        }

        // ALWAYS add the fate core slot - this is required for all recipes
        builder.addSlot(RecipeIngredientRole.INPUT, 95, 56)
                .addIngredients(recipe.getFateCore());

        // Add the output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 35)
                .addItemStack(recipe.getResultItem(null));
    }
}