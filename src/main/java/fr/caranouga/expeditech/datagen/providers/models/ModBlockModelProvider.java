package fr.caranouga.expeditech.datagen.providers.models;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockModelProvider extends BlockModelProvider {
    public ModBlockModelProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, Expeditech.MODID, fileHelper);
    }

    @Override
    protected void registerModels() {
        ModBlocks.BLOCKS.getEntries().forEach(entry -> {
        });
    }
}
