/*package fr.caranouga.expeditech.common.grids;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.grids.Graph;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;

public class EnergyRoutingEngine {

    private final Graph graph;
    private final Map<BlockPos, TileEntity> generators;
    private final Map<BlockPos, TileEntity> consumers;
    private final Map<BlockPos, EnergyDuctMachineTE> ducts;

    // Cache of all paths for generator → consumer
    private final Map<Pair, List<PathInfo>> pathCache = new HashMap<>();

    private boolean cacheDirty = false;

    public EnergyRoutingEngine(
            Graph graph,
            Map<BlockPos, TileEntity> generators,
            Map<BlockPos, TileEntity> consumers,
            Map<BlockPos, EnergyDuctMachineTE> ducts
    ) {
        this.graph = graph;
        this.generators = generators;
        this.consumers = consumers;
        this.ducts = ducts;
    }

    // Call this when ducts are added/removed or grid is split/merged
    public void markCacheDirty() {
        cacheDirty = true;
    }

    public void tick() {
        if (generators.isEmpty() || consumers.isEmpty() || ducts.isEmpty())
            return;

        if (cacheDirty) {
            pathCache.clear();
            cacheDirty = false;
        }

        // 1) Duct throughput for this tick
        Map<BlockPos, Integer> remainingCapacity = new HashMap<>();
        int totalNetworkCapacity = 0;

        for (Map.Entry<BlockPos, EnergyDuctMachineTE> e : ducts.entrySet()) {
            int cap = e.getValue().getMaxTransferPerTick();
            remainingCapacity.put(e.getKey(), cap);
            totalNetworkCapacity += cap;
        }

        // 2) Fair generator share
        int generatorShare = Math.max(1, totalNetworkCapacity / generators.size());

        // 3) Shuffle for fairness
        List<Map.Entry<BlockPos, TileEntity>> gens = new ArrayList<>(generators.entrySet());
        List<Map.Entry<BlockPos, TileEntity>> cons = new ArrayList<>(consumers.entrySet());
        Collections.shuffle(gens);
        Collections.shuffle(cons);

        // 4) Routing
        for (Map.Entry<BlockPos, TileEntity> genEntry : gens) {

            IEnergyStorage gen = genEntry.getValue()
                    .getCapability(CapabilityEnergy.ENERGY, null)
                    .orElse(null);

            if (gen == null) continue;

            // Generator can output ANY buffer, but only up to its share
            int remainingGen = gen.extractEnergy(generatorShare, true);
            if (remainingGen <= 0) continue;

            for (Map.Entry<BlockPos, TileEntity> conEntry : cons) {

                IEnergyStorage con = conEntry.getValue()
                        .getCapability(CapabilityEnergy.ENERGY, null)
                        .orElse(null);

                if (con == null) continue;

                int remainingCon = con.receiveEnergy(Integer.MAX_VALUE, true);
                if (remainingCon <= 0) continue;

                Pair key = new Pair(genEntry.getKey(), conEntry.getKey());
                List<PathInfo> paths = pathCache.computeIfAbsent(key, k -> computePaths(k.from, k.to));
                if (paths.isEmpty()) continue;

                for (PathInfo p : paths) {
                    if (remainingGen <= 0 || remainingCon <= 0) break;

                    // Path bottleneck
                    int pathRemaining = Integer.MAX_VALUE;
                    for (BlockPos ductPos : p.path) {
                        pathRemaining = Math.min(pathRemaining, remainingCapacity.getOrDefault(ductPos, 0));
                    }
                    if (pathRemaining <= 0) continue;

                    int attempt = Math.min(pathRemaining, Math.min(remainingGen, remainingCon));
                    if (attempt <= 0) continue;

                    int canExtract = gen.extractEnergy(attempt, true);
                    int canReceive = con.receiveEnergy(canExtract, true);
                    int toSend = Math.min(canExtract, canReceive);

                    int extracted = gen.extractEnergy(toSend, false);
                    int received = con.receiveEnergy(extracted, false);
                    int transferred = Math.min(extracted, received);

                    for (BlockPos ductPos : p.path) {
                        remainingCapacity.put(ductPos,
                                remainingCapacity.get(ductPos) - transferred);
                    }

                    remainingGen -= transferred;
                    remainingCon -= transferred;
                }
            }
        }
    }




    // -------- Pathfinding + Bottleneck Computation ---------- //

    private List<PathInfo> computePaths(BlockPos genPos, BlockPos conPos) {

        List<List<BlockPos>> rawPaths = findAllPaths(genPos, conPos);
        List<PathInfo> result = new ArrayList<>();

        for (List<BlockPos> path : rawPaths) {
            int bottleneck = computeBottleneck(path);
            if (bottleneck > 0)
                result.add(new PathInfo(path, bottleneck));
        }

        // Sort fastest → slowest
        result.sort((a, b) -> Integer.compare(b.bottleneck, a.bottleneck));
        return result;
    }


    private List<List<BlockPos>> findAllPaths(BlockPos gen, BlockPos con) {
        List<List<BlockPos>> result = new ArrayList<>();

        List<BlockPos> starts = graph.getGeneratorAdj().getOrDefault(gen, Collections.emptyList());
        List<BlockPos> ends = graph.getConsumerAdj().getOrDefault(con, Collections.emptyList());

        for (BlockPos s : starts) {
            for (BlockPos e : ends) {
                List<BlockPos> path = bfs(s, e);
                if (path != null) result.add(path);
            }
        }

        return result;
    }


    private List<BlockPos> bfs(BlockPos start, BlockPos end) {

        if (start.equals(end)) {
            return Collections.singletonList(start);
        }

        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> parent = new HashMap<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {

            BlockPos current = queue.poll();

            for (BlockPos next : graph.getDuctGraph().getOrDefault(current, Collections.emptyList())) {

                if (visited.contains(next)) continue;
                visited.add(next);
                parent.put(next, current);

                if (next.equals(end)) {
                    return reconstructPath(parent, start, end);
                }

                queue.add(next);
            }
        }

        return null; // unreachable
    }


    private List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> parent, BlockPos start, BlockPos end) {

        List<BlockPos> path = new ArrayList<>();
        BlockPos cur = end;

        while (!cur.equals(start)) {
            path.add(cur);
            cur = parent.get(cur);
        }

        path.add(start);
        Collections.reverse(path);
        return path;
    }


    private int computeBottleneck(List<BlockPos> path) {

        int min = Integer.MAX_VALUE;

        for (BlockPos pos : path) {
            EnergyDuctMachineTE duct = ducts.get(pos);
            if (duct != null) {
                min = Math.min(min, duct.getMaxTransferPerTick());
            }
        }

        return min == Integer.MAX_VALUE ? 0 : min;
    }


    // ---------------- Utility Structures ---------------- //

    private static class Pair {
        final BlockPos from;
        final BlockPos to;

        Pair(BlockPos from, BlockPos to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair p = (Pair) o;
            return p.from.equals(from) && p.to.equals(to);
        }
    }

    public static class PathInfo {
        public final List<BlockPos> path;
        public final int bottleneck;

        public PathInfo(List<BlockPos> path, int bottleneck) {
            this.path = path;
            this.bottleneck = bottleneck;
        }
    }
}*/