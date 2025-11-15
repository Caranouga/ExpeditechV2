package fr.caranouga.expeditech;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.items.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Expeditech.MODID)
public class Expeditech
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "et";

    public Expeditech() {
        IEventBus modEBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEBus);
        ModItems.register(modEBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
