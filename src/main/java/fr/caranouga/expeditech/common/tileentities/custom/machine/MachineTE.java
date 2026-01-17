package fr.caranouga.expeditech.common.tileentities.custom.machine;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class MachineTE extends TileEntity implements ITickableTileEntity {
    public MachineTE(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide) return;

        serverTick();
    }

    // region Data Saving (World load/save)
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

    protected abstract CompoundNBT createTagClient(CompoundNBT tag);
    protected abstract CompoundNBT createTagServer(CompoundNBT tag);

    protected abstract void readTagServer(CompoundNBT tag);
    protected abstract void readTagClient(CompoundNBT tag);
    // endregion

    protected abstract void serverTick();
}
