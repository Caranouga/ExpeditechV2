package fr.caranouga.expeditech.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {
    public CustomEnergyStorage(int capacity) {
        super(capacity);
    }

    public CustomEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public boolean isFull() {
        return this.getEnergyStored() >= this.getMaxEnergyStored();
    }

    public boolean isFullFor(int amount) {
        return this.getEnergyStored() + amount > this.getMaxEnergyStored();
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, this.capacity);
    }

    public void addEnergy(int energy) {
        this.energy = Math.min(this.energy + energy, this.capacity);
    }

    public void removeEnergy(int energy) {
        this.energy = Math.max(this.energy - energy, 0);
    }

    public int getMaxReceive() {
        return this.maxReceive;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Energy", this.getEnergyStored());
        nbt.putInt("Capacity", this.getMaxEnergyStored());
        nbt.putInt("MaxReceive", this.maxReceive);
        nbt.putInt("MaxExtract", this.maxExtract);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.energy = nbt.getInt("Energy");
        this.capacity = nbt.getInt("Capacity");
        this.maxReceive = nbt.getInt("MaxReceive");
        this.maxExtract = nbt.getInt("MaxExtract");
    }
}