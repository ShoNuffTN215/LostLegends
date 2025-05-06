package net.sho.lostlegends.integration.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.sho.lostlegends.recipe.ForgeOfKnowledgeRecipe;
import net.sho.lostlegends.screen.ForgeOfKnowledgeMenu;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeOfKnowledgeTransferHandler implements IRecipeTransferHandler<ForgeOfKnowledgeMenu, ForgeOfKnowledgeRecipe> {
    @Override
    public Class<ForgeOfKnowledgeMenu> getContainerClass() {
        return ForgeOfKnowledgeMenu.class;
    }

    @Override
    public Optional<MenuType<ForgeOfKnowledgeMenu>> getMenuType() {
        // Return your menu type here if you have one registered
        // If you don't have a registered MenuType, return Optional.empty()
        return Optional.empty(); // Replace with your actual MenuType if available
    }

    @Override
    public RecipeType<ForgeOfKnowledgeRecipe> getRecipeType() {
        return JEILostLegendsPlugin.FORGE_OF_KNOWLEDGE_TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(ForgeOfKnowledgeMenu container, ForgeOfKnowledgeRecipe recipe,
                                                         IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer,
                                                         boolean doTransfer) {
        if (!canHandle(container)) {

        }

        if (doTransfer) {
            // Get the input ingredients from the recipe
            List<ItemStack> recipeInputs = new ArrayList<>();

            // Add the grid ingredients (assuming recipe.getIngredients() returns all ingredients)
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                var ingredient = recipe.getIngredients().get(i);
                if (!ingredient.isEmpty()) {
                    ItemStack[] matchingStacks = ingredient.getItems();
                    if (matchingStacks.length > 0) {
                        recipeInputs.add(matchingStacks[0].copy());
                    } else {
                        recipeInputs.add(ItemStack.EMPTY);
                    }
                } else {
                    recipeInputs.add(ItemStack.EMPTY);
                }
            }

            // Transfer items from player inventory to container slots
            for (int i = 0; i < Math.min(10, recipeInputs.size()); i++) {
                ItemStack recipeStack = recipeInputs.get(i);
                if (!recipeStack.isEmpty()) {
                    // Find matching item in player inventory
                    for (int j = 11; j < container.slots.size(); j++) {
                        Slot inventorySlot = container.slots.get(j);
                        ItemStack inventoryStack = inventorySlot.getItem();

                        if (!inventoryStack.isEmpty() && ItemStack.isSameItemSameTags(inventoryStack, recipeStack)) {
                            // Transfer the item using click operations instead of direct moveItemStackTo
                            ItemStack held = player.containerMenu.getCarried();

                            // Pick up the item from inventory
                            container.clicked(j, 0, net.minecraft.world.inventory.ClickType.PICKUP, player);

                            // Place it in the crafting grid
                            container.clicked(i, 0, net.minecraft.world.inventory.ClickType.PICKUP, player);

                            // If we still have something in hand, put it back
                            if (!player.containerMenu.getCarried().isEmpty()) {
                                container.clicked(j, 0, net.minecraft.world.inventory.ClickType.PICKUP, player);
                            }

                            break;
                        }
                    }
                }
            }
        }

        return null; // null means success
    }

    private boolean canHandle(ForgeOfKnowledgeMenu container) {
        // Add any additional checks here if needed
        return true;
    }
}
