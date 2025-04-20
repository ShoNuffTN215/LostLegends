package net.sho.lostlegends.item.custom;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.sho.lostlegends.util.ModTags;

public class PaxelItem extends DiggerItem {
    // Make sure to use a reasonable mining speed
    private static final float MINING_SPEED = 16.0F;

    public PaxelItem(Tier tier, float attackDamage, float attackSpeed, Item.Properties properties) {
        // The first parameter should be attack damage, not mining speed
        // The second parameter is BlockTags.MINEABLE_WITH_PICKAXE, but we'll override this
        super(attackDamage, attackSpeed, tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // This is critical - check if the block is mineable by pickaxe, axe, or shovel
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
                state.is(BlockTags.MINEABLE_WITH_AXE) ||
                state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            return MINING_SPEED;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // Check if the block requires a specific tool level
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            int tier = getTier().getLevel();
            if (state.is(BlockTags.NEEDS_DIAMOND_TOOL) && tier < 3) {
                return false;
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL) && tier < 2) {
                return false;
            } else {
                return tier >= 1 || !state.is(BlockTags.NEEDS_STONE_TOOL);
            }
        }

        // For axe and shovel blocks, just check if they're mineable
        return state.is(BlockTags.MINEABLE_WITH_AXE) ||
                state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    // Support tool actions for all three tools
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) ||
                ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) ||
                ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
    }
}
