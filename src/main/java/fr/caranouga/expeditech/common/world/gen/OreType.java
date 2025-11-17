package fr.caranouga.expeditech.common.world.gen;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import net.minecraft.block.OreBlock;
import net.minecraftforge.common.util.Lazy;

public enum OreType {
    CARANITE(Lazy.of(ModBlocks.CARANITE_ORE), 8, 0, 32, 4)
    ;

    private final Lazy<OreBlock> block;
    private final int maxVeinSize;
    private final int minHeight;
    private final int maxHeight;
    private final int veinsPerChunk;

    OreType(Lazy<OreBlock> block, int maxVeinSize, int minHeight, int maxHeight, int veinsPerChunk) {
        this.block = block;
        this.maxVeinSize = maxVeinSize;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.veinsPerChunk = veinsPerChunk;
    }

    public Lazy<OreBlock> getBlock() {
        return block;
    }

    public int getMaxVeinSize() {
        return maxVeinSize;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getVeinsPerChunk() {
        return veinsPerChunk;
    }
}
