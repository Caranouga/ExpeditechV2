package fr.caranouga.expeditech.common.te.custom;

import fr.caranouga.expeditech.common.grids.EnergyGrid;
import fr.caranouga.expeditech.common.grids.ModGrids;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class EnergyDuctMachineTE extends TileEntity {
    private EnergyGrid grid;

    public EnergyDuctMachineTE() {
        super(ModTileEntities.CONSUMER.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(level == null || level.isClientSide) return;

        if(!hasGridAround()){
            grid = createGrid();
        }else{
            if(hasMultipleGrid()){
                grid = fuseGrids();
            }else{
                grid = joinGrid();
            }
        }
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
                grids.add(((EnergyDuctMachineTE) tile).getGrid());
            }
        }

        return grids;
    }

    private boolean hasGridAround() {
        return !getGrids().isEmpty();
    }

    private boolean hasMultipleGrid(){
        return getGrids().size() > 1;
    }

    private EnergyGrid createGrid(){
        EnergyGrid grid = new EnergyGrid();
        ModGrids.GRIDS.add(grid);

        return grid;
    }

    private EnergyGrid joinGrid(){
        return getGrids().get(0).join(this);
    }

    private EnergyGrid fuseGrids(){
        List<EnergyGrid> grids = getGrids();
        EnergyGrid grid = grids.remove(0);
        grid.add(grids);

        return grid;
    }

    public EnergyGrid getGrid() {
        return grid;
    }

    /*@Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;

        Expeditech.LOGGER.info("The consumer at {} has {}", getBlockPos(), this.energyStorage.getEnergyStored());

        //sendOutEnergy();
        setChanged();
    }*/
}
