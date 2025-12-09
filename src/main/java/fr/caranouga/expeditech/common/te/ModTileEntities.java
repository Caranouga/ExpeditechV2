package fr.caranouga.expeditech.common.te;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.te.custom.ConsumerMachineTE;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTE;
import fr.caranouga.expeditech.common.te.custom.EnergyDuctMachineTOneTE;
import fr.caranouga.expeditech.common.te.custom.GeneratorMachineTE;
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
    public static final RegistryObject<TileEntityType<ConsumerMachineTE>> CONSUMER = register("consumer_tile",
            () -> TileEntityType.Builder.of(ConsumerMachineTE::new, ModBlocks.CONSUMER.get()).build(null));
    public static final RegistryObject<TileEntityType<GeneratorMachineTE>> GENERATOR = register("generator_tile",
            () -> TileEntityType.Builder.of(GeneratorMachineTE::new, ModBlocks.GENERATOR.get()).build(null));

    // Ducts
    public static final RegistryObject<TileEntityType<EnergyDuctMachineTE>> ENERGY_DUCT = register("energy_duct",
            () -> TileEntityType.Builder.of(EnergyDuctMachineTE::new, ModBlocks.DUCT.get()).build(null));
    public static final RegistryObject<TileEntityType<EnergyDuctMachineTOneTE>> ENERGY_DUCT_T1 = register("energy_duct_t1",
            () -> TileEntityType.Builder.of(EnergyDuctMachineTOneTE::new, ModBlocks.DUCT_T1.get()).build(null));

    // region Utility methods
    private static <T extends TileEntityType<? extends TileEntity>> RegistryObject<T> register(String name, Supplier<T> tileEntityType) {
        return TILE_ENTITIES.register(name, tileEntityType);
    }

    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
}
