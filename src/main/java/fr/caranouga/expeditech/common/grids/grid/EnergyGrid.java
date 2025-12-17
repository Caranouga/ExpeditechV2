package fr.caranouga.expeditech.common.grids.grid;

import fr.caranouga.expeditech.common.grids.cap.EnergyGridCapWrapper;
import fr.caranouga.expeditech.common.grids.data.EnergyGridSavedData;
import fr.caranouga.expeditech.common.te.custom.duct.EnergyDuctTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyGrid extends Grid<IEnergyStorage, EnergyDuctTE> {
    public EnergyGrid(EnergyGridSavedData savedData, ServerWorld world) {
        super(savedData, world, EnergyDuctTE.class, new EnergyGridCapWrapper());
    }

    public EnergyGrid(ServerWorld worldRef, CompoundNBT nbt, EnergyGridSavedData savedData) {
        super(worldRef, nbt, savedData, EnergyDuctTE.class, new EnergyGridCapWrapper());
    }

    @Override
    protected Capability<IEnergyStorage> getCapability() {
        return CapabilityEnergy.ENERGY;
    }
}
