package fr.caranouga.expeditech.datagen.providers.lang;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.duct.Duct;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import fr.caranouga.expeditech.common.items.custom.ModItems;
import fr.caranouga.expeditech.common.utils.Locale;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class CustomLanguageProvider implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // <lang, <key, value>>
    private final Map<Locale, Map<String, String>> data = new HashMap<>();
    private final DataGenerator generator;
    private Locale currentLocale;

    public CustomLanguageProvider(DataGenerator generator) {
        this.generator = generator;
    }

    /**
     * This function populates the data hashmap containing the translations
     */
    protected abstract void addTranslations();

    @Override
    public void run(@Nonnull DirectoryCache pCache) throws IOException {
        addTranslations();

        if(data.isEmpty()){
            Expeditech.LOGGER.warn("No translations found for {}. Please add translations in the addTranslations() method", this.getName());
            return;
        }

        verifyIfAllLocalesAreSet();
        verifyIfAllContentIsSet();
        checkDiff();

        for (Locale locale : Locale.values()) {
            save(pCache, data.get(locale), this.generator.getOutputFolder()
                    .resolve("assets/" + Expeditech.MODID + "/lang/" + locale.getName() + ".json"));
        }
    }

    @Override
    @Nonnull
    public String getName() {
        return Expeditech.MODID + " - Language Provider";
    }

    /**
     * This function verify that all locales defined during the class initialization have received translations
     */
    private void verifyIfAllLocalesAreSet(){
        for (Locale locale : Locale.values()) {
            if(!data.containsKey(locale)) throw new IllegalStateException("Locale " + locale.getName() + " is not set in the language provider");
        }
    }

    /**
     * This function verify that all items, blocks, ... have translations
     * This function does not stop the provider if something is missing, it only prints a message to the console
     */
    private void verifyIfAllContentIsSet(){
        ModItems.ITEMS.getEntries().forEach(itemRegistryObj -> {
            String name = itemRegistryObj.get().getDescriptionId();
            if(!data.get(Locale.EN_US).containsKey(name)){
                Expeditech.LOGGER.warn("Item {} does not have a translation, did you forgot to add one ?", name);
            }
        });
        ModBlocks.BLOCKS.getEntries().forEach(blockRegistryObj -> {
            String name = blockRegistryObj.get().getDescriptionId();
            if(!data.get(Locale.EN_US).containsKey(name)){
                Expeditech.LOGGER.warn("Block {} does not have a translation, did you forgot to add one ?", name);
            }
        });
    }

    /**
     * This functions that all locales have the same keys
     */
    private void checkDiff(){
        ArrayList<String> keys = new ArrayList<>(data.get(Locale.EN_US).keySet());

        for(Locale locale : Locale.values()) {
            Map<String, String> localeData = data.get(locale);

            if(localeData.isEmpty()){
                throw new IllegalStateException("Locale " + locale + " is empty in the language provider");
            }

            for (String key : keys) {
                // We verify that each key of localeData is present in keys
                if (!localeData.containsKey(key)) {
                    throw new IllegalStateException("Locale " + locale + " is missing key: " + key);
                }

                // We verify that each key of keys is present in localeData
                if (!keys.contains(key)) {
                    throw new IllegalStateException("Key " + key + " is missing in locale " + locale);
                }
            }
        }
    }

    /**
     * This function saves the translations into the file
     * @param cache The directory cache
     * @param dataMap The map containing the translations
     * @param path The path of the file to put translations in
     * @throws IOException If the buffer cannot be written
     */
    private void save(DirectoryCache cache, Map<String, String> dataMap, Path path) throws IOException {
        String data = GSON.toJson(dataMap);
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data);
        String hash = Hashing.sha1().hashUnencodedChars(data).toString();
        if(!Objects.equals(cache.getHash(path), hash) || !Files.exists(path)){
            Files.createDirectories(path.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                bufferedwriter.write(data);
            }
        }

        cache.putNew(path, hash);
    }

    /**
     * This function should be used to change the locale the translations will be written into
     */
    protected void selectLocale(Locale locale){
        this.currentLocale = locale;
    }

    // region Register function

    /**
     * This function add an item's translation
     * @param item The item to generate the translation for
     * @param translation The translation
     */
    protected void addItem(RegistryObject<? extends Item> item, String translation){
        add(item.get().getDescriptionId(), translation);
    }

    /**
     * This function add a block's translation
     * @param block The block to generate the translation for
     * @param translation The translation
     */
    protected void addBlock(RegistryObject<? extends Block> block, String translation){
        add(block.get().getDescriptionId(), translation);
    }

    /**
     * This function add a duct's translation
     * @param duct The duct to generate the translation for
     * @param baseTranslation The base translation (should contain 1 "%s" that'll be replaced by the duct's tier)
     * @param trans The translation for each of the duct tier
     */
    protected void addDuct(RegistryObject<? extends Duct<?>> duct, String baseTranslation, Map<DuctTier, String> trans){
        for (DuctTier tier : DuctTier.values()) {
            add(
                    duct.get().getDescriptionId() + "." + tier.getName(),
                    baseTranslation.replace("%s", trans.get(tier))
            );
        }
    }

    /**
     * This function add an item group's translation
     * @param group The item group to generate the translation for
     * @param translation The translation
     */
    protected void addItemGroup(ItemGroup group, String translation){
        add(group.getDisplayName().getString(), translation);
    }

    protected void addCommand(String commandName, String path, String translation){
        add("commands." + Expeditech.MODID + "." + commandName + "." + path, translation);
    }

    protected void addScreen(String screen, String translation){
        add("screen." + Expeditech.MODID + "." + screen, translation);
    }

    protected void addJEI(String screen, String translation){
        add("jei." + Expeditech.MODID + "." + screen, translation);
    }

    protected void addTooltip(String screen, String path, String translation){
        add("tooltip." + Expeditech.MODID + "." + screen + "." + path, translation);
    }

    protected void addJEITooltip(String screen, String path, String translation){
        add("jei." + Expeditech.MODID + "." + screen + ".tooltips." + path, translation);
    }

    /**
     * This function add a translation based on a key
     * @param key The key to translate for
     * @param value The translation
     */
    protected void add(String key, String value){
        this.data.computeIfAbsent(currentLocale, k -> new HashMap<>());
        if(this.data.get(currentLocale).put(key, value) != null){
            throw new IllegalStateException("Duplicate translation key " + key);
        }
    }
    // endregion
}
