package net.sho.lostlegends.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sho.lostlegends.LostLegendsMod;
import net.sho.lostlegends.effect.ModEffects;

@Mod.EventBusSubscriber(modid = LostLegendsMod.MODID)
public class NetherMobEvents {

    @SubscribeEvent
    public static void onTargetChange(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewTarget();
        LivingEntity attacker = event.getEntity();

        // Check if the target is a player with the King of the Nether effect
        if (target instanceof Player player && player.hasEffect(ModEffects.KING_OF_THE_NETHER.get())) {
            // Check if the attacker is a nether mob
            if (isNetherMob(attacker)) {
                // Cancel targeting
                event.setCanceled(true);
            }
        }
    }

    private static boolean isNetherMob(LivingEntity entity) {
        return entity instanceof Blaze ||
                entity instanceof Ghast ||
                entity instanceof MagmaCube ||
                entity instanceof WitherSkeleton ||
                entity instanceof Hoglin ||
                entity instanceof Piglin ||
                entity instanceof PiglinBrute ||
                entity instanceof Zoglin ||
                entity instanceof Strider;
    }
}