package fr.caranouga.expeditech.common.grids;


import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Map;

public class Node {
    private final Map<Node, Integer> connections;

    private final int maxTransfer;
    private final boolean isDuct;

    private final BlockPos pos;
    private final TileEntity tile;

    public Node(ServerWorld world, BlockPos pos) {
        this.connections = new HashMap<>();
        this.pos = pos;

        TileEntity te = world.getBlockEntity(pos);
        tile = te;
        isDuct = te instanceof EnergyDuctMachineTE;

        maxTransfer = isDuct ? ((EnergyDuctMachineTE) te).getMaxTransferPerTick() : Integer.MAX_VALUE;
    }

    public boolean isDuct(){
        return this.isDuct;
    }

    public void addDuct(Node other) {
        connections.put(other, Math.min(maxTransfer, other.maxTransfer));
    }

    public void addGen(Node other) {
        other.connections.put(this, maxTransfer);
    }

    public void addCons(Node other) {
        connections.put(other, maxTransfer);
    }

    public BlockPos getPos(){
        return this.pos;
    }

    public Map<Node, Integer> getConnections(){
        return this.connections;
    }

    public TileEntity getTile(){
        return tile;
    }
}
