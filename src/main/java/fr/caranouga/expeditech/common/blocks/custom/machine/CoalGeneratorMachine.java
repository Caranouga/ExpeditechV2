package fr.caranouga.expeditech.common.blocks.custom.machine;

import fr.caranouga.expeditech.common.blocks.custom.MachineBlock;
import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import fr.caranouga.expeditech.common.tileentities.custom.machine.MachineTE;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;

public class CoalGeneratorMachine extends MachineBlock {
    public CoalGeneratorMachine() {
        super(Properties.of(Material.METAL));
    }

    @Override
    protected TileEntityType<? extends MachineTE> getTileEntityType() {
        return ModTileEntities.COAL_GENERATOR.get();
    }
}
