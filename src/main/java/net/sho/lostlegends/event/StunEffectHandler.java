package net.sho.lostlegends.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.effect.ModEffects;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID)
public class StunEffectHandler {

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        // Check if the attacker is stunned
        LivingEntity attacker = event.getEntity();
        if (attacker != null && attacker.hasEffect(ModEffects.STUN.get())) {
            // Cancel the attack
            event.setCanceled(true);

            // Notify the player if it's a player
            if (attacker instanceof Player player && !player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("effect.lostlegends.stun.cannot_attack"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        // Check if the player is stunned
        if (event.getEntity().hasEffect(ModEffects.STUN.get())) {
            // Cancel the block breaking
            event.setCanceled(true);

            // Notify the player
            if (!event.getLevel().isClientSide) {
                event.getEntity().displayClientMessage(Component.translatable("effect.lostlegends.stun.cannot_break"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Check if the player is stunned
        if (event.getEntity().hasEffect(ModEffects.STUN.get())) {
            // Cancel the interaction
            event.setCanceled(true);

            // Notify the player
            if (!event.getLevel().isClientSide) {
                event.getEntity().displayClientMessage(Component.translatable("effect.lostlegends.stun.cannot_interact"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        // Check if the player is stunned
        if (event.getEntity().hasEffect(ModEffects.STUN.get())) {
            // Cancel the item use
            event.setCanceled(true);

            // Notify the player
            if (!event.getLevel().isClientSide) {
                event.getEntity().displayClientMessage(Component.translatable("effect.lostlegends.stun.cannot_use_item"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityUseItem(LivingEntityUseItemEvent.Start event) {
        // Check if the entity is stunned
        if (event.getEntity().hasEffect(ModEffects.STUN.get())) {
            // Cancel the item use
            event.setCanceled(true);

            // Notify the player if it's a player
            if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("effect.lostlegends.stun.cannot_use_item"), true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        // Check if the entity trying to mount is stunned
        if (event.getEntityMounting() instanceof LivingEntity living &&
                living.hasEffect(ModEffects.STUN.get())) {
            // Cancel the mounting
            event.setCanceled(true);

            // Notify the player if it's a player
            if (living instanceof Player player && !player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("effect.lostlegends.stun.cannot_mount"), true);
            }
        }
    }

    // Add this new event handler to prevent jumping
    @SubscribeEvent
    public static void onLivingJump(LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();

        // Check if the entity is stunned
        if (entity.hasEffect(ModEffects.STUN.get())) {
            // Cancel the jump by zeroing out the entity's motion
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0, entity.getDeltaMovement().z);

            // Notify the player if it's a player
            if (entity instanceof Player player && !player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("effect.lostlegends.stun.cannot_jump"), true);
            }
        }
    }
}