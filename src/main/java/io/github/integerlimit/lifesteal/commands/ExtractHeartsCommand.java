package io.github.integerlimit.lifesteal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/* TODO
public class ExtractHeartsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setHearts")
                .then(Commands.argument("hearts", IntegerArgumentType.integer(1))
                        .executes((command) -> extractHearts(command.getSource(),
                                IntegerArgumentType.getInteger(command, "hearts")))));
    }
    private static int extractHearts(CommandSourceStack source, int hearts) {
        Entity entity = source.getEntity();
        if (entity instanceof Player player) {
            // Each heart is 2 health
            int health = hearts * 2;

            // If amount we want to extract is less than or equal to our health
            if (player.getMaxHealth() <= health) {

            }
        }
    }
}
 */
