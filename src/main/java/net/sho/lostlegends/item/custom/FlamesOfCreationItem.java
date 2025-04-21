package net.sho.lostlegends.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.entity.ModEntities;
import net.sho.lostlegends.entity.custom.CobblestoneGolemEntity;
import net.sho.lostlegends.util.ModTags;

import javax.annotation.Nullable;
import java.util.*;

public class FlamesOfCreationItem extends Item {
    public FlamesOfCreationItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();

        // Check if the clicked block is cobblestone
        if (level.getBlockState(blockPos).getBlock() == Blocks.COBBLESTONE) {
            if (!level.isClientSide) {
                // Remove the cobblestone block
                level.removeBlock(blockPos, false);

                // Create a new golem at the position
                CobblestoneGolemEntity golem = ModEntities.COBBLESTONE_GOLEM.get().create(level);
                if (golem != null) {
                    golem.setPos(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5);

                    // Set the owner to the player who used the item
                    if (player != null) {
                        golem.tame(player);
                        player.sendSystemMessage(Component.translatable("item.lostlegends.flames_of_creation.summon_success"));
                    }

                    level.addFreshEntity(golem);

                    // Damage the item (but don't consume it)
                    if (!player.getAbilities().instabuild) {
                        context.getItemInHand().hurtAndBreak(1, player, (p) ->
                                p.broadcastBreakEvent(context.getHand()));
                    }
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("Summons Golems"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
