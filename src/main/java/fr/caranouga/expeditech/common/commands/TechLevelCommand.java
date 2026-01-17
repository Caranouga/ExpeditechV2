package fr.caranouga.expeditech.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.capabilities.tech.TechLevelUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

public class TechLevelCommand {
    public TechLevelCommand(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> literalCommandNode = dispatcher.register(
                Commands.literal("techlevel")
                        // /techlevel add <targets> <amount> [xp|levels]
                        .then(Commands.literal("add")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                                .executes((cmd) -> addTechLevel(cmd.getSource(), EntityArgument.getPlayers(cmd, "targets"), IntegerArgumentType.getInteger(cmd, "amount"), Type.XP)).then(Commands.literal("xp")
                                                        .executes((cmd) -> addTechLevel(cmd.getSource(), EntityArgument.getPlayers(cmd, "targets"), IntegerArgumentType.getInteger(cmd, "amount"), Type.XP))
                                                ).then(Commands.literal("levels")
                                                        .executes((cmd) -> addTechLevel(cmd.getSource(), EntityArgument.getPlayers(cmd, "targets"), IntegerArgumentType.getInteger(cmd, "amount"), Type.LEVELS))
                                                )
                                        )
                                )
                        // /techlevel set <targets> <amount> [xp|levels]
                        ).then(Commands.literal("set")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes((cmd) -> setTechLevel(cmd.getSource(), EntityArgument.getPlayers(cmd, "targets"), IntegerArgumentType.getInteger(cmd, "amount"), Type.XP)).then(Commands.literal("xp")
                                                        .executes((cmd) -> setTechLevel(cmd.getSource(), EntityArgument.getPlayers(cmd, "targets"), IntegerArgumentType.getInteger(cmd, "amount"), Type.XP))
                                                ).then(Commands.literal("levels")
                                                        .executes((cmd) -> setTechLevel(cmd.getSource(), EntityArgument.getPlayers(cmd, "targets"), IntegerArgumentType.getInteger(cmd, "amount"), Type.LEVELS))
                                                )
                                        )
                                )
                        // /techlevel get targets <targets> [xp|levels]
                        ).then(Commands.literal("get")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.literal("xp")
                                                .executes((cmd) -> getTechLevel(cmd.getSource(), EntityArgument.getPlayer(cmd, "target"), Type.XP))
                                        ).then(Commands.literal("levels")
                                                .executes((cmd) -> getTechLevel(cmd.getSource(), EntityArgument.getPlayer(cmd, "target"), Type.LEVELS))
                                        )
                                )
                        )
        );

        dispatcher.register(Commands.literal("techlevel").redirect(literalCommandNode));
    }

    // region Commands
    private static int addTechLevel(CommandSource src, Collection<? extends ServerPlayerEntity> targets, int amount, Type type) {
        for(ServerPlayerEntity target : targets) {
            type.add.accept(target, amount);
        }

        if(targets.size() == 1) {
            src.sendSuccess(new TranslationTextComponent("commands." + Expeditech.MODID + ".techlevel.add." + type.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            src.sendSuccess(new TranslationTextComponent("commands." + Expeditech.MODID + ".techlevel.add." + type.name + ".success.multiple", amount, targets.size()), true);
        }

        return targets.size();
    }

    private static int setTechLevel(CommandSource src, Collection<? extends ServerPlayerEntity> targets, int amount, Type type) {
        for(ServerPlayerEntity target : targets) {
            type.set.accept(target, amount);
        }

        if(targets.size() == 1) {
            src.sendSuccess(new TranslationTextComponent("commands." + Expeditech.MODID + ".techlevel.set." + type.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            src.sendSuccess(new TranslationTextComponent("commands." + Expeditech.MODID + ".techlevel.set." + type.name + ".success.multiple", amount, targets.size()), true);
        }

        return targets.size();
    }

    private static int getTechLevel(CommandSource src, ServerPlayerEntity target, Type type) {
        int i = type.get.applyAsInt(target);
        src.sendSuccess(new TranslationTextComponent("commands." + Expeditech.MODID + ".techlevel.get." + type.name + ".success", target.getDisplayName(), i), true);

        return i;
    }
    // endregion

    enum Type {
        XP("xp", TechLevelUtils::addTechXp, TechLevelUtils::setTechXp, TechLevelUtils::getTechXp),
        LEVELS("levels", TechLevelUtils::addTechLevel, TechLevelUtils::setTechLevel, TechLevelUtils::getTechLevel);

        private final String name;
        private final BiConsumer<ServerPlayerEntity, Integer> add;
        private final BiConsumer<ServerPlayerEntity, Integer> set;
        private final ToIntFunction<ServerPlayerEntity> get;

        Type(String name, BiConsumer<ServerPlayerEntity, Integer> add, BiConsumer<ServerPlayerEntity, Integer> set, ToIntFunction<ServerPlayerEntity> get) {
            this.name = name;
            this.add = add;
            this.set = set;
            this.get = get;
        }
    }
}
