package fr.caranouga.expeditech.common.blocks.custom.multiblock;

import fr.caranouga.expeditech.common.tileentities.custom.multiblock.AbstractMultiBlockSlaveTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public abstract class AbstractMultiBlockSlave extends Block {
    public static final BooleanProperty BUILT = BooleanProperty.create("built");

    public AbstractMultiBlockSlave(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any()
                .setValue(BUILT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);

        pBuilder.add(BUILT);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getTileEntityType().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK; // Prevents the block from being pushed by pistons
    }

    protected abstract TileEntityType<? extends AbstractMultiBlockSlaveTile> getTileEntityType();
}
