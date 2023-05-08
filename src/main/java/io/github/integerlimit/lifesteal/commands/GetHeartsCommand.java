package io.github.integerlimit.lifesteal.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class GetHeartsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("getHearts")
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes((command) -> getHearts(command.getSource(),
                                EntityArgument.getPlayers(command, "targets")))));
    }

    private static int getHearts(CommandSourceStack commandSource, Collection<ServerPlayer> players) {
        var iterator = players.iterator();
        for (var player: players) {
            if (player.isCreative() || player.isSpectator()){
                commandSource.sendFailure(Component.translatable("command.getHearts.isCreative", player.getName()));
                iterator.next();
            }
            else {
                int health = (int) (player.getMaxHealth() / 2);
                commandSource.sendSuccess(Component.translatable("command.getHearts.success", iterator.next().getDisplayName(), health)
                        .withStyle(ChatFormatting.GREEN), true);
            }
        }
        return players.size();
    }
}
