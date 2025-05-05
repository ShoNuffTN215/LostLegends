package net.sho.lostlegends.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.registry.EntityRegistry;

public class FlamesOfCreationItem extends Item {

    public FlamesOfCreationItem(Properties properties) {
        super(properties.durability(100).fireResistant());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        // Check if we're on the server side
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            boolean success = false;

            // Check block type and spawn appropriate golem
            if (blockState.is(Blocks.COBBLESTONE)) {
                success = spawnGolem(serverLevel, blockPos, ModEntities.COBBLESTONE_GOLEM.get(), player);
            }
            else if (blockState.is(Blocks.MOSSY_COBBLESTONE)) {
                success = spawnGolem(serverLevel, blockPos, ModEntities.COBBLESTONE_GOLEM.get(), player);
            }
            else if (blockState.is(Blocks.OAK_PLANKS)) {
                success = spawnGolem(serverLevel, blockPos, EntityRegistry.PLANK_GOLEM.get(), player);
            }
            else if (blockState.is(Blocks.GRINDSTONE)) {
                success = spawnGolem(serverLevel, blockPos, EntityRegistry.GRINDSTONE_GOLEM.get(), player);
            }

            if (success) {
                // Remove the block
                level.removeBlock(blockPos, false);

                // Play activation sound
                level.playSound(null, blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS,
                        1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F);

                // Create fire particle effect
                createFireParticles(serverLevel, Vec3.atCenterOf(blockPos));

                // Damage the item
                if (player != null && !player.getAbilities().instabuild) {
                    itemStack.hurtAndBreak(1, player, (p) ->
                            p.broadcastBreakEvent(context.getHand()));
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Spawns a golem of the specified type at the given position
     */
    private <T extends Entity> boolean spawnGolem(ServerLevel level, BlockPos pos, EntityType<T> entityType, Player player) {
        try {
            // Create the golem entity
            T golem = entityType.create(level);

            if (golem != null) {
                // Position the golem at the block position
                golem.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                // Set the golem's owner if there is a player and the golem has an owner field
                if (player != null && golem instanceof LivingEntity livingGolem) {
                    // Try to set owner using reflection or a common interface
                    // This is a simplified approach - in a real implementation, you'd want to use
                    // a common interface or base class for all your golems
                    try {
                        // Attempt to call setOwnerUUID method if it exists
                        golem.getClass().getMethod("setOwnerUUID", java.util.UUID.class)
                                .invoke(golem, player.getUUID());
                    } catch (Exception e) {
                        // Method doesn't exist or couldn't be called, log it
                        System.out.println("Could not set owner for golem: " + e.getMessage());
                    }
                }

                // Add the golem to the world
                level.addFreshEntity(golem);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error spawning golem: " + e.getMessage());
        }
        return false;
    }

    /**
     * Creates an impressive fire particle effect at the specified position
     */
    private void createFireParticles(ServerLevel level, Vec3 pos) {
        // Create a spiral of fire particles
        double radius = 0.5;
        double height = 2.5;
        int spiralPoints = 40;

        for (int i = 0; i < spiralPoints; i++) {
            double angle = (i / (double) spiralPoints) * Math.PI * 8; // 4 full rotations
            double x = pos.x + Math.cos(angle) * radius * (i / (double) spiralPoints);
            double y = pos.y + (i / (double) spiralPoints) * height;
            double z = pos.z + Math.sin(angle) * radius * (i / (double) spiralPoints);

            // Flame particles for the spiral
            level.sendParticles(
                    ParticleTypes.FLAME,
                    x, y, z,
                    1, // count
                    0, 0, 0, // offset
                    0.01 // speed
            );

            // Add some lava particles for extra effect
            if (i % 5 == 0) {
                level.sendParticles(
                        ParticleTypes.LAVA,
                        x, y, z,
                        1, // count
                        0, 0, 0, // offset
                        0.01 // speed
                );
            }
        }

        // Create a ring of fire at the base
        int ringPoints = 20;
        double ringRadius = 0.8;

        for (int i = 0; i < ringPoints; i++) {
            double angle = (i / (double) ringPoints) * Math.PI * 2;
            double x = pos.x + Math.cos(angle) * ringRadius;
            double y = pos.y + 0.1;
            double z = pos.z + Math.sin(angle) * ringRadius;

            // Flame particles for the ring
            level.sendParticles(
                    ParticleTypes.FLAME,
                    x, y, z,
                    1, // count
                    0, 0, 0, // offset
                    0.01 // speed
            );
        }

        // Add a central column of fire and smoke
        for (int i = 0; i < 15; i++) {
            double y = pos.y + (i / 15.0) * 2.0;

            // Central flame particles
            level.sendParticles(
                    ParticleTypes.FLAME,
                    pos.x, y, pos.z,
                    3, // count
                    0.1, 0, 0.1, // offset
                    0.05 // speed
            );

            // Smoke particles above
            if (i > 7) {
                level.sendParticles(
                        ParticleTypes.LARGE_SMOKE,
                        pos.x, y + 0.5, pos.z,
                        2, // count
                        0.1, 0.1, 0.1, // offset
                        0.05 // speed
                );
            }
        }

        // Add some soul fire particles
        for (int i = 0; i < 10; i++) {
            level.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    pos.x + (level.random.nextDouble() - 0.5) * 1.0,
                    pos.y + level.random.nextDouble() * 2.0,
                    pos.z + (level.random.nextDouble() - 0.5) * 1.0,
                    1, // count
                    0, 0, 0, // offset
                    0.05 // speed
            );
        }
    }
}