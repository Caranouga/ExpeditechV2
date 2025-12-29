package fr.caranouga.expeditech.common.capabilities.tech;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.capabilities.ModCapabilities;
import fr.caranouga.expeditech.common.packets.TechLevelSyncPacket;
import fr.caranouga.expeditech.common.triggers.AdvancementTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class TechLevelUtils {
    public static int getTechLevel(Entity entity){
        return entity.getCapability(ModCapabilities.TECH_LEVEL).map(ITechLevel::getTechLevel).orElse(0);
    }

    public static int getTechXp(Entity entity){
        return entity.getCapability(ModCapabilities.TECH_LEVEL).map(ITechLevel::getTechXp).orElse(0);
    }

    public static void addTechXp(Entity entity, int amount){
        entity.getCapability(ModCapabilities.TECH_LEVEL).ifPresent(techLevel -> {
            techLevel.addTechXp(amount);

            update((PlayerEntity) entity, techLevel);
        });
    }

    public static void addTechLevel(Entity entity, int amount){
        entity.getCapability(ModCapabilities.TECH_LEVEL).ifPresent(techLevel -> {
            techLevel.addTechLevel(amount);

            update((PlayerEntity) entity, techLevel);
        });
    }

    public static void setTechXp(Entity entity, int amount){
        entity.getCapability(ModCapabilities.TECH_LEVEL).ifPresent(techLevel -> {
            techLevel.setTechXp(amount);

            update((PlayerEntity) entity, techLevel);
        });
    }

    public static void setTechLevel(Entity entity, int amount){
        entity.getCapability(ModCapabilities.TECH_LEVEL).ifPresent(techLevel -> {
            techLevel.setTechLevel(amount);

            update((PlayerEntity) entity, techLevel);
        });
    }

    public static void update(PlayerEntity entity){
        entity.getCapability(ModCapabilities.TECH_LEVEL).ifPresent(techLevel -> {
            update(entity, techLevel);
        });
    }

    private static void update(PlayerEntity player, ITechLevel techLevel) {
        AdvancementTriggers.TECH_LEVEL_TRIGGER.trigger((ServerPlayerEntity) player);
        Expeditech.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new TechLevelSyncPacket(techLevel.getTechLevel(), techLevel.getTechXp(), player.getId()));
    }
}
