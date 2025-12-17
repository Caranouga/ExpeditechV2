package fr.caranouga.expeditech.common.te.custom.duct;

import fr.caranouga.expeditech.common.grids.cap.EnergyGridCapWrapper;
import fr.caranouga.expeditech.common.grids.cap.GridCapabilityWrapper;
import fr.caranouga.expeditech.common.grids.data.EnergyGridSavedData;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyDuctTE extends DuctTE<IEnergyStorage, EnergyDuctTE> {
    public EnergyDuctTE(){
        super(ModTileEntities.ENERGY_DUCT.get(), new EnergyGridCapWrapper());
    }

    @Override
    protected Capability<IEnergyStorage> getAssociatedCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    public void onPlaced() {
        if(level == null || level.isClientSide) return;
        grid = EnergyGridSavedData.get((ServerWorld) level).registerNewDuct(this);
    }

    @Override
    public void onRemoved() {
        EnergyGridSavedData.get((ServerWorld) level).unregisterDuct(this);
    }

    @Override
    public int getMaxTransferPerTick() {
        // TODO: Add tier
        return 10;
    }
}
