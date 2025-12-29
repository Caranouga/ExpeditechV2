package fr.caranouga.expeditech.common.events;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.capabilities.ModCapabilities;
import fr.caranouga.expeditech.common.capabilities.tech.TechLevelProvider;
import fr.caranouga.expeditech.common.capabilities.tech.TechLevelUtils;
import fr.caranouga.expeditech.common.grids.data.EnergyGridSavedData;
import fr.caranouga.expeditech.common.grids.grid.Grid;
import fr.caranouga.expeditech.common.te.custom.duct.EnergyDuctTE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static fr.caranouga.expeditech.common.capabilities.ModCapabilities.TECH_LEVEL_ID;

@Mod.EventBusSubscriber(
        modid = Expeditech.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ModEvents {
    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.CLIENT || event.phase == TickEvent.Phase.END) return;
        List<Grid<IEnergyStorage, EnergyDuctTE>> gridList = EnergyGridSavedData.get((ServerWorld) event.world).getGridList();

        gridList.forEach(Grid::tick);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof PlayerEntity)) return;

        event.addCapability(TECH_LEVEL_ID, new TechLevelProvider());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(ModCapabilities.TECH_LEVEL).ifPresent(oldTechLevel -> {
            PlayerEntity player = event.getPlayer();
            player.getCapability(ModCapabilities.TECH_LEVEL).ifPresent(newTechLevel -> {
                newTechLevel.set(oldTechLevel);
                TechLevelUtils.update(player);
            });
        });
    }


    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Entity ent = event.getEntity();
        if(!ent.level.isClientSide()) {
            PlayerEntity player = (PlayerEntity) ent;

            TechLevelUtils.update(player);
        }
    }
}
