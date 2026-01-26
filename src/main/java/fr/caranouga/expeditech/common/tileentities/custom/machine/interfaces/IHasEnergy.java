package fr.caranouga.expeditech.common.tileentities.custom.machine.interfaces;

import fr.caranouga.expeditech.common.capabilities.energy.CustomEnergyStorage;

public interface IHasEnergy {
    CustomEnergyStorage createEnergyStorage();
}
