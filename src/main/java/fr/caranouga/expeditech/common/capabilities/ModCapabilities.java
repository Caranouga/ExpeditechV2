package fr.caranouga.expeditech.common.capabilities;

import fr.caranouga.expeditech.common.capabilities.tech.ITechLevel;
import fr.caranouga.expeditech.common.capabilities.tech.TechLevelImplementation;
import fr.caranouga.expeditech.common.capabilities.tech.TechLevelStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class ModCapabilities {
    @CapabilityInject(ITechLevel.class)
    public static Capability<ITechLevel> TECH_LEVEL = null;
    public static final ResourceLocation TECH_LEVEL_ID = modLocation("tech_level");

    public static void register(){
        CapabilityManager.INSTANCE.register(ITechLevel.class, new TechLevelStorage(), TechLevelImplementation::new);
    }
}