package fr.caranouga.expeditech.common.multiblocks;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.tileentities.custom.multiblock.AbstractMultiBlockSlaveTile;
import fr.caranouga.expeditech.common.utils.DirectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class MultiBlockShape {
    private final BlockState[][][] layers;
    private final BlockPos masterRelative;

    public MultiBlockShape(BlockPos masterRelative, BlockState[][]... layers) {
        this.masterRelative = masterRelative;
        this.layers = layers;
    }

    /**
     * Tests if the multiblock structure matches the expected block states in the world.
     *
     * @param direction  The direction in which the multiblock is oriented.
     * @param world      The world in which to check the block states.
     * @param masterPos  The position of the master block of the multiblock structure.
     * @return           Returns a Map of BlockPos where mismatches were found.
     */
    public Map<BlockPos, ITextComponent> test(Direction direction, World world, BlockPos masterPos) {
        Map<BlockPos, ITextComponent> mismatches = new HashMap<>();

        BlockPos startPos = offset(masterPos, direction, masterRelative);

        for (int y = 0; y < layers.length; y++) {
            for (int z = 0; z < layers[y].length; z++) {
                for (int x = 0; x < layers[y][z].length; x++) {
                    BlockPos pos = offset(startPos, direction, x, y, z);
                    BlockState expectedState = layers[y][z][x];
                    BlockState actualState = world.getBlockState(pos);

                    if (!actualState.is(expectedState.getBlock())) {
                        TranslationTextComponent message = new TranslationTextComponent("mb." + Expeditech.MODID + ".error.at", expectedState.getBlock().getName(), actualState.getBlock().getName());
                        mismatches.put(pos, message);
                    }
                }
            }
        }

        return mismatches;
    }

    public void prepareMultiBlock(Direction direction, World world, BlockPos masterPos, List<BlockPos> savedPos) {
        BlockPos startPos = offset(masterPos, direction, masterRelative);

        for (int y = 0; y < layers.length; y++) {
            for (int z = 0; z < layers[y].length; z++) {
                for (int x = 0; x < layers[y][z].length; x++) {
                    BlockPos pos = offset(startPos, direction, x, y, z);
                    if(pos.equals(masterPos)) continue;

                    savedPos.add(pos);

                    TileEntity tile = world.getBlockEntity(pos);
                    if (tile instanceof AbstractMultiBlockSlaveTile) {
                        ((AbstractMultiBlockSlaveTile) tile).setMaster(masterPos, world);
                    } else {
                        Expeditech.LOGGER.error("Failed to set AbstractMultiBlockSlaveTile at {}, expected a AbstractMultiBlockSlaveTile but found: {}", pos, world.getBlockEntity(pos));
                    }
                }
            }
        }
    }

    public boolean isMasterAtGoodPos(BlockPos testPos){
        for(Direction dir : DirectionUtils.CARDINAL){
            BlockPos offsetPos = offset(testPos, dir, masterRelative);
            if(offsetPos.equals(testPos)) return true;
        }

        return false;
    }

    private BlockPos offset(BlockPos pos, Direction direction, int xOffset, int yOffset, int zOffset) {
        switch (direction){
            case NORTH: {
                return pos.offset(xOffset, yOffset, zOffset);
            }
            case SOUTH: {
                return pos.offset(-xOffset, yOffset, -zOffset);
            }
            case WEST: {
                return pos.offset(zOffset, yOffset, -xOffset);
            }
            case EAST: {
                return pos.offset(-zOffset, yOffset, xOffset);
            }
            default: {
                Expeditech.LOGGER.error("Invalid direction for offset: {}", direction);
                return pos; // Return the original position if the direction is invalid
            }
        }
    }

    private BlockPos offset(BlockPos pos, Direction direction, BlockPos offset) {
        return offset(pos, direction, offset.getX(), offset.getY(), offset.getZ());
    }
}
