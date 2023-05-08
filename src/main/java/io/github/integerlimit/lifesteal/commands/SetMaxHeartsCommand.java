package io.github.integerlimit.lifesteal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collection;
import java.util.Objects;

public class SetMaxHeartsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setHearts").requires((source) -> source.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("hearts", IntegerArgumentType.integer(1))
                        .executes((command) -> setHearts(command.getSource(),
                                EntityArgument.getPlayers(command, "targets"),
                                IntegerArgumentType.getInteger(command, "hearts"))))));
    }

    private static int setHearts(CommandSourceStack commandSource, Collection<ServerPlayer> players, int hearts) {
        var iterator = players.iterator();
        for (var player: players) {
            if (player.isCreative() || player.isSpectator()){
                commandSource.sendFailure(Component.translatable("command.setHearts.isCreative", player.getName()));
                iterator.next();
            }
            else {
                // each heart is 2 health
                var health = hearts * 2;
                Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
                player.setHealth(health);

                commandSource.sendSuccess(Component.translatable("command.setHearts.success", iterator.next().getDisplayName(), hearts)
                        .withStyle(ChatFormatting.GREEN), true);
            }
        }
        return players.size();
    }
}
