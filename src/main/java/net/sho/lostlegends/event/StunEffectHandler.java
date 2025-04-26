package net.sho.lostlegends.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.effect.ModEffects;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID)
public class StunEffectHandler {

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ModEffects.STUN.get())) {
            // Cancel jump by setting motion to 0
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0, entity.getDeltaMovement().z);
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if (player.hasEffect(ModEffects.STUN.get())) {
            // Cancel all interactions
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player.hasEffect(ModEffects.STUN.get())) {
            // Cancel block breaking
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerDigSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.hasEffect(ModEffects.STUN.get())) {
            // Set mining speed to 0
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        // Check if the attacker is a living entity and has the stun effect
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (attacker.hasEffect(ModEffects.STUN.get())) {
                // Cancel the attack
                event.setCanceled(true);
            }
        }
    }
}