package fr.caranouga.expeditech.common.blocks.custom.duct;

import fr.caranouga.expeditech.common.te.ModTileEntities;
import fr.caranouga.expeditech.common.te.custom.duct.EnergyDuctTE;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyDuct extends Duct<EnergyDuctTE> {
    public EnergyDuct() {
        super(EnergyDuctTE.class, "energy", DuctTier.values());
    }

    @Override
    protected TileEntityType<EnergyDuctTE> getTileEntityType() {
        return ModTileEntities.ENERGY_DUCT.get();
    }

    @Override
    protected Capability<?> getCapability() {
        return CapabilityEnergy.ENERGY;
    }
}
