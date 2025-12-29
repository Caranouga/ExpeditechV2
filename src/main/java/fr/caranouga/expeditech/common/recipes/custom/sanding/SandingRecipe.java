package fr.caranouga.expeditech.common.recipes.custom.sanding;

import com.google.gson.JsonObject;
import fr.caranouga.expeditech.common.recipes.ModRecipes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SandingRecipe implements ISandingRecipe {
    private final ResourceLocation id;
    private final ItemStack result;
    private final Ingredient ingredient;
    private final int energyNeeded;
    private final int duration;

    public SandingRecipe(ResourceLocation id, ItemStack result, Ingredient ingredient, int energyNeeded, int duration) {
        this.id = id;
        this.result = result;
        this.ingredient = ingredient;
        this.energyNeeded = energyNeeded;
        this.duration = duration;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, ingredient);
    }

    @Override
    public boolean matches(Inventory pInv, @Nonnull World pLevel) {
        return ingredient.test(pInv.getItem(0));
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull Inventory pInv) {
        return result.copy();
    }

    @Override
    @Nonnull
    public ItemStack getResultItem() {
        return result.copy();
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SANDING_SERIALIZER.get();
    }

    public int getEnergyNeeded() {
        return energyNeeded;
    }

    public int getDuration() {
        return duration;
    }

    public static class Type implements IRecipeType<SandingRecipe> {
        @Override
        public String toString() {
            return SandingRecipe.TYPE_ID.toString();
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SandingRecipe> {
        @Override
        @Nonnull
        public SandingRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pJson) {
            ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(pJson, "result"));
            Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(pJson,  "ingredient"));
            int energyNeeded = JSONUtils.getAsInt(pJson, "energy", 0);
            int duration = JSONUtils.getAsInt(pJson, "duration", 200);

            return new SandingRecipe(pRecipeId, result, ingredient, energyNeeded, duration);
        }

        @Nullable
        @Override
        public SandingRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull PacketBuffer pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            int energyNeeded = pBuffer.readVarInt();
            int duration = pBuffer.readVarInt();

            return new SandingRecipe(pRecipeId, result, ingredient, energyNeeded, duration);
        }

        @Override
        public void toNetwork(@Nonnull PacketBuffer pBuffer, @Nonnull SandingRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItemStack(pRecipe.getResultItem(), false);
            pBuffer.writeVarInt(pRecipe.getEnergyNeeded());
            pBuffer.writeVarInt(pRecipe.getDuration());
        }
    }
}
