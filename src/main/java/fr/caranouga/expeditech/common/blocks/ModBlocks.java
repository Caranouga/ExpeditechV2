package fr.caranouga.expeditech.common.blocks;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.items.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Expeditech.MODID);

    // region Registry
    // Storage blocks
    public static final RegistryObject<Block> CARANITE_BLOCK = block("caranite_block",
            AbstractBlock.Properties.of(Material.METAL).strength(5.0f, 6.0f)
                    .harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops());
    // endregion

    // region Utils
    /**
     * This function register a new standard block with the given properties.
     * It also registers the block item
     * @param id The id of the block to be registered (modid:id)
     * @param properties The block's properties
     * @return The {@link RegistryObject} containing the block
     * @since 1.0.0
     */
    private static RegistryObject<Block> block(String id, AbstractBlock.Properties properties){
        RegistryObject<Block> blockObj = BLOCKS.register(id, () -> new Block(properties));
        ModItems.blockItem(id, blockObj);

        return blockObj;
    }
    // endregion

    public static void register(IEventBus eBus){
        BLOCKS.register(eBus);
    }
}
