package fr.caranouga.expeditech.common.utils;

import fr.caranouga.expeditech.Expeditech;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class StringUtils {
    /**
     * This function generates a ressource location (in this format "modid:path")
     * @param path The RL path (without the modid)
     * @return The generated RL
     */
    @Nonnull
    public static ResourceLocation modLocation(@Nonnull String path) {
        return new ResourceLocation(Expeditech.MODID, path);
    }
}
