package fr.caranouga.expeditech.common.grids.data;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.grid.Grid;
import fr.caranouga.expeditech.common.grids.GridType;
import fr.caranouga.expeditech.common.tileentities.custom.duct.DuctTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class GridSavedData<C, D extends DuctTE<C, D>> extends WorldSavedData {
    private static final String BASE_ID = Expeditech.MODID + "_%_grids";

    private final List<Grid<C, D>> gridList = new ArrayList<>();
    protected final ServerWorld refWorld;

    protected GridSavedData(ServerWorld world, GridType type) {
        super(getId(type));

        this.refWorld = world;
    }

    protected static String getId(GridType type){
        return BASE_ID.replace("%", type.getName());
    }

    public void unregisterDuct(D duct){
        BlockPos pos = duct.getBlockPos();

        List<Grid<C, D>> gridList = new ArrayList<>(getGridList());

        for (Grid<C, D> grid : gridList) {
            grid.removeDuct(pos);
        }
    }

    @Nullable
    public Grid<C, D> registerNewDuct(D duct) {
        BlockPos pos = duct.getBlockPos();

        List<Grid<C, D>> joinableGrids = getJoignableGrids(pos);
        if(joinableGrids.size() > 1) {
            // Fuse
            return fuseGrids(joinableGrids).addDuct(pos);
        }else if(joinableGrids.size() == 1){
            return joinableGrids.get(0).addDuct(pos);
        }else {
            return createNewGrid().addDuct(pos);
        }
    }

    private Grid<C, D> createNewGrid(){
        Grid<C, D> grid = createGrid();
        addGrid(grid);

        return grid;
    }

    private Grid<C, D> fuseGrids(List<Grid<C, D>> grids){
        Grid<C, D> mainGrid = grids.remove(0);
        ArrayList<Grid<C, D>> remainingGrids = new ArrayList<>(grids);

        for (Grid<C, D> grid : remainingGrids) {
            mainGrid.fuse(grid);
            removeGrid(grid);
        }

        return mainGrid;
    }

    private List<Grid<C, D>> getJoignableGrids(BlockPos pos){
        List<Grid<C, D>> joignableGrids = new ArrayList<>();

        for (Grid<C, D> grid : getGridList()) {
            if(grid.canJoin(pos)) joignableGrids.add(grid);
        }

        return joignableGrids;
    }

    public List<Grid<C, D>> getGridList() {
        return gridList;
    }

    private void addGrid(Grid<C, D> grid){
        this.gridList.add(grid);
        setDirty();
    }

    public void removeGrid(Grid<C, D> grid) {
        this.gridList.remove(grid);
        setDirty();
    }

    /**
     * This function creates a new grid with the given ducts already registered
     * @param ducts The ducts to register
     * @return The created grid
     */
    public Grid<C, D> createGridWithDucts(Set<D> ducts) {
        Grid<C, D> grid = createNewGrid();

        for (D duct : ducts) {
            grid.addDuct(duct.getBlockPos());
        }

        return grid;
    }

    // region Serialization
    @Override
    public void load(CompoundNBT tag) {
        gridList.clear();

        ListNBT list = tag.getList("grids", 10);

        for(INBT base : list){
            Grid<C, D> g = createGrid((CompoundNBT) base);
            gridList.add(g);
        }
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        ListNBT list = new ListNBT();

        for (Grid<C, D> grid : gridList) {
            list.add(grid.save());
        }

        tag.put("grids", list);

        return tag;
    }
    // endregion

    protected abstract Grid<C, D> createGrid();
    protected abstract Grid<C, D> createGrid(CompoundNBT nbt);
}
