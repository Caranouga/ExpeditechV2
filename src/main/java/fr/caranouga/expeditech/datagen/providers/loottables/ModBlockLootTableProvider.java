package fr.caranouga.expeditech.datagen.providers.loottables;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModBlockLootTableProvider extends LootTableProvider {
    public ModBlockLootTableProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, @Nonnull ValidationTracker validationtracker) {
        map.forEach((resourceLocation, lootTable) -> {
            LootTableManager.validate(validationtracker, resourceLocation, lootTable);
        });
    }

    @Override
    @Nonnull
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
                Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK)
        );
    }

    private static class ModBlockLootTables extends BlockLootTables {
        @Override
        protected void addTables() {
            ModBlocks.BLOCKS.getEntries().forEach(entry -> {
                dropSelf(entry.get());
            });
        }

        @Override
        @Nonnull
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
        }
    }
}
