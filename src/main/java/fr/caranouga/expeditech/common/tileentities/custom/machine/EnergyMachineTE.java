package fr.caranouga.expeditech.common.tileentities.custom.machine;

import fr.caranouga.expeditech.common.capabilities.energy.CustomEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EnergyMachineTE extends MachineTE {
    protected final CustomEnergyStorage energyStorage = createEnergyStorage();
    private final LazyOptional<CustomEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    public EnergyMachineTE(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
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

    // region Data Saving (World load/save)
    protected CompoundNBT createTagClient(CompoundNBT tag){
        tag.put("Energy", this.energyStorage.serializeNBT());

        return tag;
    }

    protected CompoundNBT createTagServer(CompoundNBT tag){
        tag.put("Energy", this.energyStorage.serializeNBT());

        return tag;
    }

    protected void readTagServer(CompoundNBT tag){
        if (tag.contains("Energy")) {
            this.energyStorage.deserializeNBT(tag.getCompound("Energy"));
        }
    }

    protected void readTagClient(CompoundNBT tag){
        if (tag.contains("ogBlock")) {
            this.energyStorage.deserializeNBT(tag.getCompound("Energy"));
        }
    }
    // endregion









    protected abstract CustomEnergyStorage createEnergyStorage();
}
