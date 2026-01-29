package fr.caranouga.expeditech.common.tileentities.custom.multiblock;

import fr.caranouga.expeditech.common.blocks.custom.multiblock.AbstractMultiBlockSlave;
import fr.caranouga.expeditech.common.tileentities.custom.machine.BaseTE;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractMultiBlockSlaveTile extends BaseTE {
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
    protected void readSaveLoadTag(CompoundNBT tag) {
        if(tag.contains("masterPos")) {
            pendingMasterPos = NBTUtil.readBlockPos(tag.getCompound("masterPos"));
        }
    }

    @Override
    protected CompoundNBT createSaveLoadTag(CompoundNBT tag) {
        if(masterTile != null) tag.put("masterPos", NBTUtil.writeBlockPos(masterTile.getBlockPos()));

        return tag;
    }
}

/*public abstract class AbstractMultiBlockSlaveTile extends TileEntity {
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
}*/
