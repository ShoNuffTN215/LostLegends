package net.sho.lostlegends.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.entity.custom.CobblestoneGolemEntity;
import net.sho.lostlegends.util.ModTags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlamesOfCreationItem extends Item {
    // Use a lazy initialization approach to avoid accessing registry objects too early
    private static Set<Block> validBlocks;

    public FlamesOfCreationItem(Properties properties) {
        super(properties);
    }

    // Lazy initialization of valid blocks
    private static Set<Block> getValidBlocks() {
        if (validBlocks == null) {
            validBlocks = new HashSet<>();
            validBlocks.add(Blocks.COBBLESTONE);
        }
        return validBlocks;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();

        // Don't do anything on the client side
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Check if the block is valid for transformation
        if (getValidBlocks().contains(block)) {
            // Try to spawn the cobblestone golem
            boolean success = spawnCobblestoneGolem(level, blockPos);

            if (success) {
                // Play a creation sound
                level.playSound(null, blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

                // Add particle effects
                level.levelEvent(1018, blockPos, 0);

                // Remove the block
                level.removeBlock(blockPos, false);

                // Damage the item (optional)
                if (!context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().hurtAndBreak(1, context.getPlayer(),
                            player -> player.broadcastBreakEvent(context.getHand()));
                }

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    private boolean spawnCobblestoneGolem(Level level, BlockPos pos) {
        try {
            // Create the cobblestone golem entity
            CobblestoneGolemEntity golem = new CobblestoneGolemEntity(ModEntities.COBBLESTONE_GOLEM.get(), level);

            // Position the entity correctly
            golem.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            // Add the entity to the world
            return level.addFreshEntity(golem);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error spawning Cobblestone Golem: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}