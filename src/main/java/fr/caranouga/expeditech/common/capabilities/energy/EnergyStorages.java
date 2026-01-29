package fr.caranouga.expeditech.common.capabilities.energy;

public enum EnergyStorages {
    // TODO: Rebalance the energy
    COAL_GENERATOR(1000, 0, 1000)
    ;

    private int capacity;
    private int maxReceive;
    private int maxExtract;

    EnergyStorages(int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public CustomEnergyStorage get(){
        return new CustomEnergyStorage(this.capacity, this.maxReceive, this.maxExtract);
    }

    public int getCapacity() {
        return capacity;
    }
}
