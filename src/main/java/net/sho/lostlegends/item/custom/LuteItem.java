package net.sho.lostlegends.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LuteItem extends Item {
    private static final int SEARCH_RADIUS = 20;
    private static final int ALLAY_COUNT = 5;

    // Static counter to track active Allays across all instances
    private static int activeAllayCount = 0;

    // Static set to track active Allay UUIDs
    private static final Set<java.util.UUID> activeAllayUUIDs = new HashSet<>();

    public LuteItem(Properties properties) {
        super(properties);
    }

    // Static method to get active Allay count
    public static int getActiveAllayCount() {
        return activeAllayCount;
    }

    // Static method to increment active Allay count
    public static void incrementActiveAllayCount(java.util.UUID allayUUID) {
        activeAllayCount++;
        activeAllayUUIDs.add(allayUUID);
    }

    // Static method to decrement active Allay count
    public static void decrementActiveAllayCount(java.util.UUID allayUUID) {
        if (activeAllayUUIDs.contains(allayUUID)) {
            activeAllayCount = Math.max(0, activeAllayCount - 1);
            activeAllayUUIDs.remove(allayUUID);
        }
    }

    // Add a new method to check if a block is blacklisted
    private boolean isBlockBlacklisted(Block block) {
        // List of blocks that cannot be harvested
        return block == net.minecraft.world.level.block.Blocks.OBSIDIAN ||
                block == net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN ||
                block == net.minecraft.world.level.block.Blocks.BEDROCK ||
                block == net.minecraft.world.level.block.Blocks.REINFORCED_DEEPSLATE;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos blockPos = context.getClickedPos();
        ItemStack itemStack = context.getItemInHand();

        if (player == null || level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Get the block that was clicked
        BlockState clickedBlockState = level.getBlockState(blockPos);
        Block clickedBlock = clickedBlockState.getBlock();

        // Check if the block is blacklisted
        if (isBlockBlacklisted(clickedBlock)) {
            // Play error sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.PLAYERS, 1.0F, 0.5F);

            // Send message to player
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "This block is too hard for the Allays to harvest!"), true);

            return InteractionResult.FAIL;
        }

        // Check if we've reached the maximum number of active Allays
        if (getActiveAllayCount() >= ALLAY_COUNT) {
            // Play error sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.PLAYERS, 1.0F, 0.5F);

            // Send message to player
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "Maximum of " + ALLAY_COUNT + " Harvester Allays already active!"), true);

            return InteractionResult.FAIL;
        }

        // Find a suitable position for the chest (adjacent to the clicked block)
        BlockPos chestPos = findAdjacentEmptySpace(level, blockPos);
        if (chestPos == null) {
            // No suitable position found
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "No suitable position for the chest found!"), true);
            return InteractionResult.FAIL;
        }

        // Play lute sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);

        // Damage the item
        if (!player.getAbilities().instabuild) {
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
        }

        // Find all matching blocks in radius
        List<BlockPos> matchingBlocks = findMatchingBlocks(level, blockPos, clickedBlock, SEARCH_RADIUS);

        // If no blocks found, inform player
        if (matchingBlocks.isEmpty()) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "No matching blocks found within range!"), true);
            return InteractionResult.SUCCESS;
        }

        // Place a chest at the chosen position
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        // Play chest placement sound
        level.playSound(null, chestPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

        // Spawn allays to collect blocks
        spawnHarvestingAllays(level, player, clickedBlock, matchingBlocks, chestPos, null);

        return InteractionResult.SUCCESS;
    }

    // Add a new method to find an adjacent empty space for the chest
    private BlockPos findAdjacentEmptySpace(Level level, BlockPos centerPos) {
        // Check all six directions (up, down, north, south, east, west)
        BlockPos[] adjacentPositions = new BlockPos[]{
                centerPos.above(),
                centerPos.below(),
                centerPos.north(),
                centerPos.south(),
                centerPos.east(),
                centerPos.west()
        };

        // First try positions that have a solid block below them (for stability)
        for (BlockPos pos : adjacentPositions) {
            if (level.getBlockState(pos).isAir() &&
                    level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)) {
                return pos;
            }
        }

        // If no ideal position found, accept any empty space
        for (BlockPos pos : adjacentPositions) {
            if (level.getBlockState(pos).isAir() ||
                    level.getBlockState(pos).canBeReplaced()) {
                return pos;
            }
        }

        // No suitable position found
        return null;
    }

    // Modify the findMatchingBlocks method to include the center block and strictly enforce boundaries
    private List<BlockPos> findMatchingBlocks(Level level, BlockPos center, Block targetBlock, int radius) {
        List<BlockPos> matchingBlocks = new ArrayList<>();

        // If the target block is blacklisted, return an empty list
        if (isBlockBlacklisted(targetBlock)) {
            return matchingBlocks;
        }

        // First, add the center block (the one that was clicked)
        matchingBlocks.add(center.immutable());

        // Custom search dimensions: 10 blocks outward, 3 blocks downward
        int horizontalRadius = 10;
        int upwardRadius = radius; // Keep the same upward radius
        int downwardRadius = 3;

        for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
            for (int y = -downwardRadius; y <= upwardRadius; y++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    BlockPos pos = center.offset(x, y, z);

                    // Skip the center block as we've already added it
                    if (pos.equals(center)) {
                        continue;
                    }

                    // Check if the block at this position matches the target block
                    if (level.getBlockState(pos).getBlock() == targetBlock) {
                        matchingBlocks.add(pos.immutable());
                    }
                }
            }
        }

        return matchingBlocks;
    }

    // Modify the spawnHarvestingAllays method to handle the case where originalBlockState is null
    private void spawnHarvestingAllays(Level level, Player player, Block targetBlock, List<BlockPos> matchingBlocks,
                                       BlockPos chestPos, BlockState originalBlockState) {
        if (matchingBlocks.isEmpty()) {
            return;
        }

        // Create a shared block list for all Allays to work from
        net.minecraft.nbt.ListTag blockPosList = new net.minecraft.nbt.ListTag();
        for (BlockPos pos : matchingBlocks) {
            net.minecraft.nbt.CompoundTag posTag = new net.minecraft.nbt.CompoundTag();
            posTag.putInt("X", pos.getX());
            posTag.putInt("Y", pos.getY());
            posTag.putInt("Z", pos.getZ());
            blockPosList.add(posTag);
        }

        // Create a shared ID for this harvesting task
        String harvestTaskId = java.util.UUID.randomUUID().toString();

        // Spawn a single Allay
        Allay allay = EntityType.ALLAY.create(level);
        if (allay != null) {
            Vec3 spawnPos = player.position().add(
                    Math.cos(0) * 2,
                    1.0,
                    Math.sin(0) * 2
            );

            allay.setPos(spawnPos);

            // Make the Allay invincible
            allay.setInvulnerable(true);

            // Store harvesting data in the allay
            allay.getPersistentData().putInt("HarvestingAllay", 1);
            allay.getPersistentData().putUUID("OwnerUUID", player.getUUID());
            allay.getPersistentData().putString("TargetBlock", targetBlock.getDescriptionId());
            allay.getPersistentData().put("BlocksToHarvest", blockPosList);
            allay.getPersistentData().putString("HarvestTaskId", harvestTaskId);
            allay.getPersistentData().putInt("TotalBlocks", matchingBlocks.size());
            allay.getPersistentData().putInt("ReturningToOwner", 0);
            allay.getPersistentData().putInt("StateTimer", 0);

            // Store the center position and radius for boundary checking
            allay.getPersistentData().putInt("CenterX", chestPos.getX());
            allay.getPersistentData().putInt("CenterY", chestPos.getY());
            allay.getPersistentData().putInt("CenterZ", chestPos.getZ());
            allay.getPersistentData().putInt("HorizontalRadius", 10);
            allay.getPersistentData().putInt("UpwardRadius", 20);
            allay.getPersistentData().putInt("DownwardRadius", 3);

            // Store maximum operation time (5 minutes = 6000 ticks)
            allay.getPersistentData().putLong("MaxOperationTime", level.getGameTime() + 6000L);

            // Store chest position
            allay.getPersistentData().putInt("ChestPosX", chestPos.getX());
            allay.getPersistentData().putInt("ChestPosY", chestPos.getY());
            allay.getPersistentData().putInt("ChestPosZ", chestPos.getZ());

            // Store original block state if provided
            if (originalBlockState != null) {
                allay.getPersistentData().putString("OriginalBlockId", originalBlockState.getBlock().getDescriptionId());
            }

            // Register this Allay as active
            incrementActiveAllayCount(allay.getUUID());

            // Spawn the allay in the world
            level.addFreshEntity(allay);

            // Add particles for the summoning effect
            for (int j = 0; j < 20; j++) {
                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.NOTE,
                        allay.getX() + (level.random.nextDouble() - 0.5) * 0.5,
                        allay.getY() + level.random.nextDouble() * 1.0,
                        allay.getZ() + (level.random.nextDouble() - 0.5) * 0.5,
                        level.random.nextDouble() * 0.1,
                        level.random.nextDouble() * 0.1,
                        level.random.nextDouble() * 0.1
                );
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.lostlegends.lute_tooltip.shift"));
        super.appendHoverText(stack, level, tooltip, flag);

    }
}
