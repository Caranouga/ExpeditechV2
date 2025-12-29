package fr.caranouga.expeditech.common.capabilities.tech;

import fr.caranouga.expeditech.common.capabilities.ModCapabilities;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TechLevelProvider implements ICapabilitySerializable<INBT> {
    private final ITechLevel techLevel = new TechLevelImplementation();
    private final LazyOptional<ITechLevel> techLevelOptional = LazyOptional.of(() -> techLevel);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == ModCapabilities.TECH_LEVEL){
            return techLevelOptional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return ModCapabilities.TECH_LEVEL.getStorage().writeNBT(ModCapabilities.TECH_LEVEL, techLevel, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        ModCapabilities.TECH_LEVEL.getStorage().readNBT(ModCapabilities.TECH_LEVEL, techLevel, null, nbt);
    }
}
