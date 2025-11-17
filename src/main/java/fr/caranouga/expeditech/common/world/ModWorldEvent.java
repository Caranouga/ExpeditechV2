package fr.caranouga.expeditech.common.world;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.world.gen.ModOreGeneration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = Expeditech.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ModWorldEvent {
    @SubscribeEvent
    public static void biomeLoadingEvent(BiomeLoadingEvent e){
        ModOreGeneration.generateOres(e);
    }
}
