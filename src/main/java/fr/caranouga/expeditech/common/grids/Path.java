package fr.caranouga.expeditech.common.grids;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;

import java.util.List;

public class Path {
    private final int bottleneck;
    private final Node start;
    private final Node end;
    private final List<Node> path;

    public Path(Node start, Node end, List<Node> path) {
        this.start = start;
        this.end = end;
        this.path = path;
        this.bottleneck = calculateBottleneck();
    }

    private int calculateBottleneck(){
        int bottleneck = Integer.MAX_VALUE;
        for (Node node : path) {
            bottleneck = Math.min(bottleneck, node.isDuct() ? ((EnergyDuctMachineTE) node.getTile()).getMaxTransferPerTick() : bottleneck);
        }

        return bottleneck;
    }

    public void print(){
        Expeditech.LOGGER.debug("({}) {} -> {}:", bottleneck, start.getPos(), end.getPos());
        path.forEach(node -> Expeditech.LOGGER.debug("    * {}", node.getPos()));
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public List<Node> getDucts() {
        return path;
    }
}
