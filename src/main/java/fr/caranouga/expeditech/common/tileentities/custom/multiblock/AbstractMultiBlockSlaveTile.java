package fr.caranouga.expeditech.common.tileentities.custom.multiblock;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.custom.multiblock.AbstractMultiBlockSlave;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractMultiBlockSlaveTile extends TileEntity {
    // TODO: Use the machine TE ?
    private AbstractMultiBlockMasterTile masterTile;
    private BlockPos pendingMasterPos = null;

    public AbstractMultiBlockSlaveTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public void setMaster(BlockPos masterPos, World masterWorld) {
        TileEntity tile = masterWorld.getBlockEntity(masterPos);
        if (tile instanceof AbstractMultiBlockMasterTile) {
            this.masterTile = (AbstractMultiBlockMasterTile) tile;
        } else {
            throw new IllegalArgumentException("The provided master position does not contain a multiBlock master.");
        }

        BlockState state = getBlockState();
        level.setBlockAndUpdate(getBlockPos(), state.setValue(AbstractMultiBlockSlave.BUILT, true));
        setChanged();
    }

    public void masterBroken(){
        this.masterTile = null;

        BlockState state = getBlockState();
        level.setBlockAndUpdate(getBlockPos(), state.setValue(AbstractMultiBlockSlave.BUILT, false));
        setChanged();
    }

    @Nullable
    public AbstractMultiBlockMasterTile getMasterTile() {
        return masterTile;
    }

    public void broken(){
        if (masterTile != null) {
            masterTile.slaveBroken();
        }
    }

    @Override
    public void setRemoved() {
        broken();
        super.setRemoved();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (pendingMasterPos != null && this.level != null) {
            setMaster(pendingMasterPos, this.level);
            pendingMasterPos = null; // Clear after setting
        }

        this.level.setBlockAndUpdate(getBlockPos(), getBlockState());
        setChanged();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);

        if(compound.contains("masterPos")) {
            pendingMasterPos = NBTUtil.readBlockPos(compound.getCompound("masterPos"));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        CompoundNBT nbt = super.save(pCompound);

        if(masterTile != null) nbt.put("masterPos", NBTUtil.writeBlockPos(masterTile.getBlockPos()));

        return nbt;
    }

    /*private AbstractMultiBlockMasterTile masterTile;
    private BlockState originalBlock;
    private BlockPos pendingMasterPos = null;

    public SlaveMultiBlockTile() {
        super(ModTileEntities.MB_SLAVE.get());
    }

    public void setMaster(BlockPos masterPos, World masterWorld) {
        TileEntity tile = masterWorld.getBlockEntity(masterPos);
        if (tile instanceof AbstractMultiBlockMasterTile) {
            this.masterTile = (AbstractMultiBlockMasterTile) tile;
        } else {
            throw new IllegalArgumentException("The provided master position does not contain a MasterMbTile.");
        }

        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.NOTIFY_NEIGHBORS);
        setChanged();
    }

    @Nullable
    public AbstractMultiBlockMasterTile getMasterTile() {
        return masterTile;
    }

    public void setOriginalBlock(BlockState state){
        this.originalBlock = state;

        if(this.level != null) {
            Block block = state.getBlock();
            ToolType harvestTool = block.getHarvestTool(state);
            int harvestLevel = block.getHarvestLevel(state);

            MultiBlockSlave slave = ((MultiBlockSlave) getBlockState().getBlock());
            slave.harvestTool = harvestTool;
            slave.harvestLevel = harvestLevel;

            level.setBlockAndUpdate(this.worldPosition, slave.defaultBlockState());
        }

        //level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.NOTIFY_NEIGHBORS);
        setChanged();
    }

    public void broken(){
        if (masterTile != null) {
            masterTile.slaveBroken(this);
        }
    }

    @Override
    public void setRemoved() {
        broken();
        super.setRemoved();
    }

    @Nullable
    public BlockState getOriginalBlockState(){
        return originalBlock;
    }

    // region Data Saving (World load/save)
    // 2 fois: server puis client
    @Override
    public void onLoad() {
        super.onLoad();

        if (pendingMasterPos != null && this.level != null) {
            setMaster(pendingMasterPos, this.level);
            masterTile.registerNewSlave(this);
            pendingMasterPos = null; // Clear after setting
        }

        this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.NOTIFY_NEIGHBORS);
        setChanged();
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);

        readTagServer(nbt);
        readTagClient(nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        CompoundNBT tag = createTagServer(super.save(pCompound));
        return createTagClient(tag);
    }

    // serverside
    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
        return new SUpdateTileEntityPacket(getBlockPos(), -1, createTagClient(new CompoundNBT()));
    }

    // clientside
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
        CompoundNBT tag = pkt.getTag();

        readTagClient(tag);
    }

    // serverside
    // We use the default handleUpdateTag
    @Override
    public CompoundNBT getUpdateTag() {
        return createTagClient(super.getUpdateTag());
    }

    private CompoundNBT createTagClient(CompoundNBT tag){
        if(originalBlock != null) tag.put("ogBlock", NBTUtil.writeBlockState(originalBlock));

        return tag;
    }

    private CompoundNBT createTagServer(CompoundNBT tag){
        if(masterTile != null) tag.put("masterPos", NBTUtil.writeBlockPos(masterTile.getBlockPos()));

        return tag;
    }

    private void readTagServer(CompoundNBT tag){
        if (tag.contains("masterPos")) {
            pendingMasterPos = NBTUtil.readBlockPos(tag.getCompound("masterPos"));
        }
    }

    private void readTagClient(CompoundNBT tag){
        if (tag.contains("ogBlock")) {
            //originalBlock = NBTUtil.readBlockState(tag.getCompound("ogBlock"));
            setOriginalBlock(NBTUtil.readBlockState(tag.getCompound("ogBlock")));
        }
    }
    // endregion*/
}
