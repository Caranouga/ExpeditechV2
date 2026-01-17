package fr.caranouga.expeditech.common.utils;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldUtils {
    /**
     * This function get the direction at which the two blocks are connected
     * @param posA The first pos
     * @param posB The second pos
     * @return The direction
     */
    @Nullable
    public static Direction getDirectionFrom(@Nonnull BlockPos posA, @Nonnull BlockPos posB){
        for(Direction dir : Direction.values()){
            if(posA.relative(dir).equals(posB)) return dir;
        }

        return null;
    }
}
