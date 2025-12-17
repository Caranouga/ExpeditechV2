package fr.caranouga.expeditech.common.blocks.custom.duct;

import fr.caranouga.expeditech.Expeditech;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public enum DuctTier implements IStringSerializable {
    BASIC_TIER(0, "basic", 1),
    ADVANCED_TIER(1, "advanced", 10),
    ;

    public static final ResourceLocation TIER_PREDICATE = new ResourceLocation(Expeditech.MODID, "tier");

    private final String name;
    private final int amount;
    private final int id;

    DuctTier(int id, String name, int amount) {
        this.name = name;
        this.amount = amount;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public static DuctTier byName(String name){
        for (DuctTier value : values()) {
            if(value.name.equals(name)) return value;
        }

        return null;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.name;
    }
}
