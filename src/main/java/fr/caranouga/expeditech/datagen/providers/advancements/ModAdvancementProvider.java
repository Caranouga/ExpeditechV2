package fr.caranouga.expeditech.datagen.providers.advancements;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    private final List<Consumer<Consumer<Advancement>>> advancementTabs = ImmutableList.of(
            new MainAdvancements()
    );

    public ModAdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(@Nonnull Consumer<Advancement> consumer, @Nonnull ExistingFileHelper fileHelper) {
        for(Consumer<Consumer<Advancement>> advConsumer : this.advancementTabs){
            advConsumer.accept(consumer);
        }
    }
}
