package fr.caranouga.expeditech.common.world.gen;

import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.Dimension;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class ModOreGeneration {
    public static void generateOres(final BiomeLoadingEvent e){
        spawnOreInAllBiomes(OreType.CARANITE, e, Dimension.OVERWORLD);
    }

    private static void spawnOreInAllBiomes(OreType type, final BiomeLoadingEvent e, RegistryKey<Dimension> dim){
        e.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, makeOreFeature(type, dim));
    }

    private static ConfiguredFeature<?, ?> makeOreFeature(OreType ore, RegistryKey<Dimension> dimensionToSpawn) {
        OreFeatureConfig config = null;

        String dimStr = dimensionToSpawn.toString();
        if(dimStr.equals(Dimension.OVERWORLD.toString())) {
            config = getOverworldFeatureConfig(ore);
        } else if(dimStr.equals(Dimension.NETHER.toString())) {
            config = getNetherFeatureConfig(ore);
        } else if(dimStr.equals(Dimension.END.toString())) {
            config = getEndFeatureConfig(ore);
        }

        ConfiguredPlacement<TopSolidRangeConfig> placement = Placement.RANGE.configured(new TopSolidRangeConfig(ore.getMinHeight(), ore.getMinHeight(), ore.getMaxHeight()));

        return registerOreFeature(ore, config, placement);
    }

    private static ConfiguredFeature<?, ?> registerOreFeature(OreType ore, OreFeatureConfig config, ConfiguredPlacement<?> placement) {
        ResourceLocation oreRL = ore.getBlock().get().getRegistryName();

        if(oreRL == null) throw new IllegalStateException("Could not find the registry name of the block " + ore);

        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, oreRL,
                Feature.ORE.configured(config).decorated(placement).squared().count(ore.getVeinsPerChunk()));
    }

    private static OreFeatureConfig getOverworldFeatureConfig(OreType ore) {
        return new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                ore.getBlock().get().defaultBlockState(), ore.getMaxVeinSize());
    }

    private static OreFeatureConfig getNetherFeatureConfig(OreType ore) {
        return new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK,
                ore.getBlock().get().defaultBlockState(), ore.getMaxVeinSize());
    }

    private static OreFeatureConfig getEndFeatureConfig(OreType ore) {
        return new OreFeatureConfig(new BlockMatchRuleTest(Blocks.END_STONE),
                ore.getBlock().get().defaultBlockState(), ore.getMaxVeinSize());
    }
}
