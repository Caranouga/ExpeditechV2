/*package fr.caranouga.expeditech.common.te.custom;


import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.EnergyGrid;
import fr.caranouga.expeditech.common.grids.EnergyGridSavedData;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyDuctMachineTE extends TileEntity {
    private EnergyGrid grid;

    public EnergyDuctMachineTE() {
        super(ModTileEntities.ENERGY_DUCT.get());
    }

    public EnergyDuctMachineTE(TileEntityType<?> type) {
        super(type);
    }

    public void onPlaced(){
        grid = EnergyGridSavedData.registerNewDuct(this);
    }

    public void setGrid(EnergyGrid grid){
        this.grid = grid;
    }

    @Override
    public void setRemoved() {
        if(grid == null) super.setRemoved();

        EnergyGridSavedData.unregisterDuct(this);

        super.setRemoved();
    }

    public void neighborChanged(@Nullable TileEntity neighbor, @Nonnull BlockPos neighborPos, @Nullable Direction side) {
        if(grid == null) return;

        if(neighbor == null){
            grid.tryRemove(neighborPos);
            return;
        }

        neighbor.getCapability(CapabilityEnergy.ENERGY, side).ifPresent(cons -> {
            if(cons.canReceive()) grid.addConsumer(neighbor.getBlockPos());
        });
        neighbor.getCapability(CapabilityEnergy.ENERGY, side).ifPresent(cons -> {
            if(cons.canExtract()) grid.addGenerator(neighbor.getBlockPos());
        });
    }

    public int getMaxTransferPerTick() {
        return 10;
    }
}
*/