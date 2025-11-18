package fr.caranouga.expeditech.datagen.providers.lang;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.items.ModItems;
import fr.caranouga.expeditech.common.tab.ModTabs;
import net.minecraft.data.DataGenerator;

public class ModLanguageProvider extends CustomLanguageProvider {
    public ModLanguageProvider(DataGenerator generator) {
        super(generator, "en_us", "fr_fr");
    }

    @Override
    protected void addTranslations() {
        // en_us
        addItem(ModItems.CARANITE, "Caranite");
        addItem(ModItems.IMPURE_CARANITE, "Impure Caranite");

        addBlock(ModBlocks.CARANITE_BLOCK, "Block of Caranite");
        addBlock(ModBlocks.CARANITE_ORE, "Caranite Ore");

        addItemGroup(ModTabs.EXPEDITECH, "Expeditech");

        addCustom("book.et.engineering_manual.name", "The Great Engineering Manual");
        addCustom("book.et.engineering_manual.subtitle", "A serious guide to surviving your own inventions - with minimal explosions.");
        addCustom("book.et.engineering_manual.description", "$(l)Welcome to $()$(thing)The Great Engineering Manual$()$(br2)This manual brings together everything you need to understand your new machines, ores, structures, and creatures.$(br)Take a deep breath, adjust your helmet...$(br)The adventure begins !");

        switchLocale();

        // fr_fr
        addItem(ModItems.CARANITE, "Caranite");
        addItem(ModItems.IMPURE_CARANITE, "Caranite impure");

        addBlock(ModBlocks.CARANITE_BLOCK, "Bloc de caranite");
        addBlock(ModBlocks.CARANITE_ORE, "Minerai de caranite");

        addItemGroup(ModTabs.EXPEDITECH, "Expeditech");

        addCustom("book.et.engineering_manual.name", "Le Grand Manuel D'ingénierie");
        addCustom("book.et.engineering_manual.subtitle", "Un guide sérieux pour survivre à vos propres inventions - avec un minimum d’explosions.");
        addCustom("book.et.engineering_manual.description", "$(l)Bienvenue dans $()$(thing)Le Grand Manuel D'ingénierie$()$(br2)Ce manuel rassemble tout ce qu'il faut pour comprendre vos nouvelles machines, minerais, structures et créatures.$(br)Prenez une grande inspiration, ajustez votre casque...$(br)L'aventure commence !");
    }
}
