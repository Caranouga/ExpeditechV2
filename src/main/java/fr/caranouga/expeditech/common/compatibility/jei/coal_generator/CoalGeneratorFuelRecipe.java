package fr.caranouga.expeditech.common.compatibility.jei.coal_generator;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CoalGeneratorFuelRecipe {
    private final List<ItemStack> inputs;
    private final int burnTime;

    public CoalGeneratorFuelRecipe(Collection<ItemStack> inputs, int burnTime) {
        this.inputs = new ArrayList<>(inputs);
        this.burnTime = burnTime;
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }

    public int getBurnTime() {
        return burnTime;
    }
}
