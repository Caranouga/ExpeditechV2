package fr.caranouga.expeditech.common.grids.cap;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyGridCapWrapper extends GridCapabilityWrapper<IEnergyStorage> {
    @Override
    public boolean canReceive(IEnergyStorage cap) {
        return cap.canReceive();
    }

    @Override
    public boolean canExtract(IEnergyStorage cap) {
        return cap.canExtract();
    }

    @Override
    public int receive(IEnergyStorage cap, int amount, boolean simulate) {
        return cap.receiveEnergy(amount, simulate);
    }

    @Override
    public int extract(IEnergyStorage cap, int amount, boolean simulate) {
        return cap.extractEnergy(amount, simulate);
    }
}
