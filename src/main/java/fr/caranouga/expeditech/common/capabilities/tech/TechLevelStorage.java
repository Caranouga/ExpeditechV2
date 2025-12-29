package fr.caranouga.expeditech.common.capabilities.tech;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TechLevelStorage implements Capability.IStorage<ITechLevel> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<ITechLevel> capability, ITechLevel instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();

        tag.putInt("techLevel", instance.getTechLevel());
        tag.putInt("techXp", instance.getTechXp());

        return tag;
    }

    @Override
    public void readNBT(Capability<ITechLevel> capability, ITechLevel instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;

        instance.setTechLevel(tag.getInt("techLevel"));
        instance.setTechXp(tag.getInt("techXp"));
    }
}
