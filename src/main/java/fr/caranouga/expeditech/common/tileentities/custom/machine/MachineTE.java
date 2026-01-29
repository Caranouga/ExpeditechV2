package fr.caranouga.expeditech.common.tileentities.custom.machine;

import fr.caranouga.expeditech.common.capabilities.energy.CustomEnergyStorage;
import fr.caranouga.expeditech.common.tileentities.custom.machine.interfaces.IHasEnergy;
import fr.caranouga.expeditech.common.tileentities.custom.machine.interfaces.IHasInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class MachineTE extends BaseTE implements ITickableTileEntity {
    private CustomEnergyStorage energyStorage;
    private LazyOptional<CustomEnergyStorage> lazyEnergyStorage = LazyOptional.empty();
    private ItemStackHandler itemHandler;
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public MachineTE(TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        initCapabilities();
    }

    private void initCapabilities(){
        if(this instanceof IHasEnergy){
            energyStorage = ((IHasEnergy) this).createEnergyStorage();
            lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        }
        if(this instanceof IHasInventory){
            itemHandler = ((IHasInventory) this).createInventory();
            lazyItemHandler = LazyOptional.of(() -> itemHandler);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY && this.energyStorage != null) return lazyEnergyStorage.cast();
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.itemHandler != null) return lazyItemHandler.cast();

        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();

        lazyEnergyStorage.invalidate();
        lazyItemHandler.invalidate();
    }

    @Override
    public void tick() {
        if (this.level == null) return;

        serverTick();
    }



    protected CompoundNBT createSaveLoadTag(CompoundNBT tag){
        if(this.energyStorage != null) tag.put("Energy", this.energyStorage.serializeNBT());

        return tag;
    }

    protected void readSaveLoadTag(CompoundNBT tag){
        if (this.energyStorage != null && tag.contains("Energy")) {
            this.energyStorage.deserializeNBT(tag.getCompound("Energy"));
        }
    }
    // endregion

    protected Optional<CustomEnergyStorage> getEnergyStorage() {
        return Optional.ofNullable(energyStorage);
    }

    protected Optional<ItemStackHandler> getItemHandler() {
        return Optional.ofNullable(itemHandler);
    }

    protected abstract void serverTick();
}
/*import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.capabilities.energy.CustomEnergyStorage;
import fr.caranouga.expeditech.common.tileentities.custom.machine.interfaces.IHasEnergy;
import fr.caranouga.expeditech.common.tileentities.custom.machine.interfaces.IHasInventory;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class MachineTE extends TileEntity implements ITickableTileEntity {
    private CustomEnergyStorage energyStorage;
    private LazyOptional<CustomEnergyStorage> lazyEnergyStorage = LazyOptional.empty();
    private ItemStackHandler itemHandler;
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public MachineTE(TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        initCapabilities();
    }

    private void initCapabilities(){
        if(this instanceof IHasEnergy){
            energyStorage = ((IHasEnergy) this).createEnergyStorage();
            lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        }
        if(this instanceof IHasInventory){
            itemHandler = ((IHasInventory) this).createInventory();
            lazyItemHandler = LazyOptional.of(() -> itemHandler);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY && this.energyStorage != null) return lazyEnergyStorage.cast();
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.itemHandler != null) return lazyItemHandler.cast();

        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();

        lazyEnergyStorage.invalidate();
        lazyItemHandler.invalidate();
    }

    @Override
    public void tick() {
        if (this.level == null) return;

        serverTick();
    }

    // region Data Saving (World load/save)
    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);

        readSaveLoadTag(nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
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
    public CompoundNBT getUpdateTag() {
        return createTagTag(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);

        readTagTag(tag);
    }

    protected CompoundNBT createSaveLoadTag(CompoundNBT tag){
        if(this.energyStorage != null) tag.put("Energy", this.energyStorage.serializeNBT());

        Expeditech.LOGGER.debug(tag);

        return tag;
    }

    protected CompoundNBT createPcktTag(CompoundNBT tag){
        return tag;
    }

    protected CompoundNBT createTagTag(CompoundNBT tag){
        return tag;
    }

    protected void readSaveLoadTag(CompoundNBT tag){
        if (this.energyStorage != null && tag.contains("Energy")) {
            this.energyStorage.deserializeNBT(tag.getCompound("Energy"));
        }

        Expeditech.LOGGER.debug(tag);
    }

    protected void readPcktTag(CompoundNBT tag){

    }

    protected void readTagTag(CompoundNBT tag){

    }
    // endregion

    protected Optional<CustomEnergyStorage> getEnergyStorage() {
        return Optional.ofNullable(energyStorage);
    }

    protected Optional<ItemStackHandler> getItemHandler() {
        return Optional.ofNullable(itemHandler);
    }

    protected abstract void serverTick();
}
*/