package fr.caranouga.expeditech.common.events;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.EnergyGrid;
import fr.caranouga.expeditech.common.grids.EnergyGridSavedData;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = Expeditech.MODID
)
public class ModEvents {
    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event) {
        if(event.side == LogicalSide.CLIENT) return;
        EnergyGridSavedData.get((ServerWorld) event.world).getGridList().forEach(EnergyGrid::tick);
        Expeditech.LOGGER.debug("");
    }
}
