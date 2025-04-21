package net.sho.lostlegends.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.item.ModItems;
import net.sho.lostlegends.item.custom.DevourersPustuleItem;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID)
public class DevourersPustuleEffectHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Only process on the server side and at the end of the tick
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            // Check if the player has the Devourer's Pustule in their inventory
            if (hasDevourersPustuleInInventory(player)) {
                // Apply the effect field
                if (player.level() instanceof ServerLevel serverLevel) {
                    // Apply the effect to nearby entities
                    DevourersPustuleItem.applyEffectInRadius(serverLevel, player);

                    // Spawn particles to visualize the field
                    DevourersPustuleItem.spawnFieldParticles(serverLevel, player);
                }
            }
        }
    }

    /**
     * Check if the player has the Devourer's Pustule anywhere in their inventory.
     */
    private static boolean hasDevourersPustuleInInventory(Player player) {
        Inventory inventory = player.getInventory();

        // Check main inventory
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == ModItems.DEVOURERS_PUSTULE.get()) {
                return true;
            }
        }

        return false;
    }
}
