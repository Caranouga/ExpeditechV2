package fr.caranouga.expeditech.common.items;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.tab.ModTabs;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Expeditech.MODID);

    // region Registry
    public static final RegistryObject<Item> CARANITE = item("caranite", new Item.Properties().tab(ModTabs.EXPEDITECH));
    // endregion

    // region Utils

    /**
     * This function register an item with the given properties.
     * @param id The id of the item to register (modid:id)
     * @param properties The properties that the item should have
     * @return A {@link RegistryObject} containing the item
     * @see ModItems#item(String, Supplier)
     * @since 1.0.0
     */
    private static RegistryObject<Item> item(String id, Item.Properties properties){
        return item(id, () -> new Item(properties));
    }

    /**
     * This function register an item using the give supplier
     * @param id The id of the item to register (modid:id)
     * @param itemSupplier The item supplier
     * @return A {@link RegistryObject} containing the item
     * @param <I> The item's class type
     * @since 1.0.0
     */
    private static <I extends Item> RegistryObject<I> item(String id, Supplier<I> itemSupplier){
        return ITEMS.register(id, itemSupplier);
    }

    /**
     * This function registers the item for a given block.
     * @param id The id of the item (modid:id)
     * @param blockObj The {@link RegistryObject} containing the block
     * @param <B> The block's class type
     * @since 1.0.0
     */
    public static <B extends Block> void blockItem(String id, RegistryObject<B> blockObj) {
        // TODO: Add tab
        item(id, () -> new BlockItem(blockObj.get(), new Item.Properties()));
    }
    // endregion

    public static void register(IEventBus eBus){
        ITEMS.register(eBus);
    }
}
