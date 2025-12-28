package fr.caranouga.expeditech.datagen.providers.lang;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import fr.caranouga.expeditech.common.items.ModItems;
import fr.caranouga.expeditech.common.tab.ModTabs;
import fr.caranouga.expeditech.common.utils.Locale;
import net.minecraft.data.DataGenerator;

import java.util.HashMap;
import java.util.Map;

public class ModLanguageProvider extends CustomLanguageProvider {
    public ModLanguageProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void addTranslations() {
        // en_us
        addEN_US();

        // fr_fr
        addFR_FR();
    }

    private void addEN_US(){
        selectLocale(Locale.EN_US);

        addItem(ModItems.CARANITE, "Caranite");
        addItem(ModItems.IMPURE_CARANITE, "Impure Caranite");
        addItem(ModItems.SANDING_PAPER, "Sanding Paper");

        addBlock(ModBlocks.CARANITE_BLOCK, "Block of Caranite");
        addBlock(ModBlocks.CARANITE_ORE, "Caranite Ore");

        Map<DuctTier, String> ductTrans = new HashMap<>();
        ductTrans.put(DuctTier.BASIC_TIER, "Basic");
        ductTrans.put(DuctTier.ADVANCED_TIER, "Advanced");
        addDuct(ModBlocks.ENERGY_DUCT, "%s Energy Duct", ductTrans);

        addItemGroup(ModTabs.EXPEDITECH, "Expeditech");

        addCustom("book.et.engineering_manual.name", "The Great Engineering Manual");
        addCustom("book.et.engineering_manual.subtitle", "A serious guide to surviving your own inventions - with minimal explosions.");
        addCustom("book.et.engineering_manual.description", "$(l)Welcome to $()$(thing)The Great Engineering Manual$()$(br2)This manual brings together everything you need to understand your new machines, ores, structures, and creatures.$(br)Take a deep breath, adjust your helmet...$(br)The adventure begins !");
    }

    private void addFR_FR(){
        selectLocale(Locale.FR_FR);
        addItem(ModItems.CARANITE, "Caranite");
        addItem(ModItems.IMPURE_CARANITE, "Caranite impure");
        addItem(ModItems.SANDING_PAPER, "Papier abrasif");

        addBlock(ModBlocks.CARANITE_BLOCK, "Bloc de caranite");
        addBlock(ModBlocks.CARANITE_ORE, "Minerai de caranite");

        Map<DuctTier, String> trans = new HashMap<>();
        trans.put(DuctTier.BASIC_TIER, "basique");
        trans.put(DuctTier.ADVANCED_TIER, "avancé");
        addDuct(ModBlocks.ENERGY_DUCT, "Conduit d'énergie %s", trans);

        addItemGroup(ModTabs.EXPEDITECH, "Expeditech");

        addCustom("book.et.engineering_manual.name", "Le grand manuel d'ingénierie");
        addCustom("book.et.engineering_manual.subtitle", "Un guide sérieux pour survivre à vos propres inventions - avec un minimum d’explosions.");
        addCustom("book.et.engineering_manual.description", "$(l)Bienvenue dans $()$(thing)Le Grand Manuel D'ingénierie$()$(br2)Ce manuel rassemble tout ce qu'il faut pour comprendre vos nouvelles machines, minerais, structures et créatures.$(br)Prenez une grande inspiration, ajustez votre casque...$(br)L'aventure commence !");
    }
}
