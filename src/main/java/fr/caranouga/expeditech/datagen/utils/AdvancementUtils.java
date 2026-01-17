package fr.caranouga.expeditech.datagen.utils;

import fr.caranouga.expeditech.common.capabilities.tech.TechLevelImplementation;
import fr.caranouga.expeditech.common.triggers.TechLevelTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class AdvancementUtils {
    public static String id(String s) {
        return modLocation(s).toString();
    }

    public ItemPredicate[] getItemPredicate(Item... items) {
        ItemPredicate[] predicates = new ItemPredicate[items.length];
        for (int i = 0; i < items.length; i++) {
            predicates[i] = ItemPredicate.Builder.item().of(items[i]).build();
        }
        return predicates;
    }

    public static Advancement.Builder techLevelAdvancement(int required, boolean isLevels) {
        int requiredTechXp = isLevels ? TechLevelImplementation.getXpForLevel(required) : required;

        return Advancement.Builder.advancement().addCriterion("0", new TechLevelTrigger.Instance(requiredTechXp));
    }

    public static Advancement.Builder generateDisplay(Advancement.Builder builder, String category, String id, Item icon, @Nullable ResourceLocation bg, FrameType frameType, boolean showToast, boolean announceToChat, boolean hidden) {
        return builder.display(icon,
                advancementTitle(category, id),
                advancementDescription(category, id),
                bg,
                frameType,
                showToast, announceToChat, hidden);
    }

    public static Advancement.Builder advancement(String category, String key, Item icon, @Nullable ResourceLocation bg, FrameType frameType, boolean showToast, boolean announceToChat, boolean hidden) {
        return generateDisplay(Advancement.Builder.advancement(), category, key, icon, bg, frameType, showToast, announceToChat, hidden);
    }

    public static Advancement.Builder generateDisplay(Advancement.Builder builder, String category, String id, Item icon, FrameType frameType, boolean showToast, boolean announceToChat, boolean hidden) {
        return generateDisplay(builder, category, id, icon, null, frameType, showToast, announceToChat, hidden);
    }

    public static Advancement.Builder advancement(String category, String key, Item icon, FrameType frameType, boolean showToast, boolean announceToChat, boolean hidden) {
        return advancement(category, key, icon, null, frameType, showToast, announceToChat, hidden);
    }

    public static ITextComponent advancementTitle(String category, String key) {
        return new TranslationTextComponent("advancements.expeditech." + category + "." + key + ".title");
    }

    public static ITextComponent advancementDescription(String category, String key) {
        return new TranslationTextComponent("advancements.expeditech." + category + "." + key + ".description");
    }
}
