package fr.caranouga.expeditech.datagen.providers;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.duct.Duct;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Expeditech.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModBlocks.BLOCKS.getEntries().forEach(entry -> {
            if(entry.get() instanceof Duct) registerDuctBlock((Duct<?>) entry.get());
            else simpleBlock(entry.get());
        });
    }

    private void registerDuctBlock(Duct<?> duct) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(duct);

        for (DuctTier tier : duct.getTiers()) {
            String prefix = tier.getName() + "_" + duct.getType() + "_duct";

            ModelFile core = models().getExistingFile(modLocation("block/" + prefix + "_core"));
            ModelFile part = models().getExistingFile(modLocation("block/" + prefix + "_connection"));

            builder
                    .part().modelFile(core).addModel().condition(Duct.TIER, tier).end()
                    .part().modelFile(part).rotationY(0).addModel().condition(Duct.TIER, tier).condition(BlockStateProperties.NORTH, true).end()
                    .part().modelFile(part).rotationY(180).addModel().condition(Duct.TIER, tier).condition(BlockStateProperties.SOUTH, true).end()
                    .part().modelFile(part).rotationY(270).addModel().condition(Duct.TIER, tier).condition(BlockStateProperties.WEST, true).end()
                    .part().modelFile(part).rotationY(90).addModel().condition(Duct.TIER, tier).condition(BlockStateProperties.EAST, true).end()
                    .part().modelFile(part).rotationX(270).addModel().condition(Duct.TIER, tier).condition(BlockStateProperties.UP, true).end()
                    .part().modelFile(part).rotationX(90).addModel().condition(Duct.TIER, tier).condition(BlockStateProperties.DOWN, true).end();
        }
    }
}
