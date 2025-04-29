package net.sho.lostlegends.event.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sho.lostlegends.item.custom.LuteItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public class AllayHarvestingHandler {

    private static final int HARVEST_INTERVAL = 3; // Try to harvest a block every 0.15 seconds (3 ticks)
    private static final int RETURN_INTERVAL = 300; // Return to player every 15 seconds (300 ticks)
    private static final int STUCK_TIMEOUT = 100; // If stuck in a state for 5 seconds (100 ticks), reset
    private static final int RESCAN_INTERVAL = 60; // Rescan for missed blocks every 3 seconds (60 ticks)
    private static final int MAX_CONSECUTIVE_EMPTY_SCANS = 3; // After this many empty scans, consider harvesting complete

    // Map to track chest positions and their associated Allays
    private static final Map<BlockPos, UUID> chestToAllayMap = new HashMap<>();

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Allay allay) {
            // Check if this is our harvesting allay joining the level
            if (allay.getPersistentData().contains("HarvestingAllay")) {
                // Initialize state tracking
                allay.getPersistentData().putInt("StateTimer", 0);
                allay.getPersistentData().putInt("ReturningToOwner", 0);
                allay.getPersistentData().putInt("LastBlockCount", -1);
                allay.getPersistentData().putInt("EmptyScanCount", 0);
                allay.getPersistentData().putInt("FinalDelivery", 0);

                // Make sure the Allay is invincible
                allay.setInvulnerable(true);

                // Register the chest position with this Allay if it exists
                if (allay.getPersistentData().contains("ChestPosX")) {
                    int x = allay.getPersistentData().getInt("ChestPosX");
                    int y = allay.getPersistentData().getInt("ChestPosY");
                    int z = allay.getPersistentData().getInt("ChestPosZ");
                    BlockPos chestPos = new BlockPos(x, y, z);

                    // Associate this chest position with the Allay's UUID
                    chestToAllayMap.put(chestPos, allay.getUUID());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Allay allay) {
            // Check if this is our harvesting allay leaving the level
            if (allay.getPersistentData().contains("HarvestingAllay")) {
                // Decrement the active allay count
                LuteItem.decrementActiveAllayCount(allay.getUUID());

                // Remove any chest associations for this Allay
                if (allay.getPersistentData().contains("ChestPosX")) {
                    int x = allay.getPersistentData().getInt("ChestPosX");
                    int y = allay.getPersistentData().getInt("ChestPosY");
                    int z = allay.getPersistentData().getInt("ChestPosZ");
                    BlockPos chestPos = new BlockPos(x, y, z);

                    chestToAllayMap.remove(chestPos);
                }
            }
        }
    }

    // Add event handler for block break events
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos pos = event.getPos();

        // Check if this is one of our tracked chests
        if (chestToAllayMap.containsKey(pos)) {
            UUID allayUUID = chestToAllayMap.get(pos);

            // Find the Allay with this UUID
            event.getLevel().getEntitiesOfClass(Allay.class,
                            new net.minecraft.world.phys.AABB(pos).inflate(50),
                            allay -> allay.getUUID().equals(allayUUID))
                    .forEach(allay -> {
                        // First, ensure all items are delivered to the player
                        if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                            UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                            Player owner = event.getPlayer().level().getPlayerByUUID(ownerUUID);

                            if (owner != null) {
                                deliverItemsToPlayer(allay, owner);
                            }
                        }

                        // Create disappearing effect
                        for (int i = 0; i < 15; i++) {
                            event.getLevel().addParticle(
                                    net.minecraft.core.particles.ParticleTypes.NOTE,
                                    allay.getX() + (event.getLevel().getRandom().nextDouble() - 0.5) * 0.5,
                                    allay.getY() + event.getLevel().getRandom().nextDouble() * 1.0,
                                    allay.getZ() + (event.getLevel().getRandom().nextDouble() - 0.5) * 0.5,
                                    event.getLevel().getRandom().nextDouble() * 0.1,
                                    event.getLevel().getRandom().nextDouble() * 0.1,
                                    event.getLevel().getRandom().nextDouble() * 0.1
                            );
                        }

                        // Add explosion particles
                        for (int i = 0; i < 10; i++) {
                            event.getLevel().addParticle(
                                    net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                                    allay.getX() + (event.getLevel().getRandom().nextDouble() - 0.5) * 0.5,
                                    allay.getY() + event.getLevel().getRandom().nextDouble() * 0.5,
                                    allay.getZ() + (event.getLevel().getRandom().nextDouble() - 0.5) * 0.5,
                                    0, 0, 0
                            );
                        }

                        // Notify the player if possible
                        if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                            UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                            Player owner = event.getPlayer().level().getPlayerByUUID(ownerUUID);

                            if (owner != null) {

                            }
                        }

                        // Remove the Allay and decrement counter
                        LuteItem.decrementActiveAllayCount(allay.getUUID());
                        allay.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
                    });

            // Remove the chest from our tracking map
            chestToAllayMap.remove(pos);
        }
    }

    @SubscribeEvent
    public static void onAllayTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Allay allay)) {
            return;
        }

        // Check if this is our harvesting allay
        if (!allay.getPersistentData().contains("HarvestingAllay")) {
            return;
        }

        // Check if the chest still exists
        if (allay.getPersistentData().contains("ChestPosX")) {
            int x = allay.getPersistentData().getInt("ChestPosX");
            int y = allay.getPersistentData().getInt("ChestPosY");
            int z = allay.getPersistentData().getInt("ChestPosZ");
            BlockPos chestPos = new BlockPos(x, y, z);

            // If the chest is gone but we didn't catch the break event, remove the Allay
            if (!allay.level().getBlockState(chestPos).is(Blocks.CHEST)) {
                // Only if we're still tracking this chest-allay pair
                if (chestToAllayMap.containsKey(chestPos) && chestToAllayMap.get(chestPos).equals(allay.getUUID())) {
                    // First, ensure all items are delivered to the player
                    if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                        UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                        Player owner = allay.level().getPlayerByUUID(ownerUUID);

                        if (owner != null) {
                            deliverItemsToPlayer(allay, owner);
                        }
                    }

                    // Create disappearing effect
                    for (int i = 0; i < 15; i++) {
                        allay.level().addParticle(
                                net.minecraft.core.particles.ParticleTypes.NOTE,
                                allay.getX() + (allay.level().random.nextDouble() - 0.5) * 0.5,
                                allay.getY() + allay.level().random.nextDouble() * 1.0,
                                allay.getZ() + (allay.level().random.nextDouble() - 0.5) * 0.5,
                                allay.level().random.nextDouble() * 0.1,
                                allay.level().random.nextDouble() * 0.1,
                                allay.level().random.nextDouble() * 0.1
                        );
                    }

                    // Notify the player if possible
                    if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                        UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                        Player owner = allay.level().getPlayerByUUID(ownerUUID);

                        if (owner != null) {
                            owner.displayClientMessage(net.minecraft.network.chat.Component.literal(
                                    "Your Harvester Allay has vanished because its chest was destroyed!"), false);
                        }
                    }

                    // Remove the chest from our tracking map
                    chestToAllayMap.remove(chestPos);

                    // Remove the allay and decrement counter
                    LuteItem.decrementActiveAllayCount(allay.getUUID());
                    allay.remove(net.minecraft.world.entity.Entity.RemovalReason.KILLED);
                    return;
                }
            }
        }

        // Get the game time
        long gameTime = allay.level().getGameTime();

        // Increment state timer to detect stuck states
        int stateTimer = allay.getPersistentData().getInt("StateTimer") + 1;
        allay.getPersistentData().putInt("StateTimer", stateTimer);

        // Check if we're stuck in a state for too long
        if (stateTimer > STUCK_TIMEOUT) {
            // Reset state and force harvesting mode
            allay.getPersistentData().putInt("StateTimer", 0);
            allay.getPersistentData().putInt("ReturningToOwner", 0);

            // Debug particle to show state reset
            allay.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER,
                    allay.getX(), allay.getY() + 0.5, allay.getZ(),
                    0, 0, 0
            );
        }

        // Find nearby item entities and collect them
        allay.level().getEntitiesOfClass(ItemEntity.class,
                        allay.getBoundingBox().inflate(2.0),
                        itemEntity -> !itemEntity.hasPickUpDelay())
                .forEach(itemEntity -> {
                    // Collect the item
                    ItemStack stack = itemEntity.getItem().copy();
                    itemEntity.discard();

                    // Create particles to show item collection
                    allay.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.ITEM_SLIME,
                            itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                            0, 0.1, 0);

                    // Store the item in the allay's data
                    if (!allay.getPersistentData().contains("CollectedItems")) {
                        allay.getPersistentData().putInt("CollectedItems", 0);
                    }

                    int itemCount = allay.getPersistentData().getInt("CollectedItems");
                    allay.getPersistentData().put("CollectedItem" + itemCount, stack.save(new net.minecraft.nbt.CompoundTag()));
                    allay.getPersistentData().putInt("CollectedItems", itemCount + 1);
                });

        // Check if there are any blocks left to harvest
        net.minecraft.nbt.ListTag blocksList = allay.getPersistentData().getList("BlocksToHarvest", 10);

        // Track block count to detect if we're making progress
        int currentBlockCount = blocksList.size();
        int lastBlockCount = allay.getPersistentData().getInt("LastBlockCount");

        if (lastBlockCount == currentBlockCount && lastBlockCount != -1) {
            // If block count hasn't changed for a while and we're not returning, we might be stuck
            int stuckCounter = allay.getPersistentData().getInt("StuckCounter") + 1;
            allay.getPersistentData().putInt("StuckCounter", stuckCounter);

            if (stuckCounter > 60 && allay.getPersistentData().getInt("ReturningToOwner") == 0) {
                // We're stuck, try to find a new target
                allay.getPersistentData().putInt("StuckCounter", 0);

                // Debug particle to show stuck detection
                allay.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.SMOKE,
                        allay.getX(), allay.getY() + 0.5, allay.getZ(),
                        0, 0.1, 0
                );

                // Move randomly to try to get unstuck
                double x = allay.getX() + (allay.level().random.nextDouble() - 0.5) * 10;
                double y = allay.getY() + (allay.level().random.nextDouble() - 0.5) * 5;
                double z = allay.getZ() + (allay.level().random.nextDouble() - 0.5) * 10;

                allay.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET,
                        new net.minecraft.world.entity.ai.memory.WalkTarget(new Vec3(x, y, z), 2.0F, 1));
            }
        } else {
            // Reset stuck counter if we're making progress
            allay.getPersistentData().putInt("StuckCounter", 0);
            allay.getPersistentData().putInt("LastBlockCount", currentBlockCount);
        }

        // Check if we're in final delivery mode
        if (allay.getPersistentData().getInt("FinalDelivery") == 1) {
            // We're in final delivery mode, check if we're close to the player
            if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                Player owner = allay.level().getPlayerByUUID(ownerUUID);

                if (owner != null) {
                    // Move towards the player
                    allay.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET,
                            new net.minecraft.world.entity.ai.memory.WalkTarget(owner.position(), 2.0F, 1));

                    // If close enough, deliver items and finish
                    if (allay.distanceToSqr(owner) < 4.0) {
                        deliverItemsToPlayer(allay, owner);

                        // Inform player that harvesting is complete
                        int totalBlocks = allay.getPersistentData().getInt("TotalBlocks");
                        owner.displayClientMessage(net.minecraft.network.chat.Component.literal(
                                "Harvesting complete! Collected " + totalBlocks + " blocks."), false);

                        // Remove the temporary chest
                        removeTemporaryChest(allay);

                        // Create disappearing effect
                        createDisappearingEffect(allay);

                        // Remove the allay and decrement counter
                        LuteItem.decrementActiveAllayCount(allay.getUUID());
                        allay.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
                        return;
                    }
                }
            }
            return; // Skip other processing if in final delivery mode
        }

        // If no blocks left or we've done enough empty scans, start final delivery
        if (blocksList.isEmpty() || allay.getPersistentData().getInt("EmptyScanCount") >= MAX_CONSECUTIVE_EMPTY_SCANS) {
            // First check if we have any items to deliver
            if (allay.getPersistentData().contains("CollectedItems") &&
                    allay.getPersistentData().getInt("CollectedItems") > 0) {

                // Set final delivery mode
                allay.getPersistentData().putInt("FinalDelivery", 1);

                // Create particles to show final delivery mode
                allay.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.HEART,
                        allay.getX(), allay.getY() + 0.5, allay.getZ(),
                        0, 0.1, 0
                );

                // Move towards the player
                if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                    UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                    Player owner = allay.level().getPlayerByUUID(ownerUUID);

                    if (owner != null) {
                        allay.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET,
                                new net.minecraft.world.entity.ai.memory.WalkTarget(owner.position(), 2.0F, 1));
                    }
                }
                return; // Skip other processing
            } else {
                // No items to deliver, just finish up
                if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                    UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                    Player owner = allay.level().getPlayerByUUID(ownerUUID);

                    if (owner != null) {
                        // Inform player that harvesting is complete
                        int totalBlocks = allay.getPersistentData().getInt("TotalBlocks");
                        owner.displayClientMessage(net.minecraft.network.chat.Component.literal(
                                "Harvesting complete! Collected " + totalBlocks + " blocks."), false);
                    }
                }

                // Remove the temporary chest
                removeTemporaryChest(allay);

                // Create disappearing effect
                createDisappearingEffect(allay);

                // Remove the allay and decrement counter
                LuteItem.decrementActiveAllayCount(allay.getUUID());
                allay.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
                return;
            }
        }

        // Check if it's time to return to the player
        if (gameTime % RETURN_INTERVAL == 0 && allay.getPersistentData().getInt("ReturningToOwner") == 0) {
            if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                Player owner = allay.level().getPlayerByUUID(ownerUUID);

                if (owner != null) {
                    // Set the allay to return to the player
                    allay.getPersistentData().putInt("ReturningToOwner", 1);
                    allay.getPersistentData().putInt("StateTimer", 0); // Reset state timer

                    allay.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET,
                            new net.minecraft.world.entity.ai.memory.WalkTarget(owner.position(), 2.0F, 1));

                    // Create particles to show returning state
                    allay.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.HEART,
                            allay.getX(), allay.getY() + 0.5, allay.getZ(),
                            0, 0.1, 0
                    );
                }
            }
        } else if (allay.getPersistentData().getInt("ReturningToOwner") == 1) {
            // We're in returning state, check if we're close to the player
            if (allay.getPersistentData().hasUUID("OwnerUUID")) {
                UUID ownerUUID = allay.getPersistentData().getUUID("OwnerUUID");
                Player owner = allay.level().getPlayerByUUID(ownerUUID);

                if (owner != null && allay.distanceToSqr(owner) < 4.0) {
                    // We've reached the player, deliver items and reset state
                    deliverItemsToPlayer(allay, owner);
                    allay.getPersistentData().putInt("ReturningToOwner", 0);
                    allay.getPersistentData().putInt("StateTimer", 0); // Reset state timer

                    // Create particles to show state change
                    allay.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                            allay.getX(), allay.getY() + 0.5, allay.getZ(),
                            0, 0.1, 0
                    );
                }
            }
        } else if (gameTime % HARVEST_INTERVAL == 0) {
            // We're in harvesting state, try to find and harvest a block
            tryHarvestNextBlock(allay);
        }

        // Periodically rescan for missed blocks
        if (gameTime % RESCAN_INTERVAL == 0 && allay.getPersistentData().getInt("ReturningToOwner") == 0) {
            boolean foundNewBlocks = rescanForMissedBlocks(allay);

            // Update empty scan counter
            if (!foundNewBlocks) {
                int emptyScanCount = allay.getPersistentData().getInt("EmptyScanCount") + 1;
                allay.getPersistentData().putInt("EmptyScanCount", emptyScanCount);
            } else {
                // Reset empty scan counter if we found new blocks
                allay.getPersistentData().putInt("EmptyScanCount", 0);
            }
        }
    }

    // Helper method to create disappearing effect
    private static void createDisappearingEffect(Allay allay) {
        for (int i = 0; i < 15; i++) {
            allay.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.NOTE,
                    allay.getX() + (allay.level().random.nextDouble() - 0.5) * 0.5,
                    allay.getY() + allay.level().random.nextDouble() * 1.0,
                    allay.getZ() + (allay.level().random.nextDouble() - 0.5) * 0.5,
                    allay.level().random.nextDouble() * 0.1,
                    allay.level().random.nextDouble() * 0.1,
                    allay.level().random.nextDouble() * 0.1
            );
        }
    }

    // Helper method to remove temporary chest
    private static void removeTemporaryChest(Allay allay) {
        if (allay.getPersistentData().contains("ChestPosX")) {
            int x = allay.getPersistentData().getInt("ChestPosX");
            int y = allay.getPersistentData().getInt("ChestPosY");
            int z = allay.getPersistentData().getInt("ChestPosZ");
            BlockPos chestPos = new BlockPos(x, y, z);

            // Remove from tracking map
            chestToAllayMap.remove(chestPos);

            // Check if the chest is still there
            if (allay.level().getBlockState(chestPos).getBlock() == Blocks.CHEST) {
                // Remove the chest
                allay.level().removeBlock(chestPos, false);

                // Create particles for chest removal
                for (int i = 0; i < 10; i++) {
                    allay.level().addParticle(
                            net.minecraft.core.particles.ParticleTypes.POOF,
                            chestPos.getX() + 0.5 + (allay.level().random.nextDouble() - 0.5) * 0.5,
                            chestPos.getY() + 0.5 + allay.level().random.nextDouble() * 0.5,
                            chestPos.getZ() + 0.5 + (allay.level().random.nextDouble() - 0.5) * 0.5,
                            0, 0.05, 0
                    );
                }

                // Play sound for chest removal
                allay.level().playSound(null, chestPos,
                        net.minecraft.sounds.SoundEvents.WOOD_BREAK,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    // Add a method to check if a block is blacklisted
    private static boolean isBlockBlacklisted(Block block) {
        // List of blocks that cannot be harvested
        return block == net.minecraft.world.level.block.Blocks.OBSIDIAN ||
                block == net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN ||
                block == net.minecraft.world.level.block.Blocks.BEDROCK ||
                block == net.minecraft.world.level.block.Blocks.REINFORCED_DEEPSLATE;
    }

    // Modify the tryHarvestNextBlock method to check for blacklisted blocks
    private static void tryHarvestNextBlock(Allay allay) {
        // Get the target block type
        String targetBlockId = allay.getPersistentData().getString("TargetBlock");
        Block targetBlock = null;

        // Find the block from its ID
        for (Block block : ForgeRegistries.BLOCKS) {
            if (block.getDescriptionId().equals(targetBlockId)) {
                targetBlock = block;
                break;
            }
        }

        if (targetBlock == null) {
            return;
        }

        // Check if the target block is blacklisted
        if (isBlockBlacklisted(targetBlock)) {
            // Create error particles
            allay.level().addParticle(
                    ParticleTypes.BUBBLE,
                    allay.getX(), allay.getY() + 0.5, allay.getZ(),
                    0, 0, 0
            );

            // Remove the allay and decrement counter
            LuteItem.decrementActiveAllayCount(allay.getUUID());
            allay.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            return;
        }

        // Get the list of blocks to harvest
        net.minecraft.nbt.ListTag blocksList = allay.getPersistentData().getList("BlocksToHarvest", 10);
        if (blocksList.isEmpty()) {
            return;
        }

        // Find the closest block from the list
        BlockPos closestPos = null;
        int closestIndex = -1;
        double closestDistSq = Double.MAX_VALUE;

        // First pass: try to find blocks that are directly accessible
        for (int i = 0; i < blocksList.size(); i++) {
            net.minecraft.nbt.CompoundTag posTag = blocksList.getCompound(i);
            BlockPos pos = new BlockPos(
                    posTag.getInt("X"),
                    posTag.getInt("Y"),
                    posTag.getInt("Z")
            );

            double distSq = pos.distSqr(allay.blockPosition());
            if (distSq < closestDistSq) {
                // Verify the block is still there and not blacklisted
                BlockState state = allay.level().getBlockState(pos);
                if (state.getBlock() == targetBlock && !isBlockBlacklisted(state.getBlock())) {
                    // Check if the block is accessible (has at least one free adjacent space)
                    if (hasAccessibleFace(allay.level(), pos)) {
                        closestDistSq = distSq;
                        closestPos = pos;
                        closestIndex = i;
                    }
                } else {
                    // Block is no longer there or is blacklisted, remove it from the list
                    blocksList.remove(i);
                    i--; // Adjust index since we removed an element
                }
            }
        }

        // If no accessible block found, try any block
        if (closestPos == null) {
            for (int i = 0; i < blocksList.size(); i++) {
                net.minecraft.nbt.CompoundTag posTag = blocksList.getCompound(i);
                BlockPos pos = new BlockPos(
                        posTag.getInt("X"),
                        posTag.getInt("Y"),
                        posTag.getInt("Z")
                );

                double distSq = pos.distSqr(allay.blockPosition());
                if (distSq < closestDistSq) {
                    // Verify the block is still there and not blacklisted
                    BlockState state = allay.level().getBlockState(pos);
                    if (state.getBlock() == targetBlock && !isBlockBlacklisted(state.getBlock())) {
                        closestDistSq = distSq;
                        closestPos = pos;
                        closestIndex = i;
                    } else {
                        // Block is no longer there or is blacklisted, remove it from the list
                        blocksList.remove(i);
                        i--; // Adjust index since we removed an element
                    }
                }
            }
        }

        if (closestPos != null) {
            // Create particles to show target selection
            allay.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.COMPOSTER,
                    closestPos.getX() + 0.5, closestPos.getY() + 0.5, closestPos.getZ() + 0.5,
                    0, 0.1, 0
            );

            // Move to the block
            allay.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET,
                    new net.minecraft.world.entity.ai.memory.WalkTarget(Vec3.atCenterOf(closestPos), 2.0F, 1));

            // If close enough, harvest the block and nearby matching blocks
            if (closestPos.distSqr(allay.blockPosition()) < 16.0) { // Increased range from 9.0 to 16.0
                // Harvest the target block
                BlockState state = allay.level().getBlockState(closestPos);
                allay.level().destroyBlock(closestPos, true, allay);

                // Remove the block from the list
                if (closestIndex >= 0) {
                    blocksList.remove(closestIndex);
                }

                // Try to harvest up to 4 additional blocks of the same type (increased from 3)
                int extraHarvested = 0;
                List<Integer> extraIndices = new ArrayList<>();

                for (int i = 0; i < blocksList.size() && extraHarvested < 4; i++) {
                    net.minecraft.nbt.CompoundTag posTag = blocksList.getCompound(i);
                    BlockPos pos = new BlockPos(
                            posTag.getInt("X"),
                            posTag.getInt("Y"),
                            posTag.getInt("Z")
                    );

                    if (pos.distSqr(closestPos) < 36.0) { // Within 6 blocks (increased from 5)
                        if (allay.level().getBlockState(pos).getBlock() == targetBlock) {
                            allay.level().destroyBlock(pos, true, allay);
                            extraHarvested++;
                            extraIndices.add(i);

                            // Create particles for each extra block broken
                            allay.level().addParticle(
                                    net.minecraft.core.particles.ParticleTypes.CLOUD,
                                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    0, 0.1, 0);
                        }
                    }
                }

                // Remove the extra harvested blocks from the list (in reverse order to avoid index issues)
                for (int i = extraIndices.size() - 1; i >= 0; i--) {
                    blocksList.remove(extraIndices.get(i).intValue());
                }

                // Create particles to show block breaking
                allay.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.CLOUD,
                        closestPos.getX() + 0.5, closestPos.getY() + 0.5, closestPos.getZ() + 0.5,
                        0, 0.1, 0);

                // Update the blocks list in the allay's data
                allay.getPersistentData().put("BlocksToHarvest", blocksList);
            }
        } else {
            // No valid blocks found, move randomly to search
            double x = allay.getX() + (allay.level().random.nextDouble() - 0.5) * 10;
            double y = allay.getY() + (allay.level().random.nextDouble() - 0.5) * 5;
            double z = allay.getZ() + (allay.level().random.nextDouble() - 0.5) * 10;

            allay.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET,
                    new net.minecraft.world.entity.ai.memory.WalkTarget(new Vec3(x, y, z), 2.0F, 1));
        }
    }

    // Add a method to check if a block has at least one accessible face
    private static boolean hasAccessibleFace(net.minecraft.world.level.Level level, BlockPos pos) {
        // Check all six directions
        return level.getBlockState(pos.above()).isAir() ||
                level.getBlockState(pos.below()).isAir() ||
                level.getBlockState(pos.north()).isAir() ||
                level.getBlockState(pos.south()).isAir() ||
                level.getBlockState(pos.east()).isAir() ||
                level.getBlockState(pos.west()).isAir();
    }

    // Add a method to rescan for blocks that might have been missed
    private static boolean rescanForMissedBlocks(Allay allay) {
        // Get the target block type
        String targetBlockId = allay.getPersistentData().getString("TargetBlock");
        Block targetBlock = null;

        // Find the block from its ID
        for (Block block : ForgeRegistries.BLOCKS) {
            if (block.getDescriptionId().equals(targetBlockId)) {
                targetBlock = block;
                break;
            }
        }

        if (targetBlock == null) {
            return false;
        }

        // Get the current list of blocks
        net.minecraft.nbt.ListTag blocksList = allay.getPersistentData().getList("BlocksToHarvest", 10);

        // Get the chest position as a reference point
        int x = allay.getPersistentData().getInt("ChestPosX");
        int y = allay.getPersistentData().getInt("ChestPosY");
        int z = allay.getPersistentData().getInt("ChestPosZ");
        BlockPos chestPos = new BlockPos(x, y, z);

        // Scan in a radius around the allay's current position and the chest
        int horizontalRadius = 10;
        int upwardRadius = 20;
        int downwardRadius = 3;

        // Create a set of existing positions for quick lookup
        Set<BlockPos> existingPositions = new HashSet<>();
        for (int i = 0; i < blocksList.size(); i++) {
            net.minecraft.nbt.CompoundTag posTag = blocksList.getCompound(i);
            BlockPos pos = new BlockPos(
                    posTag.getInt("X"),
                    posTag.getInt("Y"),
                    posTag.getInt("Z")
            );
            existingPositions.add(pos);
        }

        // Scan for missed blocks
        List<BlockPos> missedBlocks = new ArrayList<>();

        // First scan around the chest
        scanArea(allay.level(), chestPos, horizontalRadius, upwardRadius, downwardRadius,
                targetBlock, existingPositions, missedBlocks);

        // Then scan around the allay's current position
        scanArea(allay.level(), allay.blockPosition(), horizontalRadius/2, upwardRadius/2, downwardRadius,
                targetBlock, existingPositions, missedBlocks);

        // Add the missed blocks to our list
        for (BlockPos pos : missedBlocks) {
            net.minecraft.nbt.CompoundTag posTag = new net.minecraft.nbt.CompoundTag();
            posTag.putInt("X", pos.getX());
            posTag.putInt("Y", pos.getY());
            posTag.putInt("Z", pos.getZ());
            blocksList.add(posTag);

            // Create a particle to show the newly found block
            allay.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    0, 0.1, 0
            );
        }

        // Update the blocks list in the allay's data
        allay.getPersistentData().put("BlocksToHarvest", blocksList);

        // If we found missed blocks, notify with a particle effect
        if (!missedBlocks.isEmpty()) {
            allay.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.ENCHANT,
                    allay.getX(), allay.getY() + 0.5, allay.getZ(),
                    0, 0.5, 0
            );
            return true;
        }

        return false;
    }

    // Helper method to scan an area for blocks
    private static void scanArea(net.minecraft.world.level.Level level, BlockPos center,
                                 int horizontalRadius, int upwardRadius, int downwardRadius,
                                 Block targetBlock, Set<BlockPos> existingPositions, List<BlockPos> missedBlocks) {
        for (int dx = -horizontalRadius; dx <= horizontalRadius; dx++) {
            for (int dy = -downwardRadius; dy <= upwardRadius; dy++) {
                for (int dz = -horizontalRadius; dz <= horizontalRadius; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);

                    // Skip if this position is already in our list
                    if (existingPositions.contains(pos)) {
                        continue;
                    }

                    // Check if this is a matching block
                    if (level.getBlockState(pos).getBlock() == targetBlock) {
                        missedBlocks.add(pos.immutable());
                        existingPositions.add(pos.immutable()); // Add to existing set to avoid duplicates
                    }
                }
            }
        }
    }

    private static void deliverItemsToPlayer(Allay allay, Player player) {
        if (!allay.getPersistentData().contains("CollectedItems")) {
            return;
        }

        int itemCount = allay.getPersistentData().getInt("CollectedItems");

        for (int i = 0; i < itemCount; i++) {
            net.minecraft.nbt.CompoundTag itemTag = allay.getPersistentData().getCompound("CollectedItem" + i);
            ItemStack stack = ItemStack.of(itemTag);

            // Give item to player or drop it nearby
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }

            // Create particles to show item delivery
            allay.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.NOTE,
                    allay.getX(), allay.getY() + 0.5, allay.getZ(),
                    0, 0, 0);
        }

        // Clear collected items
        allay.getPersistentData().remove("CollectedItems");
        for (int i = 0; i < itemCount; i++) {
            allay.getPersistentData().remove("CollectedItem" + i);
        }
    }
}
