package fr.caranouga.expeditech.datagen.providers.lang;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import fr.caranouga.expeditech.common.items.custom.ModItems;
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

    /**
     * This function generates the translations for en_US
     */
    private void addEN_US(){
        selectLocale(Locale.EN_US);

        addItem(ModItems.CARANITE, "Caranite");
        addItem(ModItems.IMPURE_CARANITE, "Impure Caranite");
        addItem(ModItems.SANDING_PAPER, "Sanding Paper");

        addBlock(ModBlocks.CARANITE_BLOCK, "Block of Caranite");
        addBlock(ModBlocks.CARANITE_ORE, "Caranite Ore");
        addBlock(ModBlocks.COAL_GENERATOR, "Coal Generator");

        Map<DuctTier, String> ductTrans = new HashMap<>();
        ductTrans.put(DuctTier.BASIC_TIER, "Basic");
        ductTrans.put(DuctTier.ADVANCED_TIER, "Advanced");
        addDuct(ModBlocks.ENERGY_DUCT, "%s Energy Duct", ductTrans);

        addItemGroup(ModTabs.EXPEDITECH, "Expeditech");

        addCommand("techlevel", "add.xp.success.single", "Gave %s tech experience points to %s");
        addCommand("techlevel", "add.levels.success.single", "Gave %s tech experience levels to %s");
        addCommand("techlevel", "add.xp.success.multiple", "Gave %s tech experience points to %s players");
        addCommand("techlevel", "add.levels.success.multiple", "Gave %s tech experience levels to %s players");
        addCommand("techlevel", "set.xp.success.single", "Set %s tech experience points on %s");
        addCommand("techlevel", "set.levels.success.single", "Set %s tech experience levels on %s");
        addCommand("techlevel", "set.xp.success.multiple", "Set %s tech experience points on %s players");
        addCommand("techlevel", "set.levels.success.multiple", "Set %s tech experience levels on %s players");
        addCommand("techlevel", "get.xp.success", "%s has %s tech experience points");
        addCommand("techlevel", "get.levels.success", "%s has %s tech experience levels");

        addScreen("coal_generator", "Coal Generator");

        addJEI("coal_generator", "Coal Generator");

        addTooltip("coal_generator", "progress", "Progress: %s/%s (%s)");
        addTooltip("coal_generator", "energy", "Energy: %s/%s");
        addJEITooltip("coal_generator", "progress", "Progress: %st (%ss)");
        addJEITooltip("coal_generator", "energy", "Energy: %s/%s");

        add("book.et.engineering_manual.name", "The Great Engineering Manual");
        add("book.et.engineering_manual.subtitle", "A serious guide to surviving your own inventions - with minimal explosions.");
        add("book.et.engineering_manual.description", "$(l)Welcome to $()$(thing)The Great Engineering Manual$()$(br2)This manual brings together everything you need to understand your new machines, ores, structures, and creatures.$(br)Take a deep breath, adjust your helmet...$(br)The adventure begins !");
    }

    /**
     * This function generates the translations for fr_FR
     */
    private void addFR_FR(){
        selectLocale(Locale.FR_FR);
        addItem(ModItems.CARANITE, "Caranite");
        addItem(ModItems.IMPURE_CARANITE, "Caranite impure");
        addItem(ModItems.SANDING_PAPER, "Papier abrasif");

        addBlock(ModBlocks.CARANITE_BLOCK, "Bloc de caranite");
        addBlock(ModBlocks.CARANITE_ORE, "Minerai de caranite");
        addBlock(ModBlocks.COAL_GENERATOR, "Générateur à charbon");

        Map<DuctTier, String> trans = new HashMap<>();
        trans.put(DuctTier.BASIC_TIER, "basique");
        trans.put(DuctTier.ADVANCED_TIER, "avancé");
        addDuct(ModBlocks.ENERGY_DUCT, "Conduit d'énergie %s", trans);

        addItemGroup(ModTabs.EXPEDITECH, "Expeditech");

        addCommand("techlevel", "add.xp.success.single", "Don de %s point(s) d'expérience technologique à %s");
        addCommand("techlevel", "add.levels.success.single", "Don de %s niveau(x) d'expérience technologique à %s");
        addCommand("techlevel", "add.xp.success.multiple", "Don de %s point(s) d'expérience technologique à %s joueurs");
        addCommand("techlevel", "add.levels.success.multiple", "Don de %s niveau(x) d'expérience technologique à %s joueurs");
        addCommand("techlevel", "set.xp.success.single", "Le nombre de points d'expérience technologique a été défini à %s pour %s");
        addCommand("techlevel", "set.levels.success.single", "Le nombre de niveaux d'expérience technologique a été défini à %s pour %s");
        addCommand("techlevel", "set.xp.success.multiple", "Le nombre de points d'expérience technologique a été défini à %s pour %s joueurs");
        addCommand("techlevel", "set.levels.success.multiple", "Le nombre de niveaux d'expérience technologique a été défini à %s pour %s joueurs");
        addCommand("techlevel", "get.xp.success", "%s a %s point(s) d'expérience technologique");
        addCommand("techlevel", "get.levels.success", "%s a %s niveau(x) d'expérience technologique");

        addScreen("coal_generator", "Générateur à charbon");

        addJEI("coal_generator", "Générateur à charbon");

        addTooltip("coal_generator", "progress", "Progression: %s/%s (%s)");
        addTooltip("coal_generator", "energy", "Énergie: %s/%s");
        addJEITooltip("coal_generator", "progress", "Progression: %st (%ss)");
        addJEITooltip("coal_generator", "energy", "Énergie: %s/%s");

        add("book.et.engineering_manual.name", "Le grand manuel d'ingénierie");
        add("book.et.engineering_manual.subtitle", "Un guide sérieux pour survivre à vos propres inventions - avec un minimum d’explosions.");
        add("book.et.engineering_manual.description", "$(l)Bienvenue dans $()$(thing)Le Grand Manuel D'ingénierie$()$(br2)Ce manuel rassemble tout ce qu'il faut pour comprendre vos nouvelles machines, minerais, structures et créatures.$(br)Prenez une grande inspiration, ajustez votre casque...$(br)L'aventure commence !");
    }
}
