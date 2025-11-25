package fr.caranouga.expeditech.common.events;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.EnergyGrid;
import fr.caranouga.expeditech.common.grids.ModGrids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = Expeditech.MODID
)
public class ModEvents {
    @SubscribeEvent
    public static void onServerTick(TickEvent event) {
        Expeditech.LOGGER.debug("srv tick");
        ModGrids.GRIDS.forEach(EnergyGrid::tick);
    }

}
