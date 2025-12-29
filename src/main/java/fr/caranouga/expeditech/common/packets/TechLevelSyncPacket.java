package fr.caranouga.expeditech.common.packets;

import fr.caranouga.expeditech.common.capabilities.ModCapabilities;
import fr.caranouga.expeditech.common.capabilities.tech.ITechLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TechLevelSyncPacket {
    private final int playerTechLevel;
    private final int playerTechXp;
    private final int playerId;

    public TechLevelSyncPacket(int playerTechLevel, int playerTechXp, int playerId) {
        this.playerTechLevel = playerTechLevel;
        this.playerTechXp = playerTechXp;
        this.playerId = playerId;
    }

    public void encode(PacketBuffer buffer){
        buffer.writeInt(playerTechLevel);
        buffer.writeInt(playerTechXp);
        buffer.writeInt(playerId);
    }

    public static TechLevelSyncPacket decode(PacketBuffer buffer) {
        return new TechLevelSyncPacket(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            int playerId = this.playerId;
            World world = Minecraft.getInstance().level;
            if(world == null) return;

            Entity entity = world.getEntity(playerId);
            if(entity instanceof PlayerEntity){
                ITechLevel techLevel = entity.getCapability(ModCapabilities.TECH_LEVEL).orElse(null);
                if(techLevel != null) {
                    techLevel.setTechLevel(this.playerTechLevel);
                    techLevel.setTechXp(this.playerTechXp);
                }
            }
        });

        context.setPacketHandled(true);
    }
}
