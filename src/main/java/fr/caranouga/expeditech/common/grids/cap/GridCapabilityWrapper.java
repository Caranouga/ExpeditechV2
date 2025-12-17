package fr.caranouga.expeditech.common.grids.cap;

public abstract class GridCapabilityWrapper<C> {
    public abstract boolean canReceive(C cap);
    public abstract boolean canExtract(C cap);
    public abstract int receive(C cap, int amount, boolean simulate);
    public abstract int extract(C cap, int amount, boolean simulate);
}
