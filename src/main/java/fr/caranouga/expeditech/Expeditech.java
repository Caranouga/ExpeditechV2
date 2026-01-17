package fr.caranouga.expeditech;

import fr.caranouga.expeditech.client.keybinds.ModKeyBinds;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import fr.caranouga.expeditech.common.capabilities.ModCapabilities;
import fr.caranouga.expeditech.common.items.custom.ModItems;
import fr.caranouga.expeditech.common.items.custom.DuctItem;
import fr.caranouga.expeditech.common.packets.ModPackets;
import fr.caranouga.expeditech.common.recipes.ModRecipes;
import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import fr.caranouga.expeditech.common.triggers.AdvancementTriggers;
import net.minecraft.item.ItemModelsProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

@Mod(Expeditech.MODID)
public class Expeditech
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "et";

    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            modLocation("channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public Expeditech() {
        IEventBus modEBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEBus.addListener(this::doClientStuff);
        modEBus.addListener(this::setup);

        ModBlocks.register(modEBus);
        ModItems.register(modEBus);
        ModRecipes.register(modEBus);
        ModTileEntities.register(modEBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event){
        ModCapabilities.register();
        ModPackets.register();
        AdvancementTriggers.registerTriggers();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemModelsProperties.register(
                    ModItems.ENERGY_DUCT.get(),
                    DuctTier.TIER_PREDICATE,
                    (stack, level, entity) -> {
                        if(!stack.hasTag()) return 0f;

                        DuctTier tier = DuctTier.byName(stack.getTag().getString(DuctItem.TIER_TAG));
                        return tier == null ? 0f : tier.getId();
                    }
            );

            ModKeyBinds.register();
        });
    }
}
