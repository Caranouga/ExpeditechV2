package fr.caranouga.expeditech;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Expeditech.MODID)
public class Expeditech
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "et";

    public Expeditech() {


        MinecraftForge.EVENT_BUS.register(this);
    }
}
