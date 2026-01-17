package fr.caranouga.expeditech.common.packets;

import fr.caranouga.expeditech.client.ClientState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MultiblockErrorPacket {
    private final BlockPos pos;
    private final int color;
    private final ITextComponent message;
    private final long lifetime;

    public MultiblockErrorPacket(BlockPos pos, int color, ITextComponent message, long lifetime) {
        this.pos = pos;
        this.color = color;
        this.message = message;
        this.lifetime = lifetime;
    }

    public void encode(PacketBuffer buffer){
        buffer.writeBlockPos(pos);
        buffer.writeInt(color);
        buffer.writeComponent(message);
        buffer.writeLong(lifetime);
    }

    public static MultiblockErrorPacket decode(PacketBuffer buffer){
        BlockPos pos = buffer.readBlockPos();
        int color = buffer.readInt();
        ITextComponent message = buffer.readComponent();
        long lifetime = buffer.readLong();
        return new MultiblockErrorPacket(pos, color, message, lifetime);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ClientState.getMultiblockErrorRenderer().addMarker(this.pos, this.color, this.message, this.lifetime);
        });

        context.setPacketHandled(true);
    }
}
