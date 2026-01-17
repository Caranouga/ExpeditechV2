package fr.caranouga.expeditech.common.blocks.custom.multiblock;

import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import fr.caranouga.expeditech.common.tileentities.custom.multiblock.AbstractMultiBlockMasterTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;

public class TestMBMaster extends AbstractMultiBlockMaster {
    public TestMBMaster() {
        super(AbstractBlock.Properties.of(Material.METAL));
    }

    @Override
    protected TileEntityType<? extends AbstractMultiBlockMasterTile> getTileEntityType() {
        return ModTileEntities.TEST_MB_MASTER.get();
    }
}
