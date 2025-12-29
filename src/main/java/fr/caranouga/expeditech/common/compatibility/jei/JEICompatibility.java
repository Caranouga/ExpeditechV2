package fr.caranouga.expeditech.common.compatibility.jei;

import fr.caranouga.expeditech.common.compatibility.jei.sanding.SandingRecipeCategory;
import fr.caranouga.expeditech.common.items.ModItems;
import fr.caranouga.expeditech.common.recipes.ModRecipes;
import fr.caranouga.expeditech.common.recipes.custom.sanding.SandingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

@JeiPlugin
public class JEICompatibility implements IModPlugin {
    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return modLocation("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SandingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        ClientWorld level = Minecraft.getInstance().level;
        if(level == null) return;

        RecipeManager recipeManager = level.getRecipeManager();

        List<SandingRecipe> sandingRecipes = recipeManager.getAllRecipesFor(ModRecipes.SANDING_RECIPE).stream()
                .filter(r -> r instanceof SandingRecipe).collect(Collectors.toList());
        registration.addRecipes(sandingRecipes, SandingRecipeCategory.UID);
        //registration.addRecipes(sandingRecipes, SandingMachineRecipeCategory.UID);
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        // No GUI handlers to register
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.SANDING_PAPER.get()), SandingRecipeCategory.UID);
        //registration.addRecipeCatalyst(new ItemStack(ModBlocks.SANDING_MACHINE.get()), SandingMachineRecipeCategory.UID);
    }

    @Override
    public void registerRecipeTransferHandlers(@Nonnull IRecipeTransferRegistration registration) {
        //addRecipeTransferHandler(registration, SandingMachineRecipeCategory.UID, SandingMachineContainer.class, 0, 1);
    }

    private <C extends Container> void addRecipeTransferHandler(IRecipeTransferRegistration registration, ResourceLocation recipeCategoryUid, Class<C> containerClass, int recipeStartIndex, int recipeSlotsCount) {
        int vanillaInventorySlots = 36; // Vanilla inventory slots
        registration.addRecipeTransferHandler(containerClass, recipeCategoryUid, vanillaInventorySlots + recipeStartIndex, recipeSlotsCount, 0, vanillaInventorySlots);
    }
}
