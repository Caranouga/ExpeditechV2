package fr.caranouga.expeditech.common.grids;

/*
import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import fr.caranouga.expeditech.common.te.custom.GeneratorMachineTE;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;

public class EnergyGrid {
    private final EnergyGridSavedData data;

    private Map<BlockPos, TileEntity> consumers = new HashMap<>();
    private Map<BlockPos, TileEntity> generators = new HashMap<>();
    private Map<BlockPos, EnergyDuctMachineTE> ducts = new HashMap<>();
    private final UUID gridId = UUID.randomUUID();
    private final ServerWorld world;

    protected EnergyGrid(EnergyGridSavedData data, ServerWorld world){
        this.data = data;
        this.world = world;
    }

    public void tick(){
    }

    public EnergyGrid join(EnergyDuctMachineTE duct){
        Expeditech.LOGGER.debug("Duct at {} joined grid {}", duct.getBlockPos(), gridId);

        ducts.put(duct.getBlockPos(), duct);

        setChanged();

        return this;
    }

    public void join(BlockPos ductPos){
        TileEntity te = world.getBlockEntity(ductPos);

        Expeditech.LOGGER.info(te);

        if(te instanceof EnergyDuctMachineTE){
            join((EnergyDuctMachineTE) te);
            return;
        }
        throw new IllegalArgumentException("Tried to add a non valid TE to the list of ducts");
    }

    public void addGenerator(BlockPos genPos) {
        TileEntity te = world.getBlockEntity(genPos);
        if(te == null) throw new IllegalArgumentException("Tried to add a non existing TE to the list of generators");

        generators.put(genPos, te);

        setChanged();
    }

    public void addConsumer(BlockPos consPos) {
        TileEntity te = world.getBlockEntity(consPos);
        if(te == null) throw new IllegalArgumentException("Tried to add a non existing TE to the list of consumers");

        consumers.put(consPos, te);

        setChanged();
    }

    public void add(BlockPos blockPos, Direction side) {
        TileEntity te = world.getBlockEntity(blockPos);
        if(te == null) {
            if(world.getBlockState(blockPos).is(Blocks.AIR)) {
                if (generators.get(blockPos) != null) generators.remove(blockPos);
                if (consumers.get(blockPos) != null) consumers.remove(blockPos);
            }

            return;
        }

        LazyOptional<IEnergyStorage> energyLazyOptional = te.getCapability(CapabilityEnergy.ENERGY, side);
        Expeditech.LOGGER.debug(te);
        Expeditech.LOGGER.debug(energyLazyOptional);
        Expeditech.LOGGER.debug(energyLazyOptional.isPresent());
        Expeditech.LOGGER.debug(te instanceof GeneratorMachineTE);
        if(!energyLazyOptional.isPresent()) return;
        Expeditech.LOGGER.debug("bbbbbbbbb");

        if(energyLazyOptional.orElseThrow(
                () -> new IllegalStateException("Could not get IEnergyStorage for TE at " + blockPos + " with direction " + side)
        ).canExtract() && !generators.containsKey(blockPos)) generators.put(blockPos, te);
        if(energyLazyOptional.orElseThrow(
                () -> new IllegalStateException("Could not get IEnergyStorage for TE at " + blockPos + " with direction " + side)
        ).canReceive() && !consumers.containsKey(blockPos)) consumers.put(blockPos, te);

        Expeditech.LOGGER.debug("Added {} ({})", blockPos, side);
    }

    public void add(List<EnergyGrid> grids){
        for (EnergyGrid grid : grids) {
            Expeditech.LOGGER.debug("Fused grid {} to {}", grid.gridId, gridId);

            consumers.putAll(grid.consumers);
            generators.putAll(grid.generators);
            ducts.putAll(grid.ducts);

            // il faut peut être changer avec duct.grid = ... ?????

            EnergyGridSavedData worldGrid = EnergyGridSavedData.get(world);
            worldGrid.remove(grid);

            Expeditech.LOGGER.debug("Remainig grids:");
            worldGrid.getGrids().forEach(gr -> Expeditech.LOGGER.debug(gr.gridId));

            for (EnergyDuctMachineTE duct : ducts.values()) {
                Expeditech.LOGGER.debug("Duct at {}", duct.getBlockPos());
                duct.setGrid(this);
            }
        }

        setChanged();
    }

    public void remove(EnergyDuctMachineTE duct) {
        ducts.remove(duct);

        if(ducts.isEmpty()) {
            EnergyGridSavedData worldGrid = EnergyGridSavedData.get(world);
            worldGrid.remove(this);
        }

        setChanged();
    }

    private void setChanged(){
        data.setDirty();
    }

    public Map<BlockPos, TileEntity> getConsumers() {
        return consumers;
    }

    public Map<BlockPos, TileEntity> getGenerators() {
        return generators;
    }

    public Map<BlockPos, EnergyDuctMachineTE> getDucts() {
        return ducts;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public UUID getUID() {
        return gridId;
    }
}*/

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;

import static fr.caranouga.expeditech.common.utils.WorldUtils.getServerWorld;

public class EnergyGrid {
    private final Map<BlockPos, TileEntity> generators = new HashMap<>();
    private final Map<BlockPos, TileEntity> consumers = new HashMap<>();
    private final Map<BlockPos, EnergyDuctMachineTE> ducts = new HashMap<>();
    private final ServerWorld world;
    private final EnergyGridSavedData savedData;

    private UUID uid = UUID.randomUUID();
    private EnergyGraph graph = new EnergyGraph();


    private Map<BlockPos, IEnergyStorage> genStorages = new HashMap<>();
    private Map<BlockPos, IEnergyStorage> consStorages = new HashMap<>();

    // TODO: cache
    // TODO: Randomiser les pair gen/cons

    protected EnergyGrid(EnergyGridSavedData savedData, ServerWorld world){
        this.world = world;
        this.savedData = savedData;

        setChanged();
    }

    public void tick(){
        Map<Node, Integer> remainingCapacity = new HashMap<>();

        graph.calculateCapacity(remainingCapacity);

        for (Map.Entry<BlockPos, TileEntity> genEntry : generators.entrySet()) {
            BlockPos genPos = genEntry.getKey();
            TileEntity genTe = genEntry.getValue();
            IEnergyStorage genCap = genTe.getCapability(CapabilityEnergy.ENERGY).orElse(null);
            if(genCap == null) continue;

            genStorages.put(genPos, genCap);

            int availableEnergy = genCap.extractEnergy(Integer.MAX_VALUE, true);
            if(availableEnergy == 0) continue;

            for (Map.Entry<BlockPos, TileEntity> consEntry : consumers.entrySet()) {
                BlockPos consPos = consEntry.getKey();
                TileEntity consTe = consEntry.getValue();
                IEnergyStorage consCap = consTe.getCapability(CapabilityEnergy.ENERGY).orElse(null);
                if(consCap == null) continue;

                consStorages.put(consPos, consCap);

                int requiredEnergy = consCap.receiveEnergy(Integer.MAX_VALUE, true);
                if(requiredEnergy == 0) continue;

                Path path = graph.getPath(genPos, consPos);
                if(path == null) continue;

                // Effective transfer is limited by:
                // - generator energy
                // - consumer need
                // - smallest remaining duct capacity on path
                int pathCapabity = Integer.MAX_VALUE;


                for (Node duct : path.getDucts()) {
                    if(!duct.isDuct()) continue;

                    pathCapabity = Math.min(pathCapabity, remainingCapacity.get(duct));
                }

                int energyToTransfer = Math.min(availableEnergy, Math.min(requiredEnergy, pathCapabity));
                if(energyToTransfer <= 0) continue;

                // Real transfer
                int realExtract = genCap.extractEnergy(energyToTransfer, false);
                int realReceive = consCap.receiveEnergy(realExtract, false);

                // Deduct from remaining duct capacities
                for (Node duct : path.getDucts()) {
                    if(!duct.isDuct()) continue;

                    remainingCapacity.put(duct, remainingCapacity.get(duct) - realReceive);
                }
            }
        }

        remainingCapacity.clear();

        /*for each generator G:
            get availableEnergy from G.simulateExtract(maxInt)

            if availableEnergy == 0: continue

            for each consumer C:
                get requiredEnergy from C.simulateReceive(maxInt)

                if requiredEnergy == 0: continue

                        Path p = graph.getPath(G.position, C.position)

                // Effective transfer is limited by:
                // - generator energy
                // - consumer need
                // - smallest remaining duct capacity on path
                pathCapacity = +infinity
                for each duct D in p:
                    pathCapacity = min(pathCapacity, remainingCapacity[D])

                energyToTransfer = min(availableEnergy, requiredEnergy, pathCapacity)

                if energyToTransfer <= 0: continue

                        // Real transfer
                        realExtract = G.extractEnergy(energyToTransfer, false)
                realReceive = C.receiveEnergy(realExtract, false)

                // Deduct from remaining duct capacities
                for each duct D in p:
                    remainingCapacity[D] -= realReceive*/

    }

    public boolean canJoin(BlockPos pos){
        for (EnergyDuctMachineTE duct : ducts.values()) {
            for (Direction dir : Direction.values()) {
                if(duct.getBlockPos().relative(dir).equals(pos)) return true;
            }
        }

        return false;
    }

    public EnergyGrid addDuct(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);

        if(te instanceof EnergyDuctMachineTE){
            ducts.put(pos, (EnergyDuctMachineTE) te);
            setChanged();
        }

        return this;
    }

    private void addDuctAndNotify(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);

        if(te instanceof EnergyDuctMachineTE){
            ducts.put(pos, (EnergyDuctMachineTE) te);
            setChanged();

            ((EnergyDuctMachineTE) te).setGrid(this);
        }
    }

    public void removeDuct(BlockPos pos) {
        ducts.remove(pos);
        if(ducts.isEmpty()){
            savedData.removeGrid(this);
            return;
        }

        List<EnergyDuctMachineTE> split = getSplit();
        if(!split.isEmpty()){
            EnergyGrid createdGrid = savedData.createGridWithDucts(world, split);
            switchGrid(createdGrid);
        }

        setChanged();
    }

    private void switchGrid(EnergyGrid newGrid){
        Set<BlockPos> consumersPos = consumers.keySet();
        for (BlockPos consumer : consumersPos) {
            for (Direction dir : Direction.values()) {
                if(!ducts.containsKey(consumer.relative(dir))){
                    consumers.remove(consumer);
                    newGrid.addConsumer(consumer);
                }
            }
        }

        Set<BlockPos> generatorPos = generators.keySet();
        for (BlockPos generator : generatorPos) {
            for (Direction dir : Direction.values()) {
                if(!ducts.containsKey(generator.relative(dir))){
                    generators.remove(generator);
                    newGrid.addGenerator(generator);
                }
            }
        }

        setChanged();
    }

    private List<EnergyDuctMachineTE> getSplit(){
        List<EnergyDuctMachineTE> split = new ArrayList<>();
        ArrayList<EnergyDuctMachineTE> ductList = new ArrayList<>(ducts.values());
        if(ductList.isEmpty()) return split;

        ductList.remove(0);
        for (EnergyDuctMachineTE duct : ductList) {
            if(!canJoin(duct.getBlockPos())) split.add(ducts.remove(duct.getBlockPos()));
        }

        setChanged();

        return split;
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

    public void fuse(EnergyGrid grid) {
        this.ducts.putAll(grid.ducts);
        this.generators.putAll(grid.generators);
        this.consumers.putAll(grid.consumers);

        setChanged();
    }

    public static EnergyGrid of(ServerWorld worldRef, CompoundNBT nbt, EnergyGridSavedData savedData){
        ResourceLocation worldRL = new ResourceLocation(nbt.getString("world"));
        ServerWorld world = getServerWorld(worldRL, worldRef);

        EnergyGrid g = new EnergyGrid(savedData, world);

        ListNBT ductList = nbt.getList("ducts", 10);
        for (INBT d : ductList) {
            g.addDuctAndNotify(NBTUtil.readBlockPos((CompoundNBT) d));
        }

        ListNBT generatorsList = nbt.getList("generators", 10);
        for (INBT e : generatorsList) {
            g.addGenerator(NBTUtil.readBlockPos((CompoundNBT) e));
        }

        ListNBT consumerList = nbt.getList("consumers", 10);
        for (INBT c : consumerList) {
            g.addConsumer(NBTUtil.readBlockPos((CompoundNBT) c));
        }

        return g;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("----== ==----\n");
        builder.append("UUID: ").append(uid).append("\n");
        builder.append("World: ").append(world).append("\n");
        builder.append("Ducts: ").append("\n");
        for (BlockPos duct : ducts.keySet()) {
            builder.append("    ").append(duct).append("\n");
        }
        builder.append("Generators: ").append("\n");
        for (BlockPos generator : generators.keySet()) {
            builder.append("    ").append(generator).append("\n");
        }
        builder.append("Consumers: ").append("\n");
        for (BlockPos consumer : consumers.keySet()) {
            builder.append("    ").append(consumer).append("\n");
        }
        builder.append("----== ==----");
        return builder.toString();
    }
}