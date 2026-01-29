package fr.caranouga.expeditech.common.tileentities.custom.machine;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public abstract class BaseTE extends TileEntity {
    public BaseTE(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    // region Data Saving
    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);

        readSaveLoadTag(nbt);
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT pCompound) {
        return createSaveLoadTag(super.save(pCompound));
    }

    // serverside
    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
        return new SUpdateTileEntityPacket(getBlockPos(), -1, createPcktTag(new CompoundNBT()));
    }

    // clientside
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
        readPcktTag(pkt.getTag());
    }

    // serverside
    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return createTagTag(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);

        readTagTag(tag);
    }

    protected CompoundNBT createSaveLoadTag(CompoundNBT tag) {
        return tag;
    }
    protected CompoundNBT createPcktTag(CompoundNBT tag) {
        return tag;
    }
    protected CompoundNBT createTagTag(CompoundNBT tag) {
        return tag;
    }

    protected void readSaveLoadTag(CompoundNBT tag) {
        
    }
    protected void readPcktTag(CompoundNBT tag) {
        
    }
    protected void readTagTag(CompoundNBT tag) {
        
    }
    // endregion
}
