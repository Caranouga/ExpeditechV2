package fr.caranouga.expeditech.common.grids;

import fr.caranouga.expeditech.common.te.custom.duct.DuctTE;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class Graph<D extends DuctTE<?, D>> {
    private final Map<BlockPos, Node<D>> nodes = new HashMap<>();
    private final Map<Node<D>, Map<Node<D>, Path<D>>> pathTable = new HashMap<>();
    private final Class<D> ductClass;

    public Graph(Class<D> ductClass) {
        this.ductClass = ductClass;
    }

    // TODO: Passer les adjency list en param
    public void setChanged(ServerWorld world, Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        clear();

        createNodes(world, generators, consumers, ducts);
        computeGraph(generators, consumers, ducts);
        computePaths();
    }

    public Path<D> getPath(BlockPos genPos, BlockPos consPos){
        Node<D> startNode = nodes.get(genPos);
        Node<D> endNode = nodes.get(consPos);

        return pathTable.getOrDefault(startNode, Collections.emptyMap()).get(endNode);
    }

    private void createNodes(ServerWorld world, Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        generators.forEach(gen -> nodes.put(gen, new Node<>(world, gen, ductClass)));
        consumers.forEach(cons -> nodes.put(cons, new Node<>(world,cons, ductClass)));
        ducts.forEach(duct -> nodes.put(duct, new Node<>(world,duct, ductClass)));
    }

    private void computeGraph(Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        nodes.forEach((pos, node) -> {
            for (Direction dir : Direction.values()) {
                BlockPos otherPos = pos.relative(dir);

                boolean isDuct = ducts.contains(otherPos);
                boolean isGen = generators.contains(otherPos);
                boolean isCons = consumers.contains(otherPos);

                Node<D> other = nodes.get(otherPos);

                /*
                Possible cases:
                |               | This is duct | This is cons | This is gen  |
                | Other is duct | Connect both | Skip*        | Skip*        |
                | Other is cons | Connect d->c | Do nothing   | Do nothing   |
                | Other is gen  | Connect g->d | Do nothing   | Do nothing   |
                * duct will handle the connection
                 */

                if(!node.isDuct()) return;

                if(isDuct){
                    node.addDuct(other);
                } else if (isGen) {
                    node.addGen(other);
                }else if (isCons){
                    node.addCons(other);
                }
            }
        });
    }

    private void computePaths(){
        nodes.forEach((pos, node) -> {
            dijkstra(node);
        });
    }

    /** Warning this Dijkstra algorithm does not minimize the "distance" (bottleneck), but maximize it **/
    private void dijkstra(Node<D> start){
        Map<Node<D>, Integer> dist = new HashMap<>(); // Distance from "start" to each node
        Map<Node<D>, List<Node<D>>> bestPaths = new HashMap<>();
        PriorityQueue<Node<D>> queue = new PriorityQueue<>((a, b) -> Integer.compare(dist.get(b), dist.get(a)));

        // We init the dist at 0 as we'll try to maximize it and put the start node in each path
        for (Node<D> node : nodes.values()) {
            dist.put(node, 0);
            node.getConnections().forEach((n, distance) -> dist.put(n, 0));
            bestPaths.put(node, new ArrayList<>());
        }

        dist.put(start, Integer.MAX_VALUE);
        bestPaths.put(start, Collections.singletonList(start));
        queue.add(start);

        // Actual Dijkstra "loop"
        while(!queue.isEmpty()){
            Node<D> currentNode = queue.poll();
            int currentBottleneck = dist.get(currentNode);

            for (Map.Entry<Node<D>, Integer> edge : currentNode.getConnections().entrySet()) {
                Node<D> neighbor = edge.getKey();
                int cap = edge.getValue();

                int newBottleneck = Math.min(currentBottleneck, cap);
                if(newBottleneck > dist.get(neighbor)){
                    dist.put(neighbor, newBottleneck);

                    List<Node<D>> newPath = new ArrayList<>(bestPaths.get(currentNode));
                    newPath.add(neighbor);
                    bestPaths.put(neighbor, newPath);

                    Path<D> path = new Path<>(newPath);

                    pathTable.computeIfAbsent(start, k -> new HashMap<>()).put(neighbor, path);

                    queue.add(neighbor);
                }

            }
        }
    }

    @SuppressWarnings("unchecked")
    public void calculateCapacity(Map<Node<D>, Integer> remainingCapacityMap){
        nodes.forEach((pos, node) -> {
            if(node.isDuct()) {
                remainingCapacityMap.put(node, ((D) node.getTile()).getMaxTransferPerTick());
            }
        });
    }

    private void clear(){
        nodes.clear();
        pathTable.clear();
    }
}
