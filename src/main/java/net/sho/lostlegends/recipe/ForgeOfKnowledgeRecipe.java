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

public class ForgeOfKnowledgeRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;

    public ForgeOfKnowledgeRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputItems) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer inventory, Level level) {
        // Check if we have enough slots in the inventory
        if (inventory.getContainerSize() < 9) {
            return false;
        }

        // Check each ingredient against the corresponding inventory slot
        for (int i = 0; i < 9; i++) {
            Ingredient ingredient = this.getIngredients().get(i);
            ItemStack itemStack = inventory.getItem(i);

            // If this ingredient doesn't match the item in the slot, recipe doesn't match
            if (!ingredient.test(itemStack)) {
                return false;
            }
        }

        // All ingredients matched their slots
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {
        return output.copy();
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
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
                new ResourceLocation(LostLegendsMod.MODID,"forge_knowledge");

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

            return new ForgeOfKnowledgeRecipe(id, output, inputs);
        }

        @Override
        public ForgeOfKnowledgeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new ForgeOfKnowledgeRecipe(id, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ForgeOfKnowledgeRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(null), false);
        }
    }
}
