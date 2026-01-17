package fr.caranouga.expeditech.common.grids.data;

import fr.caranouga.expeditech.common.grids.grid.EnergyGrid;
import fr.caranouga.expeditech.common.grids.grid.Grid;
import fr.caranouga.expeditech.common.grids.GridType;
import fr.caranouga.expeditech.common.tileentities.custom.duct.EnergyDuctTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyGridSavedData extends GridSavedData<IEnergyStorage, EnergyDuctTE> {
    private static final GridType TYPE = GridType.ENERGY;

    public EnergyGridSavedData(ServerWorld world) {
        super(world, TYPE);
    }

    public static EnergyGridSavedData get(ServerWorld world){
        return world.getDataStorage().computeIfAbsent(
                () -> new EnergyGridSavedData(world),
                getId(TYPE)
        );
    }

    @Override
    protected Grid<IEnergyStorage, EnergyDuctTE> createGrid() {
        return new EnergyGrid(this, refWorld);
    }

    @Override
    protected Grid<IEnergyStorage, EnergyDuctTE> createGrid(CompoundNBT nbt) {
        return new EnergyGrid(refWorld, nbt, this);
    }
}
