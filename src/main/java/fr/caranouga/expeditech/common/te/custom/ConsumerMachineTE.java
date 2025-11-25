package fr.caranouga.expeditech.common.te.custom;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.CustomEnergyStorage;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerMachineTE extends TileEntity implements ITickableTileEntity {
    protected final CustomEnergyStorage energyStorage = createEnergyStorage();
    private final LazyOptional<CustomEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    protected CustomEnergyStorage createEnergyStorage(){
        return new CustomEnergyStorage(1000);
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

    protected void sendOutEnergy(){
        AtomicBoolean energySent = new AtomicBoolean(false);

        for(Direction direction : Direction.values()) {
            TileEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if(neighbor == null || neighbor == this) continue;

            neighbor.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(handler -> {
                if (handler.canReceive()) {
                    int extracted = energyStorage.extractEnergy(this.energyStorage.getMaxExtract(), true); // Simule
                    int received = handler.receiveEnergy(extracted, false); // Reçoit
                    if(received > 0) {
                        energyStorage.extractEnergy(received, false); // Consomme
                        energySent.set(true);
                    }
                }
            });
        }

        if (energySent.get()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    public ConsumerMachineTE() {
        super(ModTileEntities.CONSUMER.get());
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;

        Expeditech.LOGGER.info("The consumer at {} has {}", getBlockPos(), this.energyStorage.getEnergyStored());

        //sendOutEnergy();
        setChanged();
    }
}
