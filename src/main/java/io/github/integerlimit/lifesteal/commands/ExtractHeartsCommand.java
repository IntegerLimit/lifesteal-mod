package io.github.integerlimit.lifesteal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.integerlimit.lifesteal.LifeSteal;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

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
            if (player.isCreative() || player.isSpectator()){
                player.sendSystemMessage(Component.translatable("command.extract.fail_creative")
                        .withStyle(ChatFormatting.YELLOW));
                return 0;
            }

            // Each heart is 2 health
            int health = hearts * 2;

            List<ItemStack> stacksToAdd = new ArrayList<>();

            float playerHealth = player.getMaxHealth();

            // If amount we want to extract is less than or equal to our health
            if (playerHealth <= health) {
                player.sendSystemMessage(Component.translatable("command.extract.fail_hearts")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }

            for (int i = 0; i < hearts; i++) {
                if (playerHealth > 40)
                    stacksToAdd.add(new ItemStack(LifeSteal.ULTIMATE_HEART.get()));
                stacksToAdd.add(new ItemStack(LifeSteal.HEART.get()));
                playerHealth -= 2;
            }

            for (int i = 0; i < stacksToAdd.size(); ++i) {
                if (!player.addItem(stacksToAdd.get(i))){
                    player.sendSystemMessage(Component.translatable("command.extract.fail_space", i).withStyle(ChatFormatting.YELLOW));
                    return i;
                }
            }
            if (hearts == 1)
                player.sendSystemMessage(Component.translatable("command.extract.success_single").withStyle(ChatFormatting.GREEN));
            else
                player.sendSystemMessage(Component.translatable("command.extract.success_multiple", hearts).withStyle(ChatFormatting.GREEN));

            return hearts;
        }
        return 0;
    }
}
