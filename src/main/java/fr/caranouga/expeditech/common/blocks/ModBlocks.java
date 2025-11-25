package fr.caranouga.expeditech.common.blocks;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.custom.ConsumerMachine;
import fr.caranouga.expeditech.common.blocks.custom.EnergyDuctMachine;
import fr.caranouga.expeditech.common.blocks.custom.GeneratorMachine;
import fr.caranouga.expeditech.common.items.ModItems;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Expeditech.MODID);

    // region Registry
    // Storage blocks
    public static final RegistryObject<Block> CARANITE_BLOCK = block("caranite_block",
            AbstractBlock.Properties.of(Material.METAL).strength(5.0f, 6.0f)
                    .harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops());

    // Ores
    public static final RegistryObject<OreBlock> CARANITE_ORE = ore("caranite_ore",
            AbstractBlock.Properties.of(Material.STONE).strength(3.0f, 3.0f)
                    .harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops());

    // Machines
    public static final RegistryObject<GeneratorMachine> GENERATOR = block("generator", GeneratorMachine::new);
    public static final RegistryObject<ConsumerMachine> CONSUMER = block("consumer", ConsumerMachine::new);
    public static final RegistryObject<EnergyDuctMachine> DUCT = block("duct", EnergyDuctMachine::new);
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
        return block(id, () -> new Block(properties));
    }

    private static <B extends Block> RegistryObject<B> block(String id, Supplier<B> supplier){
        RegistryObject<B> blockObj = BLOCKS.register(id, supplier);
        ModItems.blockItem(id, blockObj);

        return blockObj;
    }

    /**
     * This function register a new ore block with the given properties.
     * It also registers the block item
     * @param id The id of the block to be registered (modid:id)
     * @param properties The block's properties
     * @return The {@link RegistryObject} containing the {@link OreBlock}
     * @since 1.0.0
     */
    private static RegistryObject<OreBlock> ore(String id, AbstractBlock.Properties properties){
        return block(id, () -> new OreBlock(properties));
    }
    // endregion

    public static void register(IEventBus eBus){
        BLOCKS.register(eBus);
    }
}
