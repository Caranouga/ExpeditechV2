package fr.caranouga.expeditech.common.te.custom;

/*
import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.EnergyGrid;
import fr.caranouga.expeditech.common.grids.EnergyGridSavedData;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class EnergyDuctMachineTE extends TileEntity {
    private EnergyGrid grid;

    public EnergyDuctMachineTE() {
        super(ModTileEntities.ENERGY_DUCT.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) return;

        level.getServer().execute(() -> initializeGridAfterLoad());
    }

    private void initializeGridAfterLoad(){
        if(!hasGridAround() && !isInGrid()){
            grid = createGrid();
            grid.join(this);
        }else{
            if(hasMultipleGrid()){
                grid = fuseGrids();
            }else {
                grid = joinGrid();
            }
        }

        Expeditech.LOGGER.debug("TE at {}: {}", getBlockPos(), grid == null ? "pas de grid lol" : grid.getUID());
    }

    public void onNeighborChanged(BlockPos pos, Direction side){
        Expeditech.LOGGER.debug("aaaaaaa");
        if(level != null && !level.isClientSide) grid.add(pos, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if(grid == null) return;
        grid.remove(this);
    }

    private List<EnergyGrid> getGrids(){
        List<EnergyGrid> grids = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            BlockPos pos = worldPosition.relative(dir);
            TileEntity tile = level.getBlockEntity(pos);

            if(tile == null) continue;
            if(tile instanceof EnergyDuctMachineTE){
                EnergyGrid otherGrid = ((EnergyDuctMachineTE) tile).getGrid();
                if(otherGrid == null) continue;

                grids.add(otherGrid);
            }
        }

        return grids;
    }

    private boolean hasGridAround() {
        return !getGrids().isEmpty();
    }

    private boolean isInGrid() {
        if(level == null || level.isClientSide) return false;
        return EnergyGridSavedData.get((ServerWorld) level).getGrids().stream().anyMatch(grid -> grid.getDucts().containsKey(getBlockPos()));
    }

    private boolean hasMultipleGrid(){
        return getGrids().size() > 1;
    }

    private EnergyGrid createGrid(){
        EnergyGrid grid = EnergyGridSavedData.newGrid((ServerWorld) level);
        EnergyGridSavedData.get((ServerWorld) level).add(grid);

        return grid;
    }

    private EnergyGrid joinGrid(){
        return getGrids().get(0).join(this);
    }

    private EnergyGrid fuseGrids(){
        List<EnergyGrid> grids = getGrids();
        EnergyGrid grid = grids.remove(0);
        grid.add(grids);

        return grid.join(this);
    }

    public EnergyGrid getGrid() {
        return grid;
    }

    public void setGrid(EnergyGrid grid) {
        this.grid = grid;
    }
}*/

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.EnergyGrid;
import fr.caranouga.expeditech.common.grids.EnergyGridSavedData;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyDuctMachineTE extends TileEntity {
    private EnergyGrid grid;

    public EnergyDuctMachineTE() {
        super(ModTileEntities.ENERGY_DUCT.get());
    }

    public EnergyDuctMachineTE(TileEntityType<?> type) {
        super(type);
    }

    public void onPlaced(){
        grid = EnergyGridSavedData.registerNewDuct(this);
    }

    public void setGrid(EnergyGrid grid){
        this.grid = grid;
    }

    @Override
    public void setRemoved() {
        if(grid == null) super.setRemoved();

        EnergyGridSavedData.unregisterDuct(this);

        super.setRemoved();
    }

    public void neighborChanged(@Nullable TileEntity neighbor, @Nonnull BlockPos neighborPos, @Nullable Direction side) {
        if(grid == null) return;

        if(neighbor == null){
            grid.tryRemove(neighborPos);
            return;
        }

        neighbor.getCapability(CapabilityEnergy.ENERGY, side).ifPresent(cons -> {
            if(cons.canReceive()) grid.addConsumer(neighbor.getBlockPos());
        });
        neighbor.getCapability(CapabilityEnergy.ENERGY, side).ifPresent(cons -> {
            if(cons.canExtract()) grid.addGenerator(neighbor.getBlockPos());
        });
    }

    public int getMaxTransferPerTick() {
        return 10;
    }
}
