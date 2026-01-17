package fr.caranouga.expeditech.common.tileentities;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.tileentities.custom.ConsumerMachineTE;
import fr.caranouga.expeditech.common.tileentities.custom.duct.EnergyDuctTE;
import fr.caranouga.expeditech.common.tileentities.custom.machine.CoalGeneratorMachineTE;
import fr.caranouga.expeditech.common.tileentities.custom.multiblock.TestMultiBlockSlaveTile;
import fr.caranouga.expeditech.common.tileentities.custom.multiblock.TestMultiBlockMasterTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Expeditech.MODID);

    // Machines
    public static final RegistryObject<TileEntityType<ConsumerMachineTE>> CONSUMER = register("consumer",
            () -> TileEntityType.Builder.of(ConsumerMachineTE::new, ModBlocks.CONSUMER.get()).build(null));
    public static final RegistryObject<TileEntityType<CoalGeneratorMachineTE>> COAL_GENERATOR = register("coal_generator",
            () -> TileEntityType.Builder.of(CoalGeneratorMachineTE::new, ModBlocks.COAL_GENERATOR.get()).build(null));

    // Ducts
    public static final RegistryObject<TileEntityType<EnergyDuctTE>> ENERGY_DUCT = register("energy_duct",
            () -> TileEntityType.Builder.of(EnergyDuctTE::new, ModBlocks.ENERGY_DUCT.get()).build(null));

    // Internal tile entities
    public static final RegistryObject<TileEntityType<TestMultiBlockSlaveTile>> TEST_MB_SLAVE = register("test_mb_slave",
            () -> TileEntityType.Builder.of(TestMultiBlockSlaveTile::new, ModBlocks.TEST_SLAVE_MB.get()).build(null));

    public static final RegistryObject<TileEntityType<TestMultiBlockMasterTile>> TEST_MB_MASTER = register("test_mb_master",
            () -> TileEntityType.Builder.of(TestMultiBlockMasterTile::new, ModBlocks.TEST_MB_MASTER.get()).build(null));

    // region Utility methods
    /**
     * This function register a tile entity
     * @param name The tile entity's name
     * @param tileEntityType The tile entity type
     * @return The RegistryObject associated with the registered tile entity
     * @param <T> The tile entity's type
     */
    private static <T extends TileEntityType<? extends TileEntity>> RegistryObject<T> register(String name, Supplier<T> tileEntityType) {
        return TILE_ENTITIES.register(name + "_tile", tileEntityType);
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
