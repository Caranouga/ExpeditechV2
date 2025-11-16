package fr.caranouga.expeditech.datagen.providers.lang;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.caranouga.expeditech.Expeditech;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class CustomLanguageProvider implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // <lang, <key, value>>
    private final Map<String, Map<String, String>> data = new HashMap<>();
    private final DataGenerator generator;
    private final String[] locales;
    private int currentLocaleIdx = 0;

    public CustomLanguageProvider(DataGenerator generator, String... locales) {
        this.generator = generator;
        this.locales = locales;
    }

    /**
     * This function populates the data hashmap containing the translations
     * @since 1.0.0
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
        checkDiff();

        for (String locale : locales) {
            save(pCache, data.get(locale), this.generator.getOutputFolder()
                    .resolve("assets/" + Expeditech.MODID + "/lang/" + locale + ".json"));
        }
    }

    @Override
    @Nonnull
    public String getName() {
        return Expeditech.MODID + " - Language Provider";
    }

    /**
     * This function verify that all locales defined during the class initialization have received translations
     * @since 1.0.0
     */
    private void verifyIfAllLocalesAreSet(){
        for (String locale : locales) {
            if(!data.containsKey(locale)) throw new IllegalStateException("Locale " + locale + " is not set in the language provider");
        }
    }

    /**
     * This functions that all locales have the same keys
     * @since 1.0.0
     */
    private void checkDiff(){
        ArrayList<String> keys = new ArrayList<>(data.get(locales[0]).keySet());

        for(String locale : locales) {
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
     * @since 1.0.0
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
     * @since 1.0.0
     */
    protected void switchLocale(){
        this.currentLocaleIdx++;
        if(this.currentLocaleIdx >= this.locales.length){
            this.currentLocaleIdx = 0;
            Expeditech.LOGGER.warn("No more locales available, switching back to default locale");
        }
    }

    // region Register function
    protected void addItem(RegistryObject<Item> item, String translation){
        add(item.get().getDescriptionId(), translation);
    }

    protected void addItemGroup(ItemGroup group, String translation){
        add(group.getDisplayName().getString(), translation);
    }

    private void add(String key, String value){
        String currentLocale = this.locales[this.currentLocaleIdx];
        this.data.computeIfAbsent(currentLocale, k -> new HashMap<>());
        if(this.data.get(currentLocale).put(key, value) != null){
            throw new IllegalStateException("Duplicate translation key " + key);
        }
    }
    // endregion
}
