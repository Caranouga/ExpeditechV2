package fr.caranouga.expeditech.common.grids.grid;

import fr.caranouga.expeditech.common.grids.Graph;
import fr.caranouga.expeditech.common.grids.Node;
import fr.caranouga.expeditech.common.grids.Path;
import fr.caranouga.expeditech.common.grids.cap.GridCapabilityWrapper;
import fr.caranouga.expeditech.common.grids.data.GridSavedData;
import fr.caranouga.expeditech.common.tileentities.custom.duct.DuctTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;

import java.util.*;

public abstract class Grid<C, D extends DuctTE<C, D>> {
    private final Map<BlockPos, TileEntity> generators = new HashMap<>();
    private final Map<BlockPos, TileEntity> consumers = new HashMap<>();
    private final Map<BlockPos, D> ducts = new HashMap<>();
    private final ServerWorld world;
    private final GridSavedData<C, D> savedData;
    private final Graph<D> graph;
    private final Class<D> ductClass;
    private final GridCapabilityWrapper<C> capWrapper;

    // TODO: Randomiser les pair gen/cons

    public Grid(GridSavedData<C, D> savedData, ServerWorld world, Class<D> ductClass, GridCapabilityWrapper<C> capWrapper){
        this.world = world;
        this.savedData = savedData;
        this.ductClass = ductClass;
        this.graph = new Graph<>(ductClass);
        this.capWrapper = capWrapper;

        setChanged();
    }

    public Grid(ServerWorld worldRef, CompoundNBT nbt, GridSavedData<C, D> savedData, Class<D> ductClass, GridCapabilityWrapper<C> capWrapper){
        this(savedData, worldRef, ductClass, capWrapper);

        ListNBT ductList = nbt.getList("ducts", 10);
        for (INBT d : ductList) {
            addDuctAndNotify(NBTUtil.readBlockPos((CompoundNBT) d));
        }

        ListNBT generatorsList = nbt.getList("generators", 10);
        for (INBT e : generatorsList) {
            addGenerator(NBTUtil.readBlockPos((CompoundNBT) e));
        }

        ListNBT consumerList = nbt.getList("consumers", 10);
        for (INBT c : consumerList) {
            addConsumer(NBTUtil.readBlockPos((CompoundNBT) c));
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void tick(){
        Map<Node<D>, Integer> remainingCapacity = new HashMap<>();

        graph.calculateCapacity(remainingCapacity);

        for (Map.Entry<BlockPos, TileEntity> genEntry : generators.entrySet()) {
            BlockPos genPos = genEntry.getKey();
            TileEntity genTe = genEntry.getValue();
            C genCap = genTe.getCapability(getCapability()).orElse(null);
            if(genCap == null) continue;

            int availableEnergy = capWrapper.extract(genCap, Integer.MAX_VALUE, true);
            if(availableEnergy == 0) continue;

            for (Map.Entry<BlockPos, TileEntity> consEntry : consumers.entrySet()) {
                BlockPos consPos = consEntry.getKey();
                TileEntity consTe = consEntry.getValue();
                C consCap = consTe.getCapability(getCapability()).orElse(null);
                if(consCap == null) continue;

                int requiredEnergy = capWrapper.receive(consCap, Integer.MAX_VALUE, true);
                if(requiredEnergy == 0) continue;

                Path<D> path = graph.getPath(genPos, consPos);
                if(path == null) continue;

                // Effective transfer is limited by:
                // - generator energy
                // - consumer need
                // - smallest remaining duct capacity on path
                int pathCapabity = Integer.MAX_VALUE;


                for (Node<D> duct : path.getDucts()) {
                    if(!duct.isDuct()) continue;

                    pathCapabity = Math.min(pathCapabity, remainingCapacity.get(duct));
                }

                int energyToTransfer = Math.min(availableEnergy, Math.min(requiredEnergy, pathCapabity));
                if(energyToTransfer <= 0) continue;

                // Real transfer
                int realExtract = capWrapper.extract(genCap, energyToTransfer, false);
                int realReceive = capWrapper.receive(consCap, realExtract, false);

                // Deduct from remaining duct capacities
                for (Node<D> duct : path.getDucts()) {
                    if(!duct.isDuct()) continue;

                    remainingCapacity.put(duct, remainingCapacity.get(duct) - realReceive);
                }
            }
        }

        remainingCapacity.clear();
    }

    public boolean canJoin(BlockPos pos){
        for (D duct : ducts.values()) {
            for (Direction dir : Direction.values()) {
                if(duct.getBlockPos().relative(dir).equals(pos)) return true;
            }
        }

        return false;
    }


    @SuppressWarnings("unchecked")
    public Grid<C, D> addDuct(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);

        if(ductClass.isInstance(te)){
            ducts.put(pos, (D) te);
            setChanged();
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    private void addDuctAndNotify(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);

        if(ductClass.isInstance(te)){
            ducts.put(pos, (D) te);
            setChanged();

            ((D) te).setGrid(this);
        }
    }

    public void removeDuct(BlockPos pos) {
        if(ducts.remove(pos) == null) return;
        if(ducts.isEmpty()){
            savedData.removeGrid(this);
            return;
        }

        List<Set<D>> components = new ArrayList<>();
        Set<D> visited = new HashSet<>();

        for (D start : ducts.values()) {
            if(visited.contains(start)) continue;

            components.add(BFS(start, visited));
        }

        // Nothing split
        if(components.size() == 1){
            setChanged();
            return;
        }

        Set<Grid<C, D>> grids = new HashSet<>();

        for (Set<D> component : components) {
            Grid<C, D> createdGrid = savedData.createGridWithDucts(component);
            grids.add(createdGrid);
        }

        for (Grid<C, D> grid : grids) {
            consumers.keySet().forEach(cons -> {
                if(grid.canJoin(cons)) grid.addConsumer(cons);
            });
            generators.keySet().forEach(gen -> {
                if(grid.canJoin(gen)) grid.addGenerator(gen);
            });
        }

        savedData.removeGrid(this);
    }

    public void addGenerator(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);
        if(te == null) return;

        generators.put(pos, te);
        setChanged();
    }

    public void addConsumer(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);
        if(te == null) return;

        consumers.put(pos, te);
        setChanged();
    }

    /**
     * This function fuse two grids together
     * @param grid The grid to fuse with this one
     */
    public void fuse(Grid<C, D> grid) {
        this.ducts.putAll(grid.ducts);
        this.generators.putAll(grid.generators);
        this.consumers.putAll(grid.consumers);

        setChanged();
    }

    public CompoundNBT save(){
        CompoundNBT gridNbt = new CompoundNBT();

        // Save dim
        gridNbt.putString("world", world.dimension().location().toString());

        // Save ducts
        ListNBT ductList = new ListNBT();
        for (BlockPos ductPos : ducts.keySet()) {
            ductList.add(NBTUtil.writeBlockPos(ductPos));
        }
        gridNbt.put("ducts", ductList);

        // Save consumers
        ListNBT consumersList = new ListNBT();
        for (BlockPos consumerPos : consumers.keySet()) {
            consumersList.add(NBTUtil.writeBlockPos(consumerPos));
        }
        gridNbt.put("consumers", consumersList);

        // Save ducts
        ListNBT generatorList = new ListNBT();
        for (BlockPos generatorPos : generators.keySet()) {
            generatorList.add(NBTUtil.writeBlockPos(generatorPos));
        }
        gridNbt.put("generators", generatorList);

        return gridNbt;
    }

    public void tryRemove(BlockPos pos) {
        consumers.remove(pos);
        generators.remove(pos);

        setChanged();
    }

    private void setChanged(){
        graph.setChanged(world, generators.keySet(), consumers.keySet(), ducts.keySet());
    }

    @SuppressWarnings("unchecked")
    private Set<D> BFS(D start, Set<D> visited){
        Set<D> comp = new HashSet<>();
        Deque<D> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty()){
            D p = queue.poll();
            if(!visited.add(p)) continue;

            comp.add(p);

            for (Direction dir : Direction.values()) {
                BlockPos pos = p.getBlockPos().relative(dir);

                TileEntity te = world.getBlockEntity(pos);
                if(!ductClass.isInstance(te)) continue;
                D n = (D) te;

                if (ducts.containsValue(n) && !visited.contains(n)) {
                    queue.add(n);
                }
            }
        }

        return comp;
    }

    protected abstract Capability<C> getCapability();
}