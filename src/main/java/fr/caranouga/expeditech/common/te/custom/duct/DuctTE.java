package fr.caranouga.expeditech.common.te.custom.duct;

import fr.caranouga.expeditech.common.blocks.custom.duct.Duct;
import fr.caranouga.expeditech.common.grids.grid.Grid;
import fr.caranouga.expeditech.common.grids.cap.GridCapabilityWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DuctTE<C, D extends DuctTE<C, D>> extends TileEntity {
    protected Grid<C, D> grid;
    private final GridCapabilityWrapper<C> capWrapper;

    public DuctTE(TileEntityType<?> tileEntityType, GridCapabilityWrapper<C> capWrapper) {
        super(tileEntityType);

        this.capWrapper = capWrapper;
    }

    public void setGrid(Grid<C, D> grid){
        this.grid = grid;
    }

    @Override
    public void setRemoved() {
        if(grid == null) super.setRemoved();

        if(level == null || level.isClientSide) return;
        onRemoved();

        super.setRemoved();
    }

    public void neighborChanged(@Nullable TileEntity neighbor, @Nonnull BlockPos neighborPos, @Nullable Direction side) {
        if(grid == null) return;

        if(neighbor == null){
            grid.tryRemove(neighborPos);
            return;
        }

        neighbor.getCapability(getAssociatedCapability(), side).ifPresent(cons -> {
            if(capWrapper.canReceive(cons)) grid.addConsumer(neighbor.getBlockPos());
        });
        neighbor.getCapability(getAssociatedCapability(), side).ifPresent(cons -> {
            if(capWrapper.canExtract(cons)) grid.addGenerator(neighbor.getBlockPos());
        });
    }

    public int getMaxTransferPerTick(){
        return getBlockState().getValue(Duct.TIER).getAmount();
    }

    protected abstract Capability<C> getAssociatedCapability();
    public abstract void onPlaced();
    public abstract void onRemoved();
    // public abstract int getMaxTransferPerTick();
}
