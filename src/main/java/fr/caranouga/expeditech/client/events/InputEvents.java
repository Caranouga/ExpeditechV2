package fr.caranouga.expeditech.client.events;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.client.keybinds.ModKeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Expeditech.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InputEvents {
    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;

        int keyCode = event.getKey();
        int action = event.getAction();

        onInput(mc, keyCode, action);
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.MouseInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;

        int keyCode = event.getButton();
        int action = event.getAction();

        onInput(mc, keyCode, action);
    }

    private static void onInput(Minecraft mc, int key, int action) {
        if(mc.screen != null) return;

        ModKeyBinds.handleKey(key, action);
    }
}
