package fr.caranouga.expeditech.common.tileentities.custom.machine;

import fr.caranouga.expeditech.common.blocks.custom.MachineBlock;
import fr.caranouga.expeditech.common.capabilities.energy.CustomEnergyStorage;
import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import fr.caranouga.expeditech.common.tileentities.custom.machine.interfaces.IHasEnergy;
import fr.caranouga.expeditech.common.tileentities.custom.machine.interfaces.IHasInventory;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CoalGeneratorMachineTE extends MachineTE implements IHasEnergy, IHasInventory {
    private static final int FUEL_INPUT_SLOT = 0;
    private static final int ENERGY_PER_TICK = 1;

    private int requiredTime = 0;
    private int currentBurnTime = 0;

    public CoalGeneratorMachineTE() {
        super(ModTileEntities.COAL_GENERATOR.get());
    }

    public CustomEnergyStorage createEnergyStorage(){
        return new CustomEnergyStorage(1000, 0, 1000);
    }

    @Override
    public ItemStackHandler createInventory() {
        return new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == FUEL_INPUT_SLOT) {
                    return isItemBurnable(stack);
                }
                return false; // Invalid slot
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    protected void serverTick() {
        if(isBurning()){
            currentBurnTime++;
            if(!isFull(ENERGY_PER_TICK)) putEnergy();
        }else{
            if(isItemBurnable(getBurnSlot()) && !isFull()){
                setTimes();
                useItem();
                setPowered(true);
            }else{
                resetTimes();
                setPowered(false);
            }
        }

        setChanged();

        //sendOutEnergy();
    }

    private boolean isBurning(){
        return currentBurnTime < requiredTime;
    }

    private void setPowered(boolean powered){
        // We have already check if level is null
        BlockState state = level.getBlockState(getBlockPos());
        level.setBlock(getBlockPos(), state.setValue(MachineBlock.RUNNING, powered), Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
    }

    private void resetTimes(){
        requiredTime = 0;
        currentBurnTime = 0;
    }

    private void setTimes(){
        requiredTime = getItemBurnTime(getBurnSlot());
        currentBurnTime = 0;
    }

    private void useItem(){
        getItemHandler().ifPresent(handler -> handler.extractItem(FUEL_INPUT_SLOT, 1, false));
    }

    private void putEnergy(){
        getEnergyStorage().ifPresent(storage -> storage.addEnergy(ENERGY_PER_TICK));
    }

    private ItemStack getBurnSlot(){
        if(getItemHandler().isPresent()){
            return getItemHandler().get().getStackInSlot(FUEL_INPUT_SLOT);
        }
        return null;
    }

    private boolean isFull(int forAmount){
        if(getEnergyStorage().isPresent()){
            return getEnergyStorage().get().isFullFor(forAmount);
        }
        return true;
    }

    private boolean isFull(){
        if(getEnergyStorage().isPresent()){
            return getEnergyStorage().get().isFull();
        }
        return true;
    }

    private boolean isItemBurnable(ItemStack stack) {
        return getItemBurnTime(stack) > 0;
    }

    private int getItemBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING);
    }

    public int getCurrentProgress() {
        return this.currentBurnTime;
    }

    public int getMaxProgress() {
        return this.requiredTime;
    }

    @Override
    protected void readSaveLoadTag(CompoundNBT tag) {
        super.readSaveLoadTag(tag);

        if(tag.contains("requiredTime")) this.requiredTime = tag.getInt("requiredTime");
        if(tag.contains("currentBurnTime")) this.currentBurnTime = tag.getInt("currentBurnTime");
    }

    @Override
    protected CompoundNBT createSaveLoadTag(CompoundNBT tag) {
        tag.putInt("requiredTime", this.requiredTime);
        tag.putInt("currentBurnTime", this.currentBurnTime);

        return super.createSaveLoadTag(tag);
    }
}
