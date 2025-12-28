package fr.caranouga.expeditech.datagen.providers.tags;

import fr.caranouga.expeditech.Expeditech;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
        super(pGenerator, Expeditech.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {

    }

    /**
     * This function adds the given blocks to the tag
     * @param tag The tag to add the blocks to
     * @param blocks The blocks to add
     */
    private void addTag(Tags.IOptionalNamedTag<Block> tag, Block... blocks){
        this.tag(tag).add(blocks);
    }
}
