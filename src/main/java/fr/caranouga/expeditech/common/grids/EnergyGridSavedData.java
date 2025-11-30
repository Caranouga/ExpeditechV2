package fr.caranouga.expeditech.common.grids;

/*
import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.List;

public class EnergyGridSavedData extends WorldSavedData {
    public static final String ID = Expeditech.MODID + "_energy_grids";

    private final List<EnergyGrid> gridList = new ArrayList<>();
    private final ServerWorld currentWorld;

    public EnergyGridSavedData(ServerWorld world) {
        super(ID);

        this.currentWorld = world;
    }

    public void remove(EnergyGrid grid){
        gridList.remove(grid);
        setDirty();
    }

    public void add(EnergyGrid grid){
        gridList.add(grid);
        setDirty();
    }

    public List<EnergyGrid> getGrids(){
        return gridList;
    }

    public static EnergyGrid newGrid(ServerWorld world){
        return new EnergyGrid(get(world), world);
    }

    public static EnergyGridSavedData get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(
                () -> new EnergyGridSavedData(world),
                EnergyGridSavedData.ID
        );
    }

    // region Serialization
    @Override
    public void load(CompoundNBT tag) {
        gridList.clear();

        // TODO: KESAKO LE 10
        ListNBT list = tag.getList("grids", 10);

        for(INBT base : list){
            CompoundNBT gridNbt = (CompoundNBT) base;

            ResourceLocation worldRL = new ResourceLocation(gridNbt.getString("world"));
            ServerWorld world = getServerWorld(worldRL, currentWorld);

            EnergyGrid g = new EnergyGrid(this, world);

            ListNBT ductList = gridNbt.getList("ducts", 10);
            for (INBT d : ductList) {
                g.join(NBTUtil.readBlockPos((CompoundNBT) d));
            }

            ListNBT generatorsList = gridNbt.getList("generators", 10);
            for (INBT e : generatorsList) {
                g.addGenerator(NBTUtil.readBlockPos((CompoundNBT) e));
            }

            ListNBT consumerList = gridNbt.getList("consumers", 10);
            for (INBT c : consumerList) {
                g.addConsumer(NBTUtil.readBlockPos((CompoundNBT) c));
            }

            gridList.add(g);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        ListNBT list = new ListNBT();

        for (EnergyGrid grid : gridList) {
            CompoundNBT gridNbt = new CompoundNBT();

            // Save dim
            gridNbt.putString("world", grid.getWorld().dimension().location().toString());

            // Save ducts
            ListNBT ductList = new ListNBT();
            for (BlockPos ductPos : grid.getDucts().keySet()) {
                ductList.add(NBTUtil.writeBlockPos(ductPos));
            }
            gridNbt.put("ducts", ductList);

            // Save consumers
            ListNBT consumersList = new ListNBT();
            for (BlockPos consumerPos : grid.getConsumers().keySet()) {
                consumersList.add(NBTUtil.writeBlockPos(consumerPos));
            }
            gridNbt.put("consumers", consumersList);

            // Save ducts
            ListNBT generatorList = new ListNBT();
            for (BlockPos generatorPos : grid.getGenerators().keySet()) {
                generatorList.add(NBTUtil.writeBlockPos(generatorPos));
            }
            gridNbt.put("generators", generatorList);

            list.add(gridNbt);
        }

        tag.put("grids", list);

        return tag;
    }
    // endregion

    private ServerWorld getServerWorld(ResourceLocation rl, ServerWorld currentWorld){
        RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, rl);
        return currentWorld.getServer().getLevel(key);
    }
}*/

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.List;

public class EnergyGridSavedData extends WorldSavedData {
    public static final String ID = Expeditech.MODID + "_energy_grids";

    private final List<EnergyGrid> gridList = new ArrayList<>();
    private final ServerWorld currentWorld;

    public EnergyGridSavedData(ServerWorld world) {
        super(ID);

        this.currentWorld = world;
    }

    public static EnergyGridSavedData get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(
                () -> new EnergyGridSavedData(world),
                EnergyGridSavedData.ID
        );
    }

    public static void unregisterDuct(EnergyDuctMachineTE duct) {
        ServerWorld world = (duct.getLevel() == null || duct.getLevel().isClientSide()) ? null : (ServerWorld) duct.getLevel();
        BlockPos pos = duct.getBlockPos();

        if(world == null) return;

        List<EnergyGrid> gridList = new ArrayList<>(get(world).getGridList());

        for (EnergyGrid energyGrid : gridList) {
            energyGrid.removeDuct(pos);
        }
    }

    public static boolean registerNewDuct(EnergyDuctMachineTE duct) {
        ServerWorld world = (duct.getLevel() == null || duct.getLevel().isClientSide()) ? null : (ServerWorld) duct.getLevel();
        BlockPos pos = duct.getBlockPos();

        if(world == null) return false;

        List<EnergyGrid> joinableGrids = getJoinableGrids(world, pos);
        if(joinableGrids.size() > 1) {
            // Fuse
            fuseGrids(world, joinableGrids).addDuct(pos);
        }else if(joinableGrids.size() == 1){
            joinableGrids.get(0).addDuct(pos);
        }else {
            EnergyGrid grid = createNewGrid(world);
            grid.addDuct(pos);
        }

        return true;
    }

    private static EnergyGrid createNewGrid(ServerWorld world){
        EnergyGridSavedData savedData = get(world);
        EnergyGrid grid = new EnergyGrid(savedData, world);
        savedData.addGrid(grid);

        return grid;
    }

    private static EnergyGrid fuseGrids(ServerWorld world, List<EnergyGrid> grids){
        EnergyGrid mainGrid = grids.remove(0);
        ArrayList<EnergyGrid> remainingGrids = new ArrayList<>(grids);

        for (EnergyGrid grid : remainingGrids) {
            mainGrid.fuse(grid);
            get(world).removeGrid(grid);
        }

        return mainGrid;
    }

    private static List<EnergyGrid> getJoinableGrids(ServerWorld world, BlockPos pos){
        List<EnergyGrid> joinableGrids = new ArrayList<>();
        for (EnergyGrid grid : get(world).getGridList()) {
            if(grid.canJoin(pos)) joinableGrids.add(grid);
        }

        return joinableGrids;
    }

    public List<EnergyGrid> getGridList() {
        return gridList;
    }

    private void addGrid(EnergyGrid grid){
        this.gridList.add(grid);
        setDirty();
    }

    public void removeGrid(EnergyGrid grid) {
        this.gridList.remove(grid);
        setDirty();
    }

    void createGridWithDucts(ServerWorld world, List<EnergyDuctMachineTE> ducts) {
        EnergyGrid grid = createNewGrid(world);
        for (EnergyDuctMachineTE duct : ducts) {
            grid.addDuct(duct.getBlockPos());
        }
    }

    // region Serialization
    @Override
    public void load(CompoundNBT tag) {
        gridList.clear();

        ListNBT list = tag.getList("grids", 10);

        for(INBT base : list){
            EnergyGrid g = EnergyGrid.of(currentWorld, (CompoundNBT) base, this);
            gridList.add(g);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        ListNBT list = new ListNBT();

        for (EnergyGrid grid : gridList) {
            list.add(grid.save());
        }

        tag.put("grids", list);

        return tag;
    }
    // endregion
}
