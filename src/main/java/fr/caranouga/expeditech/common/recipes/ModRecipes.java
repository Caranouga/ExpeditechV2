package fr.caranouga.expeditech.common.recipes;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.recipes.custom.sanding.SandingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Expeditech.MODID);
    private static final HashMap<IRecipeType<?>, ResourceLocation> RECIPE_TYPES = new HashMap<>();

    // region Recipe Serializers
    public static final RegistryObject<SandingRecipe.Serializer> SANDING_SERIALIZER = serializer(SandingRecipe.TYPE_ID,
            SandingRecipe.Serializer::new);
    // endregion

    // region Recipe Types
    public static final IRecipeType<SandingRecipe> SANDING_RECIPE = recipe(SandingRecipe.TYPE_ID, new SandingRecipe.Type());
    // endregion

    // region Utils
    private static <S extends IRecipeSerializer<?>> RegistryObject<S> serializer(ResourceLocation id, Supplier<S> serializerSupplier){
        return RECIPE_SERIALIZERS.register(id.getPath(), serializerSupplier);
    }

    private static <T extends IRecipe<?>> IRecipeType<T> recipe(ResourceLocation id, IRecipeType<T> type){
        RECIPE_TYPES.put(type, id);

        return type;
    }

    public static void register(IEventBus eBus){
        RECIPE_SERIALIZERS.register(eBus);
        RECIPE_TYPES.forEach((type, id) -> {
            Registry.register(Registry.RECIPE_TYPE, id, type);
        });
    }
    // endregion
}
