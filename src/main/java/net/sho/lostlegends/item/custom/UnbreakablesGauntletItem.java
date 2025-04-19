package net.sho.lostlegends.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class UnbreakablesGauntletItem extends Item {
    private final int cooldownTicks;
    private final float fireballPower;

    public UnbreakablesGauntletItem(Properties properties, int cooldownTicks, float fireballPower) {
        super(properties);
        this.cooldownTicks = cooldownTicks;
        this.fireballPower = fireballPower;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Play a sound effect
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        // Add cooldown to prevent spam
        player.getCooldowns().addCooldown(this, cooldownTicks);

        if (!level.isClientSide) {
            // Create the fireball
            Vec3 lookVec = player.getLookAngle();

            // Position the fireball slightly in front of the player
            double offsetX = player.getX() + lookVec.x * 1.5;
            double offsetY = player.getY() + 1.0; // Roughly at eye level
            double offsetZ = player.getZ() + lookVec.z * 1.5;

            LargeFireball fireball = new LargeFireball(level, player,
                    lookVec.x, lookVec.y, lookVec.z, (int)fireballPower);

            fireball.setPos(offsetX, offsetY, offsetZ);
            level.addFreshEntity(fireball);

            // Optional: damage the item after use
            if (!player.getAbilities().instabuild) {
                itemstack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
        }

        // Record the stat for item usage
        player.awardStat(Stats.ITEM_USED.get(this));

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("Releases a powerful explosion"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
