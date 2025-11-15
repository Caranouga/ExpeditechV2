package fr.caranouga.expeditech.datagen.providers.tags;

import fr.caranouga.expeditech.Expeditech;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper, ModBlockTagProvider blockTagProvider) {
        super(pGenerator, blockTagProvider, Expeditech.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {

    }

    /**
     * This function adds the given items to the tag
     * @param tag The tag to add the items to
     * @param items The items to add
     * @since 1.0.0
     */
    private void addTag(Tags.IOptionalNamedTag<Item> tag, Item... items){
        this.tag(tag).add(items);
    }
}
