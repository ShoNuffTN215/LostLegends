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
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.recipe.ForgeOfKnowledgeRecipe;

public class ForgeOfKnowledgeRecipeCategory implements IRecipeCategory<ForgeOfKnowledgeRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final RecipeType<ForgeOfKnowledgeRecipe> type;

    // This is the texture for your recipe GUI
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(LostLegendsMod.MODID, "textures/gui/forge_of_knowledge_jei.png");

    public ForgeOfKnowledgeRecipeCategory(IGuiHelper guiHelper, RecipeType<ForgeOfKnowledgeRecipe> type) {
        this.type = type;
        // Adjust these values based on your texture's dimensions and layout
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModBlocks.FORGE_OF_KNOWLEDGE.get()));
    }

    @Override
    public RecipeType<ForgeOfKnowledgeRecipe> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.lostlegends.forge_of_knowledge");
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
        // Set up the input slots (3x3 grid)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                if (index < recipe.getIngredients().size()) {
                    // Adjust these coordinates based on your texture layout
                    builder.addSlot(RecipeIngredientRole.INPUT, 30 + col * 18, 17 + row * 18)
                            .addIngredients(recipe.getIngredients().get(index));
                }
            }
        }

        // Add the fate core slot using the getFateCore method
        // Adjust coordinates as needed
        builder.addSlot(RecipeIngredientRole.INPUT, 95, 56)
                .addIngredients(recipe.getFateCore());

        // Add the output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 124, 35)
                .addItemStack(recipe.getResultItem(null));
    }
}
