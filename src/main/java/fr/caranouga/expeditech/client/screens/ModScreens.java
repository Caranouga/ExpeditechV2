package fr.caranouga.expeditech.client.screens;

import fr.caranouga.expeditech.common.containers.ModContainers;
import net.minecraft.client.gui.ScreenManager;

public class ModScreens {
    public static void register() {
        ScreenManager.register(ModContainers.COAL_GENERATOR_CONTAINER.get(), CoalGeneratorMachineScreen::new);
    }
}
