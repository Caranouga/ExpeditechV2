package fr.caranouga.expeditech.common.tileentities.custom.machine;

import fr.caranouga.expeditech.common.capabilities.energy.CustomEnergyStorage;
import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;

public class CoalGeneratorMachineTE extends EnergyMachineTE implements ITickableTileEntity {
    public CoalGeneratorMachineTE() {
        super(ModTileEntities.COAL_GENERATOR.get());
    }

    protected CustomEnergyStorage createEnergyStorage(){
        return new CustomEnergyStorage(1000, 0, 1000);
    }

    protected void serverTick() {
        this.energyStorage.addEnergy(1);

        setChanged();
    }
}
