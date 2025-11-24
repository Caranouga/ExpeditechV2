package fr.caranouga.expeditech.datagen.builders.recipe;

import com.google.gson.JsonObject;
import fr.caranouga.expeditech.common.recipes.ModRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class SandingRecipeBuilder {
    private final Item result;
    private final int count;
    private final int duration;
    private final int energy;
    private Ingredient ingredient;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();

    public SandingRecipeBuilder(IItemProvider result, int count, int duration, int energy) {
        this.result = result.asItem();
        this.count = count;
        this.duration = duration;
        this.energy = energy;
    }

    public static SandingRecipeBuilder sanding(IItemProvider result, int duration, int energy){
        return new SandingRecipeBuilder(result, 1, duration, energy);
    }

    // region Requires
    public SandingRecipeBuilder requires(ITag<Item> pTag) {
        return this.requires(Ingredient.of(pTag));
    }

    public SandingRecipeBuilder requires(IItemProvider pItem) {
        this.requires(Ingredient.of(pItem));
        return this;
    }

    public SandingRecipeBuilder requires(Ingredient pIngredient) {
        this.ingredient = pIngredient;
        return this;
    }
    // endregion

    // region Unlocked by
    public SandingRecipeBuilder unlockedBy(String name, ICriterionInstance trigger) {
        this.advancement.addCriterion(name, trigger);
        return this;
    }
    // endregion

    // Save
    public void save(Consumer<IFinishedRecipe> finishedRecipeConsumer) {
        this.save(finishedRecipeConsumer, ForgeRegistries.ITEMS.getKey(this.result));
    }

    public void save(Consumer<IFinishedRecipe> finishedRecipeConsumer, String path) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.result);
        if (new ResourceLocation(path).equals(resourcelocation)) {
            throw new IllegalStateException("Sanding Recipe " + path + " should remove its 'save' argument");
        } else {
            this.save(finishedRecipeConsumer, new ResourceLocation(path));
        }
    }

    public void save(Consumer<IFinishedRecipe> finishedRecipeConsumer, ResourceLocation location) {
        this.ensureValid(location);
        this.advancement.parent(
                        new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(location))
                .rewards(AdvancementRewards.Builder.recipe(location))
                .requirements(IRequirementsStrategy.OR);
        finishedRecipeConsumer.accept(new SandingRecipeBuilder.Result(
                location,
                this.result,
                this.count,
                this.duration,
                this.energy,
                this.ingredient,
                this.advancement,
                modLocation("recipes/" + location.getPath())
        ));
    }
    // endregion

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
        if (this.ingredient == null) {
            throw new IllegalStateException("No ingredient for recipe " + pId);
        }
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final Ingredient ingredient;
        private final int duration;
        private final int energy;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation pId, Item pResult, int pCount, int pDuration, int pEnergy, Ingredient pIngredient, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.ingredient = pIngredient;
            this.duration = pDuration;
            this.energy = pEnergy;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        public void serializeRecipeData(@Nonnull JsonObject pJson) {
            ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(this.result);
            if(itemKey == null) return;

            pJson.add("ingredient", ingredient.toJson());

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", itemKey.toString());
            if (this.count > 1) {
                jsonobject.addProperty("count", this.count);
            }

            pJson.add("result", jsonobject);
            pJson.addProperty("duration", this.duration);
            pJson.addProperty("energy", this.energy);
        }

        @Nonnull
        public IRecipeSerializer<?> getType() {
            return ModRecipes.SANDING_SERIALIZER.get();
        }

        @Nonnull
        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
