package fr.caranouga.expeditech.common.events;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.data.EnergyGridSavedData;
import fr.caranouga.expeditech.common.grids.grid.Grid;
import fr.caranouga.expeditech.common.te.custom.duct.EnergyDuctTE;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(
        modid = Expeditech.MODID
)
public class ModEvents {
    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.CLIENT || event.phase == TickEvent.Phase.END) return;
        List<Grid<IEnergyStorage, EnergyDuctTE>> gridList = EnergyGridSavedData.get((ServerWorld) event.world).getGridList();

        gridList.forEach(Grid::tick);
    }
}
