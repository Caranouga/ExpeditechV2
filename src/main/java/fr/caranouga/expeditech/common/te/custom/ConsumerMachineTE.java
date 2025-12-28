package fr.caranouga.expeditech.common.te.custom;

import fr.caranouga.expeditech.common.capabilities.CustomEnergyStorage;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConsumerMachineTE extends TileEntity implements ITickableTileEntity {
    protected final CustomEnergyStorage energyStorage = createEnergyStorage();
    private final LazyOptional<CustomEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    public ConsumerMachineTE() {
        super(ModTileEntities.CONSUMER.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY){
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyStorage.invalidate();
    }

    protected CustomEnergyStorage createEnergyStorage(){
        return new CustomEnergyStorage(1000, 1000, 0);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;

        setChanged();
    }
}
