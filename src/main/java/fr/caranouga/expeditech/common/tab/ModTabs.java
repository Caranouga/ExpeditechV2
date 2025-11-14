package fr.caranouga.expeditech.common.tab;

import fr.caranouga.expeditech.common.items.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModTabs {
    public static final ItemGroup EXPEDITECH = new ItemGroup("expeditech") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.CARANITE.get());
        }
    };
}
