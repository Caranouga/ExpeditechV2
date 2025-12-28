package fr.caranouga.expeditech.common.grids;

import fr.caranouga.expeditech.common.te.custom.duct.DuctTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;

public class Node<D extends DuctTE<?, D>> {
    private final Map<Node<D>, Integer> connections;

    private final int maxTransfer;
    private final boolean isDuct;
    private final TileEntity tile;

    @SuppressWarnings("unchecked")
    public Node(ServerWorld world, BlockPos pos, Class<D> ductClass) {
        this.connections = new HashMap<>();

        TileEntity te = world.getBlockEntity(pos);
        tile = te;
        isDuct = ductClass.isInstance(te);

        maxTransfer = isDuct ? ((D) te).getMaxTransferPerTick() : Integer.MAX_VALUE;
    }

    public boolean isDuct(){
        return this.isDuct;
    }

    public void addDuct(Node<D> other) {
        connections.put(other, Math.min(maxTransfer, other.maxTransfer));
    }

    public void addGenerator(Node<D> other) {
        other.connections.put(this, maxTransfer);
    }

    public void addConsumer(Node<D> other) {
        connections.put(other, maxTransfer);
    }

    public Map<Node<D>, Integer> getConnections(){
        return this.connections;
    }

    public TileEntity getTile(){
        return tile;
    }
}
