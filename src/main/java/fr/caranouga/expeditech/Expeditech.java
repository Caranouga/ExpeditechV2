package fr.caranouga.expeditech;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import fr.caranouga.expeditech.common.items.ModItems;
import fr.caranouga.expeditech.common.items.custom.DuctItem;
import fr.caranouga.expeditech.common.recipes.ModRecipes;
import fr.caranouga.expeditech.common.te.ModTileEntities;
import net.minecraft.item.ItemModelsProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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

        modEBus.addListener(this::doClientStuff);

        ModBlocks.register(modEBus);
        ModItems.register(modEBus);
        ModRecipes.register(modEBus);
        ModTileEntities.register(modEBus);

        MinecraftForge.EVENT_BUS.register(this);
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
        });
    }
}
