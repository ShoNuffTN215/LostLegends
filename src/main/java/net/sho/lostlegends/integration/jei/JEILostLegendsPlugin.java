package net.sho.lostlegends.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.block.ModBlocks;
import net.sho.lostlegends.recipe.ForgeOfKnowledgeRecipe;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEILostLegendsPlugin implements IModPlugin {
    public static final RecipeType<ForgeOfKnowledgeRecipe> FORGE_OF_KNOWLEDGE_TYPE =
            RecipeType.create(LostLegendsMod.MODID, "forge_of_knowledge", ForgeOfKnowledgeRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(LostLegendsMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ForgeOfKnowledgeRecipeCategory(
                        registration.getJeiHelpers().getGuiHelper(),
                        FORGE_OF_KNOWLEDGE_TYPE
                )
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<ForgeOfKnowledgeRecipe> forgeRecipes = recipeManager.getAllRecipesFor(ForgeOfKnowledgeRecipe.Type.INSTANCE);
        registration.addRecipes(FORGE_OF_KNOWLEDGE_TYPE, forgeRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FORGE_OF_KNOWLEDGE.get()), FORGE_OF_KNOWLEDGE_TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new ForgeOfKnowledgeTransferHandler(), FORGE_OF_KNOWLEDGE_TYPE);
    }
}
