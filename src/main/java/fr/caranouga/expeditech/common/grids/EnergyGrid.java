package fr.caranouga.expeditech.common.grids;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnergyGrid {
    private ArrayList<TileEntity> consumers = new ArrayList<>();
    private ArrayList<TileEntity> generators = new ArrayList<>();
    private ArrayList<EnergyDuctMachineTE> ducts = new ArrayList<>();
    private final UUID gridId = UUID.randomUUID();

    public void tick(){
        Expeditech.LOGGER.debug("Ticking grid {}", gridId);
    }

    public EnergyGrid join(EnergyDuctMachineTE duct){
        Expeditech.LOGGER.debug("Duct at {} joined grid {}", duct.getBlockPos(), gridId);

        ducts.add(duct);

        return this;
    }

    public void add(List<EnergyGrid> grids){
        for (EnergyGrid grid : grids) {
            Expeditech.LOGGER.debug("Fused grid {} to {}", grid.gridId, gridId);

            consumers.addAll(grid.consumers);
            generators.addAll(grid.generators);
            ducts.addAll(grid.ducts);

            ModGrids.GRIDS.remove(grid);
        }
    }

    public void remove(EnergyDuctMachineTE duct) {
        ducts.remove(duct);

        if(ducts.isEmpty()) ModGrids.GRIDS.remove(this);
    }
}
