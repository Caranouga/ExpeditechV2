package fr.caranouga.expeditech.common.blocks.custom;

import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class EnergyDuctMachine extends Block {
    public EnergyDuctMachine() {
        super(Properties.of(Material.METAL));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.ENERGY_DUCT.get().create();
    }
}
