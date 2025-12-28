package fr.caranouga.expeditech.datagen;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.datagen.providers.ModBlockstateProvider;
import fr.caranouga.expeditech.datagen.providers.lang.ModLanguageProvider;
import fr.caranouga.expeditech.datagen.providers.loottables.ModBlockLootTableProvider;
import fr.caranouga.expeditech.datagen.providers.models.ModBlockModelProvider;
import fr.caranouga.expeditech.datagen.providers.models.ModItemModelsProvider;
import fr.caranouga.expeditech.datagen.providers.recipes.ModRecipeProvider;
import fr.caranouga.expeditech.datagen.providers.tags.ModBlockTagProvider;
import fr.caranouga.expeditech.datagen.providers.tags.ModItemTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD,
        modid = Expeditech.MODID
)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent e){
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper existingFileHelper = e.getExistingFileHelper();

        // Block related
        generator.addProvider(new ModBlockLootTableProvider(generator));
        generator.addProvider(new ModBlockModelProvider(generator, existingFileHelper));
        generator.addProvider(new ModBlockstateProvider(generator, existingFileHelper));
        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(generator, existingFileHelper);
        generator.addProvider(blockTagProvider);

        // Item related
        generator.addProvider(new ModItemModelsProvider(generator, existingFileHelper));
        generator.addProvider(new ModItemTagProvider(generator, existingFileHelper, blockTagProvider));

        // Other
        generator.addProvider(new ModLanguageProvider(generator));
        generator.addProvider(new ModRecipeProvider(generator));
    }
}
