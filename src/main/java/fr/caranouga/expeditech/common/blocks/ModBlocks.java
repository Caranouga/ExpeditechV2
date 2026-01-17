package fr.caranouga.expeditech.common.blocks;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.custom.*;
import fr.caranouga.expeditech.common.blocks.custom.duct.Duct;
import fr.caranouga.expeditech.common.blocks.custom.duct.EnergyDuct;
import fr.caranouga.expeditech.common.blocks.custom.machine.CoalGeneratorMachine;
import fr.caranouga.expeditech.common.blocks.custom.multiblock.AbstractMultiBlockMaster;
import fr.caranouga.expeditech.common.blocks.custom.multiblock.TestMBMaster;
import fr.caranouga.expeditech.common.blocks.custom.multiblock.TestMBSlave;
import fr.caranouga.expeditech.common.items.custom.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Expeditech.MODID);
    public static final Map<RegistryObject<? extends Block>, BlockEntry> BLOCKS_DATA = new HashMap<>();

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
    public static final RegistryObject<CoalGeneratorMachine> COAL_GENERATOR = block("coal_generator", CoalGeneratorMachine::new);
    public static final RegistryObject<ConsumerMachine> CONSUMER = block("consumer", ConsumerMachine::new);

    // Ducts
    public static final RegistryObject<EnergyDuct> ENERGY_DUCT = duct("energy", EnergyDuct::new);

    // MultiBlocks
    public static final RegistryObject<TestMBMaster> TEST_MB_MASTER = multiBlockMaster("test_mb_master", TestMBMaster::new);
    public static final RegistryObject<TestMBSlave> TEST_SLAVE_MB = block("test_mb_slave", TestMBSlave::new);
    // endregion

    // region Utils
    private static RegistryObject<Block> block(String id, AbstractBlock.Properties properties){
        return block(id, () -> new Block(properties));
    }

    private static <B extends Block> RegistryObject<B> block(String id, Supplier<B> supplier){
        RegistryObject<B> blockObj = blockWithoutItem(id, supplier, new BlockEntry(BlockEntry.Model.BLOCK));
        ModItems.blockItem(id, blockObj);

        return blockObj;
    }

    private static <B extends Block> RegistryObject<B> blockWithoutItem(String id, Supplier<B> supplier, BlockEntry data){
        RegistryObject<B> entry = BLOCKS.register(id, supplier);
        BLOCKS_DATA.put(entry, data);

        return entry;
    }

    private static RegistryObject<OreBlock> ore(String id, AbstractBlock.Properties properties){
        return block(id, () -> new OreBlock(properties));
    }

    private static <D extends Duct<?>> RegistryObject<D> duct(String id, Supplier<D> supplier){
        return blockWithoutItem(id + "_duct", supplier, new BlockEntry(BlockEntry.Model.DUCT));
    }

    private static <M extends AbstractMultiBlockMaster> RegistryObject<M> multiBlockMaster(String id, Supplier<M> supplier){
        return block(id, supplier);
    }

    public static boolean never(BlockState state, IBlockReader world, BlockPos pos, EntityType<?> entType) {
        return false;
    }
    // endregion

    public static void register(IEventBus eBus){
        BLOCKS.register(eBus);
    }
}
