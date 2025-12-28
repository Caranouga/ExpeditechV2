package fr.caranouga.expeditech.common.utils;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import javax.annotation.Nonnull;

public class VoxelUtils {
    /**
     * This function combines two (ore more) {@link VoxelShape}
     * @param shapes The shapes to combine
     * @return The combined shape
     */
    @Nonnull
    public static VoxelShape combine(@Nonnull VoxelShape... shapes) {
        if (shapes.length == 0) {
            return VoxelShapes.empty();
        }
        VoxelShape combined = shapes[0];

        for (int i = 1; i < shapes.length; i++) {
            combined = VoxelShapes.or(combined, shapes[i]);
        }

        return combined;
    }
}
