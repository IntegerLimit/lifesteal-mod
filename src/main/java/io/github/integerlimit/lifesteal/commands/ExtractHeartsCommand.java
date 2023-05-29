package io.github.integerlimit.lifesteal.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.integerlimit.lifesteal.LifeSteal;
import io.github.integerlimit.lifesteal.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExtractHeartsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("extract")
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

            // If player is not on full health
            if (playerHealth != player.getHealth()) {
                player.sendSystemMessage(Component.translatable("command.extract.fail_not_full")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }

            // If amount we want to extract is less than or equal to our health
            if (playerHealth <= health) {
                player.sendSystemMessage(Component.translatable("command.extract.fail_hearts")
                        .withStyle(ChatFormatting.RED));
                return 0;
            }

            for (int i = 0; i < hearts; i++) {
                stacksToAdd.add(ModItems.getHeartIndex().get((int) playerHealth).copy());
                LifeSteal.getLogger().info("[ExtractHearts]: Ran, with i = {}, playerHealth = {}, spawned Item {}", i, playerHealth, ModItems.getHeartIndex().get((int) playerHealth).copy());
                playerHealth -= 2;
            }

            for (int i = 0; i < stacksToAdd.size(); ++i) {
                if (!player.addItem(stacksToAdd.get(i))){
                    player.sendSystemMessage(Component.translatable("command.extract.fail_space", i).withStyle(ChatFormatting.YELLOW));
                    removeHearts(player, player.getMaxHealth() - (i * 2));
                    return i;
                }
            }
            removeHearts(player, player.getMaxHealth() - health);
            if (hearts == 1)
                player.sendSystemMessage(Component.translatable("command.extract.success_single").withStyle(ChatFormatting.GREEN));
            else
                player.sendSystemMessage(Component.translatable("command.extract.success_multiple", hearts).withStyle(ChatFormatting.GREEN));

            return hearts;
        }
        return 0;
    }

    private static void removeHearts(Player player, float reduceTo) {
        Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(reduceTo);
        player.setHealth(reduceTo);
    }
}
