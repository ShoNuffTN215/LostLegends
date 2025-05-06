package net.sho.lostlegends.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.ModItems;
import net.sho.lostlegends.recipe.ForgeOfKnowledgeRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ForgeOfKnowledgeRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final List<Ingredient> ingredients;
    private final int count;
    private Ingredient fateCore; // Added Fate Core ingredient
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String group;

    /**
     * Constructor for a recipe with specific ingredients in specific positions
     */
    private ForgeOfKnowledgeRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
        this.ingredients = new ArrayList<>();
        // Initialize with 9 empty ingredients
        for (int i = 0; i < 9; i++) {
            this.ingredients.add(Ingredient.EMPTY);
        }
        // Default to ModItems.FATE_CORE if it exists
        this.fateCore = Ingredient.of(ModItems.FATE_CORE.get());
    }

    /**
     * Creates a new builder for a Forge of Knowledge recipe.
     */
    public static ForgeOfKnowledgeRecipeBuilder forgeOfKnowledge(ItemLike result, int count) {
        return new ForgeOfKnowledgeRecipeBuilder(result, count);
    }

    /**
     * Sets the Fate Core ingredient for this recipe.
     */
    public ForgeOfKnowledgeRecipeBuilder setFateCore(ItemLike fateCore) {
        this.fateCore = Ingredient.of(fateCore);
        return this;
    }

    /**
     * Sets the Fate Core ingredient for this recipe using an Ingredient.
     */
    public ForgeOfKnowledgeRecipeBuilder setFateCore(Ingredient fateCore) {
        this.fateCore = fateCore;
        return this;
    }

    /**
     * Sets an ingredient at a specific position in the 3x3 grid.
     * Positions are 0-8, with 0 being top-left and 8 being bottom-right.
     */
    public ForgeOfKnowledgeRecipeBuilder setIngredient(int position, ItemLike ingredient) {
        if (position < 0 || position >= 9) {
            throw new IllegalArgumentException("Position must be between 0 and 8");
        }
        this.ingredients.set(position, Ingredient.of(ingredient));
        return this;
    }

    /**
     * Sets an ingredient at a specific position in the 3x3 grid.
     * Positions are 0-8, with 0 being top-left and 8 being bottom-right.
     */
    public ForgeOfKnowledgeRecipeBuilder setIngredient(int position, Ingredient ingredient) {
        if (position < 0 || position >= 9) {
            throw new IllegalArgumentException("Position must be between 0 and 8");
        }
        this.ingredients.set(position, ingredient);
        return this;
    }

    /**
     * Sets an empty ingredient (air) at a specific position.
     */
    public ForgeOfKnowledgeRecipeBuilder setEmptyIngredient(int position) {
        return setIngredient(position, Ingredient.EMPTY);
    }

    /**
     * Convenience method to set all 9 ingredients at once.
     * Any null values will be treated as empty ingredients.
     */
    public ForgeOfKnowledgeRecipeBuilder setIngredients(ItemLike... ingredients) {
        if (ingredients.length > 9) {
            throw new IllegalArgumentException("Cannot have more than 9 ingredients");
        }

        for (int i = 0; i < 9; i++) {
            if (i < ingredients.length && ingredients[i] != null) {
                this.ingredients.set(i, Ingredient.of(ingredients[i]));
            } else {
                this.ingredients.set(i, Ingredient.EMPTY);
            }
        }

        return this;
    }

    /**
     * Convenience method to set all 9 ingredients at once using Ingredients.
     * Any null values will be treated as empty ingredients.
     */
    public ForgeOfKnowledgeRecipeBuilder setIngredients(Ingredient... ingredients) {
        if (ingredients.length > 9) {
            throw new IllegalArgumentException("Cannot have more than 9 ingredients");
        }

        for (int i = 0; i < 9; i++) {
            if (i < ingredients.length && ingredients[i] != null) {
                this.ingredients.set(i, ingredients[i]);
            } else {
                this.ingredients.set(i, Ingredient.EMPTY);
            }
        }

        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId))
                .rewards(AdvancementRewards.Builder.recipe(pRecipeId))
                .requirements(RequirementsStrategy.OR);

        pFinishedRecipeConsumer.accept(new Result(
                pRecipeId,
                this.result,
                this.count,
                this.group == null ? "" : this.group,
                this.ingredients,
                this.fateCore, // Pass the Fate Core to the Result
                this.advancement,
                new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + pRecipeId.getPath())
        ));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation id) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<Ingredient> ingredients;
        private final Ingredient fateCore; // Added Fate Core
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, Item result, int count, String group,
                      List<Ingredient> ingredients, Ingredient fateCore,
                      Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.count = count;
            this.group = group;
            this.ingredients = ingredients;
            this.fateCore = fateCore;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            // Serialize the 9 grid ingredients
            JsonArray ingredientsArray = new JsonArray();
            for (Ingredient ingredient : this.ingredients) {
                ingredientsArray.add(ingredient.toJson());
            }
            json.add("ingredients", ingredientsArray);

            // Add the Fate Core as a separate field
            json.add("fate_core", this.fateCore.toJson());

            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (this.count > 1) {
                resultJson.addProperty("count", this.count);
            }

            json.add("output", resultJson);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ForgeOfKnowledgeRecipe.Serializer.INSTANCE;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}