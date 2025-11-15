package fr.caranouga.expeditech.datagen.providers.lang;

import fr.caranouga.expeditech.common.items.ModItems;
import net.minecraft.data.DataGenerator;

public class ModLanguageProvider extends CustomLanguageProvider {
    public ModLanguageProvider(DataGenerator generator) {
        super(generator, "en_us", "fr_fr");
    }

    @Override
    protected void addTranslations() {
        // en_us
        addItem(ModItems.CARANITE, "Caranite");

        switchLocale();

        // fr_fr
        addItem(ModItems.CARANITE, "Caranite");
    }
}
