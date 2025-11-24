package fr.caranouga.expeditech.datagen.providers.recipes;

import fr.caranouga.expeditech.common.blocks.ModBlocks;
import fr.caranouga.expeditech.common.items.ModItems;
import fr.caranouga.expeditech.common.utils.StringUtils;
import fr.caranouga.expeditech.datagen.builders.recipe.SandingRecipeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> finishedRecipeConsumer) {
        storageBlockAll(finishedRecipeConsumer, ModBlocks.CARANITE_BLOCK, ModItems.CARANITE);

        oreToItem(finishedRecipeConsumer, ModBlocks.CARANITE_ORE, ModItems.IMPURE_CARANITE, 0.9f, 150);

        sanding(finishedRecipeConsumer, ModItems.IMPURE_CARANITE, ModItems.CARANITE);
    }

    private void storageBlockAll(Consumer<IFinishedRecipe> recipeBuilder, RegistryObject<Block> blockKey, RegistryObject<Item> itemKey){
        itemToStorageBlock(recipeBuilder, blockKey, itemKey);
        storageBlockToItem(recipeBuilder, blockKey, itemKey);
    }

    private void itemToStorageBlock(Consumer<IFinishedRecipe> recipeBuilder, RegistryObject<Block> blockKey, RegistryObject<Item> itemKey){
        Block block = blockKey.get();
        Item item = itemKey.get();

        ShapedRecipeBuilder.shaped(block, 1).define('#', item)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_" + item.getDescriptionId(), has(item))
                .save(recipeBuilder, StringUtils.modLocation(block.getDescriptionId() + "_from_" + item.getDescriptionId()));
    }

    private void storageBlockToItem(Consumer<IFinishedRecipe> recipeBuilder, RegistryObject<Block> blockKey, RegistryObject<Item> itemKey){
        Block block = blockKey.get();
        Item item = itemKey.get();

        ShapelessRecipeBuilder.shapeless(item, 9)
                .requires(block)
                .unlockedBy("has_" + block.getDescriptionId(), has(block))
                .save(recipeBuilder, StringUtils.modLocation(item.getDescriptionId() + "_from_" + block.getDescriptionId()));
    }

    private void oreToItem(Consumer<IFinishedRecipe> recipeBuilder, RegistryObject<OreBlock> oreKey, RegistryObject<Item> itemKey, float experience, int time){
        OreBlock ore = oreKey.get();
        Item item = itemKey.get();

        CookingRecipeBuilder.smelting(Ingredient.of(ore), item, experience, time)
                .unlockedBy("has_" + ore.getDescriptionId(), has(ore))
                .save(recipeBuilder, StringUtils.modLocation("smelting_" + ore.getDescriptionId() + "_to_" + item.getDescriptionId()));
        CookingRecipeBuilder.blasting(Ingredient.of(ore), item, experience, time)
                .unlockedBy("has_" + ore.getDescriptionId(), has(ore))
                .save(recipeBuilder, StringUtils.modLocation("blasting_" + ore.getDescriptionId() + "_to_" + item.getDescriptionId()));
    }

    private void sanding(Consumer<IFinishedRecipe> recipeBuilder, RegistryObject<Item> inputKey, RegistryObject<Item> outputKey){
        Item input = inputKey.get();
        Item output = outputKey.get();

        SandingRecipeBuilder.sanding(output, 200, 1000)
                .requires(input)
                .unlockedBy("has_" + input.getDescriptionId(), has(input))
                .save(recipeBuilder, StringUtils.modLocation("sanding_" + input.getDescriptionId() + "_to_" + output.getDescriptionId()));
    }
}
