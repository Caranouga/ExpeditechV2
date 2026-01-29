package fr.caranouga.expeditech.datagen.providers.models;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.BlockEntry;
import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.blocks.custom.MachineBlock;
import fr.caranouga.expeditech.common.blocks.custom.duct.Duct;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class ModBlockModelProvider extends BlockModelProvider {
    public ModBlockModelProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, Expeditech.MODID, fileHelper);
    }

    @Override
    protected void registerModels() {
        ModBlocks.BLOCKS.getEntries().forEach(entry -> {
            BlockEntry.Model modelEntry = ModBlocks.BLOCKS_DATA.get(entry).getModel();

            switch (modelEntry){
                case DUCT: {
                    generateDuctBlock((Duct<?>) entry.get());

                    break;
                }

                case MACHINE: {
                    generateMachineBlock((MachineBlock) entry.get());

                    break;
                }

                case BLOCK:
                default: {
                    break;
                }
            }
        });
    }

    /**
     * This function generates a duct block model
     * @param duct The duct to generate the model for
     */
    private void generateDuctBlock(Duct<?> duct) {
        String type = duct.getType();

        for (DuctTier tier : duct.getTiers()) {
            String blockName = tier.getName() + "_" + type + "_duct_";
            String basePath = "block/ducts/" + type + "/" + tier.getName() + "/" + blockName;
            withExistingParent(blockName + "core", modLocation("block/duct_core"))
                    .texture("0", modLocation(basePath + "core"))
                    .texture("particle", modLocation(basePath + "core"));
            withExistingParent(blockName + "connection", modLocation("block/duct_connection"))
                    .texture("0", modLocation(basePath + "connection"))
                    .texture("particle", modLocation(basePath + "connection"));
        }

    }

    private void generateMachineBlock(MachineBlock machine){
        String name = machine.getRegistryName().getPath();
        withExistingParent(name, mcLoc("block/orientable"))
                .texture("front", modLocation("block/" + name + "/" + name + "_front"))
                .texture("side", modLocation("block/" + name + "/" + name + "_side"))
                .texture("top", modLocation("block/" + name + "/" + name + "_top"));

        withExistingParent(name + "_on", mcLoc("block/orientable"))
                .texture("front", modLocation("block/" + name + "/" + name + "_front_on"))
                .texture("side", modLocation("block/" + name + "/" + name + "_side"))
                .texture("top", modLocation("block/" + name + "/" + name + "_top"));
    }
}
