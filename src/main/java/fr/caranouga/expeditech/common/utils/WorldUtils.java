package fr.caranouga.expeditech.common.utils;

import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class WorldUtils {
    public static ServerWorld getServerWorld(ResourceLocation rl, ServerWorld currentWorld){
        RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, rl);
        return currentWorld.getServer().getLevel(key);
    }

    @Nullable
    public static Direction getDirectionFrom(BlockPos posA, BlockPos posB){
        for(Direction dir : Direction.values()){
            if(posA.relative(dir).equals(posB)) return dir;
        }

        return null;
    }
}
