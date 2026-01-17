package fr.caranouga.expeditech.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;

public class ColorUtils {
    @SuppressWarnings("deprecation")
    public static void setColor(float r, float g, float b, float a){
        RenderSystem.color4f(r,g,b,a);
    }
}
