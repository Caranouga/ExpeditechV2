package fr.caranouga.expeditech.datagen.providers.models;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Expeditech.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModItems.ITEMS.getEntries().forEach(entry -> {
            Item item = entry.get();
            ResourceLocation registryName = item.getRegistryName();
            if(registryName == null) return;

            String name = registryName.getPath();

            if(item instanceof BlockItem){
                withExistingParent(name, modLoc("block/" + name));
            }

            ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
            builder(itemGenerated, name);
        });
    }

    private void builder(ModelFile itemGenerated, String name){
        getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }
}
