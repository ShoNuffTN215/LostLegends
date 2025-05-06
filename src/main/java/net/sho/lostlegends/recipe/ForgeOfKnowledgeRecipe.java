package net.sho.lostlegends.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.ModItems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ForgeOfKnowledgeRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final Ingredient fateCore;
    private final ItemStack output;
    private final ResourceLocation id;
    private final boolean isShapeless;

    public ForgeOfKnowledgeRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputItems,
                                  Ingredient fateCore, boolean isShapeless) {
        this.inputItems = inputItems;
        this.fateCore = fateCore;
        this.output = output;
        this.id = id;
        this.isShapeless = isShapeless;
    }

    // Constructor overload for backward compatibility
    public ForgeOfKnowledgeRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputItems, Ingredient fateCore) {
        this(id, output, inputItems, fateCore, false);
    }

    // Constructor overload for backward compatibility
    public ForgeOfKnowledgeRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputItems) {
        this(id, output, inputItems, Ingredient.of(ModItems.FATE_CORE.get()), false);
    }

    @Override
    public boolean matches(SimpleContainer inventory, Level level) {
        // Check if we have enough slots in the inventory
        if (inventory.getContainerSize() < 10) {
            return false;
        }

        // Check if the Fate Core slot contains a valid item
        if (!this.fateCore.test(inventory.getItem(9))) {
            return false;
        }

        if (isShapeless) {
            // For shapeless recipes, we need to check if all ingredients are present in any order
            List<ItemStack> inventoryItems = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    inventoryItems.add(stack);
                }
            }

            // Create a copy of ingredients to work with
            List<Ingredient> remainingIngredients = new ArrayList<>();
            for (Ingredient ingredient : this.getIngredients()) {
                if (ingredient != Ingredient.EMPTY) {
                    remainingIngredients.add(ingredient);
                }
            }

            // Try to match each inventory item with an ingredient
            for (ItemStack stack : inventoryItems) {
                boolean foundMatch = false;
                Iterator<Ingredient> iterator = remainingIngredients.iterator();

                while (iterator.hasNext()) {
                    Ingredient ingredient = iterator.next();
                    if (ingredient.test(stack)) {
                        iterator.remove();
                        foundMatch = true;
                        break;
                    }
                }

                // If we couldn't match this item, the recipe doesn't match
                if (!foundMatch) {
                    return false;
                }
            }

            // If we've matched all non-empty ingredients, the recipe matches
            return remainingIngredients.isEmpty();
        } else {
            // For shaped recipes, use the existing logic
            for (int i = 0; i < 9; i++) {
                Ingredient ingredient = this.getIngredients().get(i);
                ItemStack itemStack = inventory.getItem(i);

                if (!ingredient.test(itemStack)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }

    public Ingredient getFateCore() {
        return this.fateCore;
    }

    public boolean isShapeless() {
        return this.isShapeless;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ForgeOfKnowledgeRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "forge_knowledge";
    }

    public static class Serializer implements RecipeSerializer<ForgeOfKnowledgeRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(LostLegendsMod.MODID, "forge_knowledge");

        @Override
        public ForgeOfKnowledgeRecipe fromJson(ResourceLocation id, JsonObject json) {
            // Parse the output item
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            // Get the ingredients array from JSON
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");

            // Create a list for our ingredients with the correct size (9 input slots)
            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);

            // Safely parse each ingredient
            for (int i = 0; i < Math.min(ingredients.size(), inputs.size()); i++) {
                // Only process if the index is valid in the JSON array
                if (i < ingredients.size()) {
                    inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
                }
                // If JSON has fewer ingredients than expected, the remaining slots stay as EMPTY
            }

            // Parse the Fate Core ingredient if present, otherwise use the default
            Ingredient fateCore = Ingredient.of(ModItems.FATE_CORE.get()); // Default
            if (json.has("fate_core")) {
                fateCore = Ingredient.fromJson(json.get("fate_core"));
            }

            // Check if this is a shapeless recipe
            boolean isShapeless = GsonHelper.getAsBoolean(json, "shapeless", false);

            return new ForgeOfKnowledgeRecipe(id, output, inputs, fateCore, isShapeless);
        }

        @Override
        public ForgeOfKnowledgeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            // Read the number of ingredients
            int size = buf.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);

            // Read each ingredient
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            // Read the Fate Core ingredient
            Ingredient fateCore = Ingredient.fromNetwork(buf);

            // Read the output item
            ItemStack output = buf.readItem();

            // Read the shapeless flag
            boolean isShapeless = buf.readBoolean();

            return new ForgeOfKnowledgeRecipe(id, output, inputs, fateCore, isShapeless);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ForgeOfKnowledgeRecipe recipe) {
            // Write the number of ingredients
            buf.writeInt(recipe.getIngredients().size());

            // Write each ingredient
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }

            // Write the Fate Core ingredient
            recipe.getFateCore().toNetwork(buf);

            // Write the output item
            buf.writeItemStack(recipe.getResultItem(null), false);

            // Write the shapeless flag
            buf.writeBoolean(recipe.isShapeless());
        }
    }
}