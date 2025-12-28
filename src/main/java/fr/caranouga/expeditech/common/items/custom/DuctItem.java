package fr.caranouga.expeditech.common.items.custom;

import fr.caranouga.expeditech.common.blocks.custom.duct.Duct;
import fr.caranouga.expeditech.common.blocks.custom.duct.DuctTier;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class DuctItem extends BlockItem {
    public static final String TIER_TAG = "Tier";

    public DuctItem(Duct<?> block, Properties properties) {
        super(block, properties);
    }

    public static void setTier(ItemStack stack, DuctTier tier) {
        stack.getOrCreateTag().putString(TIER_TAG, tier.getName());
    }

    public static DuctTier getTier(ItemStack stack) {
        if (!stack.hasTag()) return DuctTier.BASIC_TIER;
        return DuctTier.byName(stack.getTag().getString(TIER_TAG));
    }

    @Override
    @Nonnull
    public ITextComponent getName(@Nonnull ItemStack pStack) {
        return new TranslationTextComponent(getDescriptionId(pStack) + "." + getTier(pStack).getName());
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext ctx, @Nonnull BlockState state) {
        if(ctx.getLevel().isClientSide()) return super.placeBlock(ctx, state);

        DuctTier tier = getTier(ctx.getItemInHand());
        return super.placeBlock(
                ctx,
                state.setValue(Duct.TIER, tier)
        );
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup tab, @Nonnull NonNullList<ItemStack> items) {
        if (!allowdedIn(tab)) return;

        for (DuctTier tier : DuctTier.values()) {
            ItemStack stack = new ItemStack(this);
            setTier(stack, tier);
            items.add(stack);
        }
    }
}
