package fr.caranouga.expeditech.common.tileentities.custom.multiblock;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.custom.multiblock.AbstractMultiBlockMaster;
import fr.caranouga.expeditech.common.grids.grid.Grid;
import fr.caranouga.expeditech.common.multiblocks.MultiBlockShape;
import fr.caranouga.expeditech.common.packets.MultiblockErrorPacket;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMultiBlockMasterTile extends TileEntity implements ITickableTileEntity {
    // TODO: Use the machine TE ?
    private boolean isFormed = false;
    private final MultiBlockShape shape;
    private final List<BlockPos> savedPos = new ArrayList<>();

    private static final Direction[] DIRECTIONS = new Direction[]{
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
    };

    public AbstractMultiBlockMasterTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        this.shape = getShape();
    }

    @Override
    public void tick() {
        if(level == null || level.isClientSide) return;

        if(isFormed) formedTick();
        else unformedTick();
    }

    public boolean tryBuild(Direction firstDirection) {
        if(isFormed) return false;

        Map<Direction, Map<BlockPos, ITextComponent>> mismatchesMap = new HashMap<>();
        Direction goodDirection = null;

        for(Direction dir : DIRECTIONS) {
            Map<BlockPos, ITextComponent> mismatches = this.shape.test(dir, level, getBlockPos());
            if(mismatches.isEmpty()) {
                goodDirection = dir;
                break; // No mismatches, we can build
            } else {
                mismatchesMap.put(dir, mismatches);
            }
        }

        if(goodDirection != null) {
            this.shape.prepareMultiBlock(goodDirection, level, getBlockPos(), savedPos);
            build();

            return true;
        } else {
            Map<BlockPos, ITextComponent> goodMasterMismatch = getGoodMasterMismatch(mismatchesMap, firstDirection);
            Map<BlockPos, ITextComponent> mismatches = null;
            if(goodMasterMismatch != null){
                mismatches = goodMasterMismatch;
            }else{
                Map<BlockPos, ITextComponent> minMismatch = getMinMismatch(mismatchesMap);
                if(minMismatch == null) {
                    Expeditech.LOGGER.warn("No mismatches found, but multiBlock structure cannot be built at {}", getBlockPos());
                    return false;
                }
                mismatches = minMismatch;
            }

            for(Map.Entry<BlockPos, ITextComponent> entry : mismatches.entrySet()) {
                BlockPos pos = entry.getKey();
                ITextComponent message = entry.getValue();

                // Send packet to client to display the error
                Expeditech.NETWORK.send(PacketDistributor.ALL.noArg(), new MultiblockErrorPacket(pos, 0xCCFF0000, message, 5000));
            }
        }

        return false;
    }

    private Map<BlockPos, ITextComponent> getMinMismatch(Map<Direction, Map<BlockPos, ITextComponent>> mismatchesMap) {
        List<Map<BlockPos, ITextComponent>> mismatchesList = new ArrayList<>(mismatchesMap.values());
        return getMinMismatch(mismatchesList).get(0);
    }

    private List<Map<BlockPos, ITextComponent>> getMinMismatch(List<Map<BlockPos, ITextComponent>> mismatchesList) {
        // Find the minimum number of mismatches
        List<Map<BlockPos, ITextComponent>> minMismatches = new ArrayList<>();

        int minCount = Integer.MAX_VALUE;
        for(Map<BlockPos, ITextComponent> mismatches : mismatchesList) {
            if(mismatches.size() < minCount) {
                minCount = mismatches.size();
            }
        }

        // Collect all mismatches with the minimum count
        for(Map<BlockPos, ITextComponent> entry : mismatchesList) {
            if(entry.size() == minCount) {
                minMismatches.add(entry);
            }
        }

        return minMismatches;
    }

    private Map<BlockPos, ITextComponent> getGoodMasterMismatch(Map<Direction, Map<BlockPos, ITextComponent>> mismatchesMap, Direction firstDirection) {
        ArrayList<Map<BlockPos, ITextComponent>> mismatchesList = new ArrayList<>();
        ArrayList<Direction> goodDirections = new ArrayList<>();

        for(Map<BlockPos, ITextComponent> mismatches : mismatchesMap.values()) {
            if(this.shape.isMasterAtGoodPos(mismatches.keySet().iterator().next())) {
                mismatchesList.add(mismatches);
                goodDirections.add(mismatchesMap.entrySet().stream()
                        .filter(entry -> entry.getValue() == mismatches)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null));
            }
        }

        List<Map<BlockPos, ITextComponent>> minMismatch = getMinMismatch(mismatchesList);
        for(Map<BlockPos, ITextComponent> entry : minMismatch) {
            if(goodDirections.get(mismatchesList.indexOf(entry)) == firstDirection) {
                return entry;
            }
        }

        return null;
    }

    private void build(){
        this.isFormed = true;

        this.level.playSound(null, getBlockPos(), SoundEvents.ANVIL_LAND, SoundCategory.BLOCKS, 1.0F, 1.0F);

        BlockState state = getBlockState();
        level.setBlockAndUpdate(getBlockPos(), state.setValue(AbstractMultiBlockMaster.BUILT, true));
        setChanged();
    }

    public void slaveBroken() {
        // Handle the case when a slave tile is broken
        unform();
    }

    public boolean unform() {
        if(!isFormed || this.level == null) return false;

        // Logic to unform the multiblock structure
        this.isFormed = false;

        this.level.playSound(null, getBlockPos(), SoundEvents.ANVIL_LAND, SoundCategory.BLOCKS, 1.0F, 1.0F);

        unBuildSlaves();

        BlockState state = getBlockState();
        level.setBlockAndUpdate(getBlockPos(), state.setValue(AbstractMultiBlockMaster.BUILT, false));
        setChanged();


        return true;
    }

    private void unBuildSlaves(){
        savedPos.forEach((pos) -> {
            TileEntity te = this.level.getBlockEntity(pos);
            if(te instanceof AbstractMultiBlockSlaveTile){
                ((AbstractMultiBlockSlaveTile) te).masterBroken();
            }
        });
    }

    public boolean isBuilt() {
        return this.isFormed;
    }

    @Override
    public void setRemoved() {
        unform();
        super.setRemoved();
    }

    @Nonnull
    protected abstract MultiBlockShape getShape();
    protected abstract void formedTick();
    protected abstract void unformedTick();

    // region Data Saving (World load/save)
    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);

        if(compound.contains("savedPos")) {
            ListNBT list = compound.getList("savedPos", 10);

            for(INBT base : list){
                savedPos.add(NBTUtil.readBlockPos((CompoundNBT) base));
            }
        }
        if(compound.contains("isFormed")) {
            isFormed = compound.getBoolean("isFormed");
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        CompoundNBT nbt = super.save(pCompound);

        ListNBT list = new ListNBT();

        savedPos.forEach((pos) -> {
            list.add(NBTUtil.writeBlockPos(pos));
        });

        nbt.put("savedPos", list);
        nbt.putBoolean("isFormed", isFormed);

        return nbt;
    }
    // endregion
}
