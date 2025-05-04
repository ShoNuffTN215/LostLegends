package net.sho.lostlegends.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.sho.lostlegends.block.ModBlockEntities;
import net.sho.lostlegends.block.entity.ForgeOfKnowledgeBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.RandomSource;

import java.util.List;

public class ForgeOfKnowledgeBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty PART = IntegerProperty.create("part", 0, 1);

    // Define VoxelShapes for each part and orientation with proper offsets
    // For North facing
    private static final VoxelShape SHAPE_NORTH_MAIN = Block.box(0, 0, 0, 16, 22, 16);
    private static final VoxelShape SHAPE_NORTH_SECONDARY = Block.box(0, 0, 0, 16, 22, 16);

    // For East facing
    private static final VoxelShape SHAPE_EAST_MAIN = Block.box(0, 0, 0, 16, 22, 16);
    private static final VoxelShape SHAPE_EAST_SECONDARY = Block.box(0, 0, 0, 16, 22, 16);

    // For South facing - shifted one to the left
    private static final VoxelShape SHAPE_SOUTH_MAIN = Block.box(0, 0, 0, 16, 22, 16);
    private static final VoxelShape SHAPE_SOUTH_SECONDARY = Block.box(0, 0, 0, 16, 22, 16);

    // For West facing - shifted one to the left
    private static final VoxelShape SHAPE_WEST_MAIN = Block.box(0, 0, 0, 16, 22, 16);
    private static final VoxelShape SHAPE_WEST_SECONDARY = Block.box(0, 0, 0, 16, 22, 16);

    // Reference block for particles
    private static final Block PARTICLE_BLOCK = Blocks.SMITHING_TABLE;

    public ForgeOfKnowledgeBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK)
                .strength(5.0f, 6.0f)
                .sound(SoundType.METAL)
                .noOcclusion()  // Important for visibility
                .isViewBlocking((state, level, pos) -> false)  // Ensures the block doesn't block view
                .isSuffocating((state, level, pos) -> false));  // Ensures the block doesn't suffocate
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Direction facing = context.getHorizontalDirection().getOpposite();

        // Check if we have space for a 2x1 structure
        if (canPlaceAt(level, pos, facing)) {
            return this.defaultBlockState().setValue(FACING, facing).setValue(PART, 0);
        }

        return null;
    }

    private boolean canPlaceAt(Level level, BlockPos pos, Direction facing) {
        // Check if both positions are available
        BlockPos[] positions = getPositionsForParts(pos, facing);

        for (BlockPos blockPos : positions) {
            if (!level.getBlockState(blockPos).canBeReplaced()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(this) && state.getValue(PART) == 0) {
            Direction facing = state.getValue(FACING);
            BlockPos[] positions = getPositionsForParts(pos, facing);

            // Place the other part
            level.setBlock(positions[1], state.setValue(PART, 1), 3);
        }

        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (state.getValue(PART) == 0) {
                // If the main block is removed, remove the other part
                Direction facing = state.getValue(FACING);
                BlockPos[] positions = getPositionsForParts(pos, facing);

                BlockPos partPos = positions[1];
                BlockState partState = level.getBlockState(partPos);
                if (partState.is(this)) {
                    level.removeBlock(partPos, false);
                }
            } else {
                // If the secondary part is removed, remove the main part
                BlockPos mainPos = getMainPos(pos, state);
                BlockState mainState = level.getBlockState(mainPos);
                if (mainState.is(this) && mainState.getValue(PART) == 0) {
                    level.removeBlock(mainPos, false);
                }
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    private BlockPos[] getPositionsForParts(BlockPos mainPos, Direction facing) {
        BlockPos[] positions = new BlockPos[2];
        positions[0] = mainPos; // Main block

        // Second block position depends on facing
        // For South and West, we need to adjust the positioning
        switch (facing) {
            case NORTH:
                positions[1] = mainPos.east(); // Extend to the east
                break;
            case EAST:
                positions[1] = mainPos.south(); // Extend to the south
                break;
            case SOUTH:
                positions[1] = mainPos.west(); // Extend to the west (shifted left)
                break;
            case WEST:
                positions[1] = mainPos.north(); // Extend to the north (shifted left)
                break;
        }

        return positions;
    }

    private BlockPos getMainPos(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);

        // If this is part 1, get the position of part 0
        if (state.getValue(PART) == 1) {
            switch (facing) {
                case NORTH:
                    return pos.west(); // Main block is to the west
                case EAST:
                    return pos.north(); // Main block is to the north
                case SOUTH:
                    return pos.east(); // Main block is to the east (shifted left)
                case WEST:
                    return pos.south(); // Main block is to the south (shifted left)
            }
        }

        return pos; // Already at main position
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        int part = state.getValue(PART);

        // Return the appropriate shape based on orientation and part
        switch (facing) {
            case NORTH:
                return part == 0 ? SHAPE_NORTH_MAIN : SHAPE_NORTH_SECONDARY;
            case EAST:
                return part == 0 ? SHAPE_EAST_MAIN : SHAPE_EAST_SECONDARY;
            case SOUTH:
                return part == 0 ? SHAPE_SOUTH_MAIN : SHAPE_SOUTH_SECONDARY;
            case WEST:
                return part == 0 ? SHAPE_WEST_MAIN : SHAPE_WEST_SECONDARY;
            default:
                return Shapes.block();
        }
    }

    // These methods help with visibility issues
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty(); // No occlusion
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty(); // No visual shape for culling
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F; // Full brightness
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true; // Let skylight pass through
    }

    // Override to completely replace the default particle behavior
    @Override
    public void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        // Instead of using the default behavior, manually spawn particles using a consistent block
        if (level.isClientSide) {
            // Use smithing table for consistent particles
            BlockState particleState = PARTICLE_BLOCK.defaultBlockState();

            // Spawn particles manually
            for (int i = 0; i < 10; i++) {
                double x = pos.getX() + level.random.nextDouble();
                double y = pos.getY() + level.random.nextDouble();
                double z = pos.getZ() + level.random.nextDouble();

                double xSpeed = level.random.nextGaussian() * 0.05;
                double ySpeed = level.random.nextGaussian() * 0.05 + 0.2;
                double zSpeed = level.random.nextGaussian() * 0.05;

                level.addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, particleState),
                        x, y, z, xSpeed, ySpeed, zSpeed
                );
            }
        }
    }

    // Add this method to ensure particles are generated for both parts
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // You can add ambient particles here if needed
        super.animateTick(state, level, pos, random);
    }

    // Block Entity Stuff

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Only render the main part (part 0) with the custom renderer
        return state.getValue(PART) == 0 ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // Only create a block entity for the main part
        return state.getValue(PART) == 0 ? new ForgeOfKnowledgeBlockEntity(pos, state) : null;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof ForgeOfKnowledgeBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer) pPlayer), (ForgeOfKnowledgeBlockEntity) entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.FORGE_OF_KNOWLEDGE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.lostlegends.forge_of_knowledge.line1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.lostlegends.forge_of_knowledge.line2")
                .withStyle(ChatFormatting.BLUE));

        super.appendHoverText(stack, level, tooltip, flag);
    }
}


