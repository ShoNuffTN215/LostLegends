package net.sho.lostlegends.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class LightningStaffItem extends Item {
    private static final int COOLDOWN_TICKS = 1; // 1 second cooldown
    private static final int MAX_RANGE = 500; // Maximum range in blocks
    private static final int DURABILITY = 10000; // Number of uses before breaking

    public LightningStaffItem() {
        super(new Item.Properties()
                .stacksTo(1) // Only stack to 1
                .rarity(Rarity.RARE) // Make it rare (purple text)
                .durability(DURABILITY)); // Set durability
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Check if on cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemStack);
        }

        // Only proceed on server side
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;

            // Get player's look vector
            Vec3 lookVec = player.getLookAngle();
            Vec3 eyePos = player.getEyePosition();

            // Calculate target position - this will work in air or on blocks
            Vec3 targetPos = findTargetPosition(level, player);

            // Create lightning bolt at the target position
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightningBolt != null) {
                lightningBolt.moveTo(targetPos);
                lightningBolt.setCause(player instanceof net.minecraft.server.level.ServerPlayer ?
                        (net.minecraft.server.level.ServerPlayer) player : null);
                serverLevel.addFreshEntity(lightningBolt);

                // Play sound
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS,
                        1.0F, 1.0F);

                // Damage the item
                if (!player.getAbilities().instabuild) {
                    itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }

                // Add cooldown
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            }
        }

        return InteractionResultHolder.success(itemStack);
    }

    /**
     * Finds the target position for the lightning bolt.
     * This will work for both air and blocks.
     */
    private Vec3 findTargetPosition(Level level, Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        // First, try to find a block hit
        HitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            // If we hit a block, use that position
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHit.getBlockPos().relative(blockHit.getDirection());
            return Vec3.atBottomCenterOf(blockPos);
        } else {
            // If we didn't hit a block, calculate a position in the air
            // Use a reasonable distance (MAX_RANGE blocks away)
            double distance = MAX_RANGE;

            // Raycast to find the first non-air block or the max distance
            for (int i = 1; i < MAX_RANGE; i++) {
                Vec3 pos = eyePos.add(lookVec.scale(i));
                BlockPos blockPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);

                // If we hit a non-air block, use the position just before it
                if (!level.getBlockState(blockPos).isAir()) {
                    distance = i - 1;
                    break;
                }
            }

            // Calculate the final position
            Vec3 targetPos = eyePos.add(lookVec.scale(distance));

            // Ensure the lightning strikes at a valid Y level
            // Lightning needs a block below it to strike properly
            BlockPos groundPos = findGroundBelow(level, new BlockPos((int)targetPos.x, (int)targetPos.y, (int)targetPos.z));

            // Return the position with the ground Y coordinate
            return new Vec3(targetPos.x, groundPos.getY() + 1, targetPos.z);
        }
    }

    /**
     * Finds the ground position below the given position.
     */
    private BlockPos findGroundBelow(Level level, BlockPos pos) {
        // Start from the given position and go down until we find a solid block
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

        // Don't go below the world
        int minY = level.getMinBuildHeight();

        // Go down until we find a solid block or hit the bottom of the world
        while (mutablePos.getY() > minY) {
            mutablePos.move(0, -1, 0);
            if (!level.getBlockState(mutablePos).isAir()) {
                return mutablePos.immutable();
            }
        }

        // If we didn't find a solid block, return the bottom of the world
        return new BlockPos(pos.getX(), minY, pos.getZ());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.lostlegends.lightning_staff_tooltip.shift"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Add enchantment glint
        return true;
    }
}