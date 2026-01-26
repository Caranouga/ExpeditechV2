package fr.caranouga.expeditech.common.containers;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.capabilities.energy.CustomEnergyStorage;
import fr.caranouga.expeditech.common.tileentities.custom.machine.CoalGeneratorMachineTE;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CoalGeneratorMachineContainer extends Container {
    protected final CoalGeneratorMachineTE tileEntity;
    private final PlayerEntity player;
    private final IItemHandler playerInv;
    private final Block block;

    public CoalGeneratorMachineContainer(int pContainerId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
        super(ModContainers.COAL_GENERATOR_CONTAINER.get(), pContainerId);

        this.tileEntity = (CoalGeneratorMachineTE) world.getBlockEntity(pos);
        this.block = ModBlocks.COAL_GENERATOR.get();
        this.player = player;
        this.playerInv = new InvWrapper(playerInv);
        layoutPlayerInv(8, 86);

        if(this.tileEntity != null){
            this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                addSlot(new SlotItemHandler(handler, 0, 80, 35));
            });
        }

        trackEnergy();
    }

    private void trackEnergy(){
        addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return getEnergyStored() & 0xFFFF;
            }

            @Override
            public void set(int pValue) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                    int energyStored = handler.getEnergyStored() & 0xffff0000;
                    ((CustomEnergyStorage) handler).setEnergy(energyStored + (pValue & 0xFFFF));
                });
            }
        });

        addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return (getEnergyStored() >> 16) & 0xFFFF;
            }

            @Override
            public void set(int pValue) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                    int energyStored = handler.getEnergyStored() & 0x0000FFFF;
                    ((CustomEnergyStorage) handler).setEnergy(energyStored | (pValue << 16));
                });
            }
        });
    }

    public int getEnergyStored() {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getMaxEnergyStored() {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        return this.tileEntity.getLevel() != null &&
                stillValid(IWorldPosCallable.create(this.tileEntity.getLevel(), this.tileEntity.getBlockPos()),
                        pPlayer, this.block);
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + getNumberOfSlots(), false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + getNumberOfSlots()) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            Expeditech.LOGGER.error("Invalid slotIndex:{}", index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(this.player, sourceStack);
        return copyOfSourceStack;
    }

    private void layoutPlayerInv(int leftCol, int topRow) {
        addSlotBox(playerInv, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        addSlotRange(playerInv, 0, leftCol, topRow, 9, 18);
    }

    private int addSlotRange(IItemHandler handler, int idx, int x, int y, int amount, int dx){
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, idx, x, y));
            x += dx;
            idx++;
        }
        return idx;
    }

    private void addSlotBox(IItemHandler handler, int idx, int x, int y, int horizontalAmount, int dx, int verticalAmount, int dy){
        for (int i = 0; i < verticalAmount; i++) {
            idx = addSlotRange(handler, idx, x, y, horizontalAmount, dx);
            y += dy;
        }
    }

    protected int getNumberOfSlots(){
        return this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .orElseThrow(() -> new IllegalArgumentException("TileEntity does not have an item handler"))
                .getSlots();
    }

    public int getProgress() {
        return tileEntity.getCurrentProgress();
    }

    public int getMaxProgress() {
        return tileEntity.getMaxProgress();
    }

    public float getScaledEnergy() {
        int energy = getEnergyStored();
        int maxEnergy = getMaxEnergyStored();

        return maxEnergy == 0 ? 0 : (float) energy / (float) maxEnergy;
    }

    public float getScaledProgress() {
        int progress = getProgress();
        int maxProgress = getMaxProgress();

        return maxProgress == 0 ? 0 : (float) progress / (float) maxProgress;
    }
}
