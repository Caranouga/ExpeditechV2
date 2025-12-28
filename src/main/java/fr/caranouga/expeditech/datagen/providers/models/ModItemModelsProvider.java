package fr.caranouga.expeditech.datagen.providers.models;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.BlockEntry;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.duct.Duct;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import fr.caranouga.expeditech.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

import java.util.Map;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

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
                Map.Entry<RegistryObject<? extends Block>, BlockEntry> blockEntryEntry = ModBlocks.BLOCKS_DATA.entrySet().stream()
                        .filter((blockEntry) -> blockEntry.getKey().get() == ((BlockItem) item).getBlock())
                        .findFirst().orElse(null);
                if(blockEntryEntry != null) {
                    if (blockEntryEntry.getValue().getModel() == BlockEntry.Model.DUCT) {
                        generateDuctItem((Duct<?>) ((BlockItem) item).getBlock());
                        return;
                    }
                }

                withExistingParent(name, modLoc("block/" + name));
                return;
            }

            ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
            builder(itemGenerated, name);
        });
    }

    private void builder(ModelFile itemGenerated, String name){
        getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }

    private void generateDuctItem(Duct<?> duct) {
        String type = duct.getType();

        ItemModelBuilder builder = withExistingParent("energy_duct", "item/generated");

        for (DuctTier tier : duct.getTiers()) {
            String name = tier.getName() + "_" + type + "_duct";
            builder.override().predicate(modLocation("tier"), tier.getId()).model(getExistingFile(modLocation("block/" + name + "_core"))).end();
        }
    }
}
