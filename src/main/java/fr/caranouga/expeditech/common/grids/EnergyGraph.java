package fr.caranouga.expeditech.common.grids;
/*
import fr.caranouga.expeditech.Expeditech;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class EnergyGraph {
    private final Map<BlockPos, Node> nodes = new HashMap<>();
    private final Map<BlockPos, Map<BlockPos, List<Node>>> paths = new HashMap<>();

    public EnergyGraph() {
    }

    // TODO: Passer les adjency list en param
    public void setChanged(ServerWorld world, Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        clear();

        createNodes(world, generators, consumers, ducts);
        computeGraph(generators, consumers, ducts);
        computePaths();

        paths.forEach((start, map) -> {
            map.forEach((end, nodeList) -> {
                Expeditech.LOGGER.debug("{} -> {}:", start, end);
                nodeList.forEach(node -> {
                    Expeditech.LOGGER.debug("    * {}", node.getPos());
                });
            });
        });
    }

    private void createNodes(ServerWorld world, Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        generators.forEach(gen -> nodes.put(gen, new Node(world, gen)));
        consumers.forEach(cons -> nodes.put(cons, new Node(world,cons)));
        ducts.forEach(duct -> nodes.put(duct, new Node(world,duct)));
    }

    private void computeGraph(Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        nodes.forEach((pos, node) -> {
            for (Direction dir : Direction.values()) {
                BlockPos otherPos = pos.relative(dir);

                boolean isDuct = ducts.contains(otherPos);
                boolean isGen = generators.contains(otherPos);
                boolean isCons = consumers.contains(otherPos);

                Node other = nodes.get(otherPos);

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
            paths.put(pos, dijkstra(node));
        });
    }

    // Warning this Dijkstra algorithm does not minimize the "distance" (bottleneck), but maximize it
    private Map<BlockPos, List<Node>> dijkstra(Node start){
        Map<Node, Integer> dist = new HashMap<>(); // Distance from "start" to each node
        Map<Node, List<Node>> bestPaths = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> Integer.compare(dist.get(b), dist.get(a)));

        // We init the dist at 0 as we'll try to maximize it and put the start node in each path
        for (Node node : nodes.values()) {
            dist.put(node, 0);
            node.getConnections().forEach((n, distance) -> dist.put(n, 0));
            bestPaths.put(node, new ArrayList<>());
        }

        dist.put(start, Integer.MAX_VALUE);
        bestPaths.put(start, Collections.singletonList(start));
        queue.add(start);

        // Actual Dijkstra "loop"
        while(!queue.isEmpty()){
            Node currentNode = queue.poll();
            int currentBottleneck = dist.get(currentNode);

            for (Map.Entry<Node, Integer> edge : currentNode.getConnections().entrySet()) {
                Node neighbor = edge.getKey();
                int cap = edge.getValue();

                int newBottleneck = Math.min(currentBottleneck, cap);
                if(newBottleneck > dist.get(neighbor)){
                    dist.put(neighbor, newBottleneck);

                    List<Node> newPath = new ArrayList<>(bestPaths.get(currentNode));
                    newPath.add(neighbor);
                    bestPaths.put(neighbor, newPath);

                    queue.add(neighbor);
                }

            }
        }

        Map<BlockPos, List<Node>> result = new HashMap<>();
        nodes.forEach((pos, node) -> result.put(pos, bestPaths.get(node)));

        return result;
    }

    private void add(Map<BlockPos, Set<BlockPos>> map, BlockPos key, BlockPos toAdd){
        map.computeIfAbsent(key, k -> new HashSet<>()).add(toAdd);
    }

    private void put(Map<BlockPos, BlockPos> map, BlockPos key, BlockPos toAdd){
        map.putIfAbsent(key, toAdd);
    }

    private void clear(){
        nodes.clear();
        paths.clear();
    }
}
*/

import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class EnergyGraph {
    private final Map<BlockPos, Node> nodes = new HashMap<>();
    private final List<Path> paths = new ArrayList<>();
    //private final Map<BlockPos, Node> adjacencyMap = new HashMap<>();
    private final Map<Node, Map<Node, Path>> pathTable = new HashMap<>();

    public EnergyGraph() {
    }

    // TODO: Passer les adjency list en param
    public void setChanged(ServerWorld world, Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        clear();

        createNodes(world, generators, consumers, ducts);
        computeGraph(generators, consumers, ducts);
        computePaths();
        //calculateAdjacencyMap(generators, consumers);

        //paths.forEach(Path::print);
    }

    public Path getPath(BlockPos genPos, BlockPos consPos){
        /*Node startNode = adjacencyMap.get(genPos);
        Node endNode = adjacencyMap.get(consPos);*/
        Node startNode = nodes.get(genPos);
        Node endNode = nodes.get(consPos);

        return pathTable.getOrDefault(startNode, Collections.emptyMap()).get(endNode);
    }

    /*private void calculateAdjacencyMap(Set<BlockPos> generators, Set<BlockPos> consumers){
        Set<BlockPos> allPos = new HashSet<>();
        allPos.addAll(generators);
        allPos.addAll(consumers);

        allPos.forEach(pos -> {
            for (Direction dir : Direction.values()) {
                if(nodes.containsKey(pos.relative(dir))) adjacencyMap.put(pos, nodes.get(pos.relative(dir)));
            }
        });
    }*/

    private void createNodes(ServerWorld world, Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        generators.forEach(gen -> nodes.put(gen, new Node(world, gen)));
        consumers.forEach(cons -> nodes.put(cons, new Node(world,cons)));
        ducts.forEach(duct -> nodes.put(duct, new Node(world,duct)));
    }

    private void computeGraph(Set<BlockPos> generators, Set<BlockPos> consumers, Set<BlockPos> ducts){
        nodes.forEach((pos, node) -> {
            for (Direction dir : Direction.values()) {
                BlockPos otherPos = pos.relative(dir);

                boolean isDuct = ducts.contains(otherPos);
                boolean isGen = generators.contains(otherPos);
                boolean isCons = consumers.contains(otherPos);

                Node other = nodes.get(otherPos);

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
            paths.addAll(dijkstra(node));
        });
    }

    /** Warning this Dijkstra algorithm does not minimize the "distance" (bottleneck), but maximize it **/
    private List<Path> dijkstra(Node start){
        Map<Node, Integer> dist = new HashMap<>(); // Distance from "start" to each node
        Map<Node, List<Node>> bestPaths = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> Integer.compare(dist.get(b), dist.get(a)));
        List<Path> result = new ArrayList<>();

        // We init the dist at 0 as we'll try to maximize it and put the start node in each path
        for (Node node : nodes.values()) {
            dist.put(node, 0);
            node.getConnections().forEach((n, distance) -> dist.put(n, 0));
            bestPaths.put(node, new ArrayList<>());
        }

        dist.put(start, Integer.MAX_VALUE);
        bestPaths.put(start, Collections.singletonList(start));
        queue.add(start);

        // Actual Dijkstra "loop"
        while(!queue.isEmpty()){
            Node currentNode = queue.poll();
            int currentBottleneck = dist.get(currentNode);

            for (Map.Entry<Node, Integer> edge : currentNode.getConnections().entrySet()) {
                Node neighbor = edge.getKey();
                int cap = edge.getValue();

                int newBottleneck = Math.min(currentBottleneck, cap);
                if(newBottleneck > dist.get(neighbor)){
                    dist.put(neighbor, newBottleneck);

                    List<Node> newPath = new ArrayList<>(bestPaths.get(currentNode));
                    newPath.add(neighbor);
                    bestPaths.put(neighbor, newPath);

                    Path path = new Path(
                            start,
                            neighbor,
                            newPath
                    );

                    result.add(path);
                    pathTable.computeIfAbsent(start, k -> new HashMap<>()).put(neighbor, path);

                    queue.add(neighbor);
                }

            }
        }

        return result;
    }

    public void calculateCapacity(Map<Node, Integer> remainingCapacityMap){
        nodes.forEach((pos, node) -> {
            if(node.isDuct()) {
                remainingCapacityMap.put(node, ((EnergyDuctMachineTE) node.getTile()).getMaxTransferPerTick());
            }
        });
    }

    private void clear(){
        nodes.clear();
        paths.clear();
        //adjacencyMap.clear();
        pathTable.clear();
    }
}
