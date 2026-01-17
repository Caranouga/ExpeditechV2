package fr.caranouga.expeditech.common.tileentities.custom.multiblock;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.multiblocks.MultiBlockShape;
import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class TestMultiBlockMasterTile extends AbstractMultiBlockMasterTile {
    public TestMultiBlockMasterTile() {
        super(ModTileEntities.TEST_MB_MASTER.get());
    }

    @Nonnull
    @Override
    protected MultiBlockShape getShape() {
        BlockState slaveState = ModBlocks.TEST_SLAVE_MB.get().defaultBlockState();
        BlockState thisState = ModBlocks.TEST_MB_MASTER.get().defaultBlockState();
        return new MultiBlockShape(new BlockPos(-1, -1, 0),
                new BlockState[][] {
                        {slaveState, slaveState, slaveState},
                        {slaveState, slaveState, slaveState},
                        {slaveState, slaveState, slaveState}
                },
                new BlockState[][] {
                        {slaveState, thisState, slaveState},
                        {slaveState, slaveState, slaveState},
                        {slaveState, slaveState, slaveState}
                },
                new BlockState[][] {
                        {slaveState, slaveState, slaveState},
                        {slaveState, slaveState, slaveState},
                        {slaveState, slaveState, slaveState}
                });
    }

    @Override
    protected void formedTick() {

    }

    @Override
    protected void unformedTick() {

    }
}
