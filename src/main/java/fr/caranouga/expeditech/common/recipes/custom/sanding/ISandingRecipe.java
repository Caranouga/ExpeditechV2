package fr.caranouga.expeditech.common.recipes.custom.sanding;

import fr.caranouga.expeditech.common.utils.StringUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface ISandingRecipe extends IRecipe<Inventory> {
    ResourceLocation TYPE_ID = StringUtils.modLocation("sanding");

    @Override
    @Nonnull
    default IRecipeType<?> getType() {
        Optional<IRecipeType<?>> recipeTypeOptional = Registry.RECIPE_TYPE.getOptional(TYPE_ID);

        try {
            return recipeTypeOptional.orElseThrow(IllegalAccessException::new);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    default boolean canCraftInDimensions(int pWidth, int pHeight){
        return true;
    }
}
