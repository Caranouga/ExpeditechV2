package fr.caranouga.expeditech.common.utils;

import fr.caranouga.expeditech.Expeditech;
import net.minecraft.util.ResourceLocation;

public class StringUtils {
    public static ResourceLocation modLocation(String path) {
        return new ResourceLocation(Expeditech.MODID, path);
    }
}
