package net.sho.lostlegends.item.custom;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.sho.lostlegends.client.renderer.GreatHogsCrownRenderer;
import net.sho.lostlegends.effect.ModEffects;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GreatHogsCrownItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GreatHogsCrownItem(ArmorMaterial material, Properties properties) {
        super(material, Type.HELMET, properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // No animations, so we leave this empty
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GreatHogsCrownRenderer renderer;

            @Override
            public net.minecraft.client.model.HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                                     EquipmentSlot equipmentSlot, net.minecraft.client.model.HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new GreatHogsCrownRenderer();
                }

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player player) {
            // Check if the crown is being worn in the helmet slot
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet == stack) {
                // Apply effect with duration of 5 seconds (100 ticks) and refresh it constantly
                // This ensures the effect disappears shortly after removing the crown
                player.addEffect(new MobEffectInstance(ModEffects.KING_OF_THE_NETHER.get(), 100, 0, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, false, false, true));
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}