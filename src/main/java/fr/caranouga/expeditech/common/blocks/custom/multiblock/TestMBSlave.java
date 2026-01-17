package fr.caranouga.expeditech.common.blocks.custom.multiblock;

import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import fr.caranouga.expeditech.common.tileentities.custom.multiblock.AbstractMultiBlockSlaveTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;

public class TestMBSlave extends AbstractMultiBlockSlave {
    public TestMBSlave() {
        super(AbstractBlock.Properties.of(Material.METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
    }

    @Override
    protected TileEntityType<? extends AbstractMultiBlockSlaveTile> getTileEntityType() {
        return ModTileEntities.TEST_MB_SLAVE.get();
    }
}
