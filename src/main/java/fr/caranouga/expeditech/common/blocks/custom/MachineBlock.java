package fr.caranouga.expeditech.common.blocks.custom;

import fr.caranouga.expeditech.common.tileentities.custom.machine.MachineTE;
import fr.caranouga.expeditech.common.utils.DirectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MachineBlock extends Block {
    private static final DirectionProperty DIRECTION = DirectionProperty.create("direction", DirectionUtils.CARDINAL);
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    public MachineBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(DIRECTION, Direction.NORTH)
                .setValue(RUNNING, false));
    }

    // region BlockState
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        return this.defaultBlockState().setValue(DIRECTION, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(DIRECTION)));
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return state.setValue(DIRECTION, direction.rotate(state.getValue(DIRECTION)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DIRECTION, RUNNING);
    }
    // endregion

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getTileEntityType().create();
    }

    @Override
    @Nonnull
    public PushReaction getPistonPushReaction(@Nonnull  BlockState pState) {
        return PushReaction.BLOCK;
    }

    protected abstract TileEntityType<? extends MachineTE> getTileEntityType();
}
