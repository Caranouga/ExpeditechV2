/*package fr.caranouga.expeditech.common.grids;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class Graph {

    private final Map<BlockPos, List<BlockPos>> ductAdj = new HashMap<>();
    private final Map<BlockPos, List<BlockPos>> genAdj = new HashMap<>();
    private final Map<BlockPos, List<BlockPos>> consAdj = new HashMap<>();

    public Graph(ServerWorld world,
                 Set<BlockPos> ducts,
                 Set<BlockPos> generators,
                 Set<BlockPos> consumers) {

        for (BlockPos duct : ducts) {

            for (Direction dir : Direction.values()) {

                BlockPos other = duct.relative(dir);


                boolean isGen = generators.contains(other);
                boolean isCons = consumers.contains(other);
                boolean isDuct = world.getBlockEntity(other) instanceof EnergyDuctMachineTE;

                // If generator or consumer is adjacent → special edges
                if (isGen) {
                    add(genAdj, other, duct);
                }
                if (isCons) {
                    add(consAdj, other, duct);
                }

                // Duct-to-duct edges
                if (isDuct) {
                    add(ductAdj, duct, other);
                    add(ductAdj, other, duct); // bidirectional
                }
            }
        }
    }

    // ------------ Adjacency Utilities ------------ //

    private void add(Map<BlockPos, List<BlockPos>> map, BlockPos from, BlockPos to) {
        map.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }

    // ------------ Accessors ------------ //

    public Map<BlockPos, List<BlockPos>> getDuctGraph() {
        return ductAdj;
    }

    public Map<BlockPos, List<BlockPos>> getGeneratorAdj() {
        return genAdj;
    }

    public Map<BlockPos, List<BlockPos>> getConsumerAdj() {
        return consAdj;
    }
}
*/