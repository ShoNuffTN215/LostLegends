package net.sho.lostlegends.datagen.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.ModItems;
import net.sho.lostlegends.recipe.ForgeOfKnowledgeRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ForgeOfKnowledgeShapelessRecipeBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgeOfKnowledgeShapelessRecipeBuilder.class);

    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private Ingredient fateCore;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>(); // Changed to store the actual instances
    private boolean fateCoreSet = false;

    private ForgeOfKnowledgeShapelessRecipeBuilder(ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
        this.fateCore = Ingredient.of(ModItems.FATE_CORE.get()); // Default Fate Core
    }

    public static ForgeOfKnowledgeShapelessRecipeBuilder shapeless(ItemLike result) {
        return shapeless(result, 1);
    }

    public static ForgeOfKnowledgeShapelessRecipeBuilder shapeless(ItemLike result, int count) {
        return new ForgeOfKnowledgeShapelessRecipeBuilder(result, count);
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; i++) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder requires(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder setFateCore(ItemLike item) {
        this.fateCore = Ingredient.of(item);
        this.fateCoreSet = true;
        return this;
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder setFateCore(TagKey<Item> tag) {
        this.fateCore = Ingredient.of(tag);
        this.fateCoreSet = true;
        return this;
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder setFateCore(Ingredient ingredient) {
        this.fateCore = ingredient;
        this.fateCoreSet = true;
        return this;
    }

    public ForgeOfKnowledgeShapelessRecipeBuilder unlockedBy(String criterionName, CriterionTriggerInstance trigger) {
        this.criteria.put(criterionName, trigger);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No criteria added to the recipe");
        }

        // Warn if no Fate Core was explicitly set (though we have a default)
        if (!fateCoreSet) {
            LOGGER.warn("No Fate Core explicitly set for recipe {}. Using default Fate Core.", id);
        }

        consumer.accept(new Result(
                id,
                this.result,
                this.count,
                this.ingredients,
                this.fateCore,
                this.criteria
        ));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final List<Ingredient> ingredients;
        private final Ingredient fateCore;
        private final Map<String, CriterionTriggerInstance> criteria;
        private final ResourceLocation advancementId;
        private final Advancement.Builder advancementBuilder;

        public Result(ResourceLocation id, Item result, int count, List<Ingredient> ingredients,
                      Ingredient fateCore, Map<String, CriterionTriggerInstance> criteria) {
            this.id = id;
            this.result = result;
            this.count = count;
            this.ingredients = ingredients;
            this.fateCore = fateCore;
            this.criteria = criteria;

            // Create advancement
            this.advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
            this.advancementBuilder = Advancement.Builder.advancement();

            // Add criteria to advancement
            for (Map.Entry<String, CriterionTriggerInstance> entry : criteria.entrySet()) {
                this.advancementBuilder.addCriterion(entry.getKey(), entry.getValue());
            }

            // Add recipe unlocked trigger
            this.advancementBuilder.addCriterion("has_the_recipe",
                            RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(RequirementsStrategy.OR);
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            // Add the shapeless flag
            json.addProperty("shapeless", true);

            // Add the output item
            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (this.count > 1) {
                outputJson.addProperty("count", this.count);
            }
            json.add("output", outputJson);

            // Add the ingredients
            JsonArray ingredientsArray = new JsonArray();
            for (Ingredient ingredient : this.ingredients) {
                ingredientsArray.add(ingredient.toJson());
            }

            // Fill remaining slots with empty ingredients if needed
            while (ingredientsArray.size() < 9) {
                ingredientsArray.add(Ingredient.EMPTY.toJson());
            }

            json.add("ingredients", ingredientsArray);

            // Add the Fate Core - ALWAYS REQUIRED
            json.add("fate_core", this.fateCore.toJson());
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ForgeOfKnowledgeRecipe.Serializer.INSTANCE;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}