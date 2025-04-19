package net.sho.lostlegends.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class DevourersPustuleItem extends Item {
    private static final int DURABILITY_COST = 1; // Durability cost per operation
    private static final int CHECK_INTERVAL = 100000; // Check every 5 seconds (100 ticks)

    public DevourersPustuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // Only process on the server side and every CHECK_INTERVAL ticks
        if (level.isClientSide || entity.tickCount % CHECK_INTERVAL != 0) {
            return;
        }

        if (entity instanceof Player player) {
            // Check if player's food level is below maximum
            if (player.getFoodData().getFoodLevel() < 20 || player.getFoodData().getSaturationLevel() < 20.0F) {
                // Restore hunger and saturation
                player.getFoodData().setFoodLevel(20);
                player.getFoodData().setSaturation(20.0F);

                // Damage the item
                if (!player.getAbilities().instabuild) {
                    stack.hurtAndBreak(DURABILITY_COST, player, (p) -> {
                        p.broadcastBreakEvent(p.getUsedItemHand());
                        // Play a sound or show a particle when the item is damaged
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                                net.minecraft.sounds.SoundSource.PLAYERS, 0.5F, 0.8F);
                    });
                }
            }
        }
    }

    // Alternative implementation using Forge events
    @SubscribeEvent
    public static void onLivingUpdateEvent(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide && player.tickCount % CHECK_INTERVAL == 0) {
            // Check if player has the amulet in inventory
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() instanceof DevourersPustuleItem && !stack.isEmpty()) {
                    // Check if player's food level is below maximum
                    if (player.getFoodData().getFoodLevel() < 20 || player.getFoodData().getSaturationLevel() < 20.0F) {
                        // Restore hunger and saturation
                        player.getFoodData().setFoodLevel(20);
                        player.getFoodData().setSaturation(20.0F);

                        // Damage the item
                        if (!player.getAbilities().instabuild) {
                            stack.hurtAndBreak(DURABILITY_COST, player, (p) -> {
                                p.broadcastBreakEvent(p.getUsedItemHand());
                                // Play a sound or show a particle when the item is damaged
                                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                        net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 0.5F, 0.8F);
                            });
                        }

                        // Only process one amulet
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("Feeds you at the cost of durability"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
