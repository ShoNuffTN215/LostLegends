package net.sho.lostlegends.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.effect.ModEffects;
import net.sho.lostlegends.item.ModItems;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID)
public class FungalAcidImmunityHandler {

    @SubscribeEvent
    public static void onEffectApplied(MobEffectEvent.Applicable event) {
        // Check if the effect is Fungal Acid
        if (event.getEffectInstance().getEffect() == ModEffects.FUNGAL_ACID.get()) {
            // Check if the entity is a player
            if (event.getEntity() instanceof Player player) {
                // Check if the player has the Devourer's Pustule in their inventory
                if (hasDevourersPustuleInInventory(player)) {
                    // Cancel the effect application
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            }
        }
    }

    /**
     * Check if the player has the Devourer's Pustule anywhere in their inventory.
     */
    private static boolean hasDevourersPustuleInInventory(Player player) {
        // Check main inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == ModItems.DEVOURERS_PUSTULE.get()) {
                return true;
            }
        }

        return false;
    }
}