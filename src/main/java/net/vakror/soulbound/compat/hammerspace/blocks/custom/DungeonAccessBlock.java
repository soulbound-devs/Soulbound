package net.vakror.soulbound.compat.hammerspace.blocks.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.vakror.soulbound.SoulboundMod;
import net.vakror.soulbound.compat.hammerspace.blocks.entity.DungeonAccessBlockEntity;
import net.vakror.soulbound.compat.hammerspace.dimension.DungeonTeleporter;
import net.vakror.soulbound.compat.hammerspace.dungeon.capability.DungeonProvider;
import net.vakror.soulbound.compat.hammerspace.items.ModDungeonItems;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.vakror.soulbound.compat.hammerspace.dimension.DimensionUtils.createWorld;
import static net.vakror.soulbound.compat.hammerspace.dimension.DimensionUtils.doesLevelExist;

public class DungeonAccessBlock extends BaseEntityBlock {
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public DungeonAccessBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide && player.getItemInHand(hand).getItem().equals(ModDungeonItems.KEY.get()) && state.getValue(LOCKED)) {
            level.setBlock(pos, state.setValue(LOCKED, false), 35);
            return InteractionResult.CONSUME;
        }
        if (!level.isClientSide && !state.getValue(LOCKED)) {
            /*
            player teleportation
            */
            if (level.getBlockEntity(pos) instanceof DungeonAccessBlockEntity blockEntity) {
                if (blockEntity.getDimensionUUID() == null) {
                    blockEntity.setDimensionUUID(UUID.randomUUID());
                    blockEntity.setChanged();
                }
                boolean levelExists = doesLevelExist(SoulboundMod.instance.server, blockEntity.getDimensionUUID());
                final boolean[] canEnter = new boolean[]{true};
                ServerLevel dimension = createWorld(level, blockEntity);
                    dimension.getCapability(DungeonProvider.DUNGEON).ifPresent((dungeonLevel -> {
                        if (!levelExists) {
                            dungeonLevel.setStable(new Random().nextBoolean());
                        }
                        canEnter[0] = dungeonLevel.canEnter();
                    }));
                if (canTeleport(level, player, dimension, blockEntity.getDimensionUUID()) && canEnter[0]) {
                    player.setPortalCooldown();
                    player.changeDimension(dimension, new DungeonTeleporter(pos, this));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    private boolean canTeleport(Level level, Player player, ServerLevel dimension, UUID dimensionUUID) {
        if (!dimension.players().isEmpty()) {
            SoulboundMod.LOGGER.info(player.getDisplayName() + "Cannot Enter" + (isStable(dimension) ? "Stable": "Unstable") + "Dungeon, as " + dimension.players().get(0) + "Is already in it");
        }
        return dimension.players().isEmpty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOCKED);
        builder.add(FACING);
    }
    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
        if (pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof DungeonAccessBlockEntity blockEntity) {
            return new ArrayList<>(Collections.singleton(blockEntity.drops()));
        }
        return super.getDrops(pState, pBuilder);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    public boolean isStable(ServerLevel dungeon) {
        AtomicBoolean toReturn = new AtomicBoolean(true);
        dungeon.getCapability(DungeonProvider.DUNGEON).ifPresent((dungeon1 -> {
            toReturn.set(dungeon1.isStable());
        }));
        return toReturn.get();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pLevel.getBlockEntity(pPos) instanceof DungeonAccessBlockEntity blockEntity) {
            if (pStack.hasTag() && pStack.getTag().hasUUID("uuid")) {
                blockEntity.setDimensionUUID(pStack.getTag().getUUID("uuid"));
                blockEntity.setChanged();
            }
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DungeonAccessBlockEntity(pPos, pState);
    }
}