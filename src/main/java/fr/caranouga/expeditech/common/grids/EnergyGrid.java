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

import java.util.*;

import static fr.caranouga.expeditech.common.utils.WorldUtils.getServerWorld;

public class EnergyGrid {
    private final Map<BlockPos, TileEntity> generators = new HashMap<>();
    private final Map<BlockPos, TileEntity> consumers = new HashMap<>();
    private final Map<BlockPos, EnergyDuctMachineTE> ducts = new HashMap<>();
    private final ServerWorld world;
    private final EnergyGridSavedData savedData;

    private boolean isLoaded = false;
    private UUID uid = UUID.randomUUID();

    protected EnergyGrid(EnergyGridSavedData savedData, ServerWorld world){
        this.world = world;
        this.savedData = savedData;

        this.isLoaded = true;

        Expeditech.LOGGER.debug(savedData);

        savedData.setDirty();
    }

    public void tick(){
        if(!isLoaded) return;

        Expeditech.LOGGER.debug("Ticking {}, ducts size {}", uid, ducts.size());
    }

    public boolean canJoin(BlockPos pos){
        for (EnergyDuctMachineTE duct : ducts.values()) {
            for (Direction dir : Direction.values()) {
                if(duct.getBlockPos().relative(dir).equals(pos)) return true;
            }
        }

        return false;
    }

    public void addDuct(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);

        if(te instanceof EnergyDuctMachineTE){
            ducts.put(pos, (EnergyDuctMachineTE) te);
            savedData.setDirty();
        }
    }

    private void addDuctAndNotify(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);

        if(te instanceof EnergyDuctMachineTE){
            ducts.put(pos, (EnergyDuctMachineTE) te);
            savedData.setDirty();

            ((EnergyDuctMachineTE) te).setInGrid(true);
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
            // TODO: En plus des ducts il faut ajouter les autres maps
            savedData.createGridWithDucts(world, split);
        }

        savedData.setDirty();
    }

    private List<EnergyDuctMachineTE> getSplit(){
        List<EnergyDuctMachineTE> split = new ArrayList<>();
        ArrayList<EnergyDuctMachineTE> ductList = new ArrayList<>(ducts.values());
        if(ductList.isEmpty()) return split;

        ductList.remove(0);
        for (EnergyDuctMachineTE duct : ductList) {
            if(!canJoin(duct.getBlockPos())) split.add(ducts.remove(duct.getBlockPos()));
        }

        return split;
    }

    private void addGenerator(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);
        if(te == null) return;

        generators.put(pos, te);
        savedData.setDirty();
    }

    private void addConsumer(BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);
        if(te == null) return;

        consumers.put(pos, te);
        savedData.setDirty();
    }

    public void fuse(EnergyGrid grid) {
        this.ducts.putAll(grid.ducts);
        this.generators.putAll(grid.generators);
        this.consumers.putAll(grid.consumers);
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

        g.isLoaded = true;

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
}