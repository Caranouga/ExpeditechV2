package fr.caranouga.expeditech.common.triggers;

import com.google.gson.JsonObject;
import fr.caranouga.expeditech.common.capabilities.ModCapabilities;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

import static fr.caranouga.expeditech.common.utils.StringUtils.modLocation;

public class TechLevelTrigger extends AbstractCriterionTrigger<TechLevelTrigger.Instance> {
    private static final ResourceLocation ID = modLocation("tech_level_trigger");

    @Override
    protected TechLevelTrigger.Instance createInstance(JsonObject pJson, EntityPredicate.AndPredicate pEntityPredicate, ConditionArrayParser pConditionsParser) {
        int requiredTechXP = pJson.has("required_tech_xp") ? pJson.get("required_tech_xp").getAsInt() : 0;
        return new Instance(requiredTechXP);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player){
        this.trigger(player, instance -> instance.test(player));
    }

    public static class Instance extends CriterionInstance {
        private final int requiredTechXp;

        public Instance(int requiredTechXp) {
            super(ID, EntityPredicate.AndPredicate.ANY);
            this.requiredTechXp = requiredTechXp;
        }

        public boolean test(ServerPlayerEntity player){
            return player.getCapability(ModCapabilities.TECH_LEVEL)
                    .map(techLevel -> techLevel.getTechXp() >= requiredTechXp)
                    .orElse(false);
        }

        @Override
        public JsonObject serializeToJson(ConditionArraySerializer pConditions) {
            JsonObject json = super.serializeToJson(pConditions);
            json.addProperty("required_tech_xp", this.requiredTechXp);

            return json;
        }
    }
}
