package fr.caranouga.expeditech.datagen;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.datagen.providers.loottables.ModBlockLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD,
        modid = Expeditech.MODID
)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent e){
        DataGenerator generator = e.getGenerator();

        generator.addProvider(new ModBlockLootTableProvider(generator));
    }
}
