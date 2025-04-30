package net.sho.lostlegends.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.sho.lostlegends.block.ModBlockEntities;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ForgeOfKnowledgeBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ForgeOfKnowledgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGE_OF_KNOWLEDGE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ForgeOfKnowledgeBlockEntity blockEntity) {
        // Add any tick logic here if needed
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        // Return an empty animation state - this is what creates the "empty animation"
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // This is the correct method signature for BlockEntity
    @Override
    public AABB getRenderBoundingBox() {
        // Create a bounding box that's large enough to contain the entire model
        // This ensures the model is rendered even when the camera is not directly looking at it
        return new AABB(getBlockPos()).inflate(2.0);
    }
}
