package fr.caranouga.expeditech.common.blocks.custom.multiblock;

import fr.caranouga.expeditech.common.tileentities.custom.multiblock.AbstractMultiBlockMasterTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractMultiBlockMaster extends Block {
    public static final BooleanProperty BUILT = BooleanProperty.create("built");

    public AbstractMultiBlockMaster(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any()
                .setValue(BUILT, false));
    }

    // region BlockState
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BUILT);
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
    public void onPlace(@Nonnull BlockState pState, @Nonnull World pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);


    }

    @Override
    @Nonnull
    public PushReaction getPistonPushReaction(@Nonnull  BlockState pState) {
        return PushReaction.BLOCK;
    }

    protected abstract TileEntityType<? extends AbstractMultiBlockMasterTile> getTileEntityType();
}
