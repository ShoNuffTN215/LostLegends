package net.sho.lostlegends.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.sho.lostlegends.LostLegendsMod;

public class PatchouliBookItem extends Item {
    private final ResourceLocation bookId;

    public PatchouliBookItem(Properties properties, ResourceLocation bookId) {
        super(properties);
        this.bookId = bookId;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            if (ModList.get().isLoaded("patchouli")) {
                // Open the book using Patchouli's API
                openPatchouliBook(player, bookId);
                return InteractionResultHolder.success(stack);
            } else {
                player.sendSystemMessage(Component.literal("Patchouli mod is required to view this book."));
            }
        }

        return InteractionResultHolder.consume(stack);
    }

    private void openPatchouliBook(Player player, ResourceLocation bookId) {
        try {
            // Use reflection to avoid hard dependency
            Class<?> apiClass = Class.forName("vazkii.patchouli.api.PatchouliAPI");
            Object api = apiClass.getMethod("get").invoke(null);

            Class<?> apiInterface = Class.forName("vazkii.patchouli.api.PatchouliAPI$IPatchouliAPI");
            java.lang.reflect.Method openBookMethod = apiInterface.getMethod("openBookGUI", Player.class, ResourceLocation.class);

            openBookMethod.invoke(api, player, bookId);
        } catch (Exception e) {

        }
    }
}