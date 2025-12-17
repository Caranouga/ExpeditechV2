/*package fr.caranouga.expeditech.common.blocks.custom;


import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

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

    @Override
    public void onPlace(BlockState pState, World pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);

        EnergyDuctMachineTE te = getTileEntity(pLevel, pPos);
        if(te == null) return;

        te.onPlaced();
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);

        EnergyDuctMachineTE te = getTileEntity(world, pos);
        if(te == null) return;

        TileEntity neighborTe = world.getBlockEntity(neighbor);
        Direction neighborSide = getDirectionFrom(pos, neighbor);

        te.neighborChanged(neighborTe, neighbor, neighborSide);
    }

    @Nullable
    private EnergyDuctMachineTE getTileEntity(IWorldReader world, BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof EnergyDuctMachineTE) return (EnergyDuctMachineTE) te;
        return null;
    }

    @Nullable
    private Direction getDirectionFrom(BlockPos posA, BlockPos posB){
        for(Direction dir : Direction.values()){
            if(posA.relative(dir).equals(posB)) return dir;
        }

        return null;
    }
}*/