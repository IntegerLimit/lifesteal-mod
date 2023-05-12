package io.github.integerlimit.lifesteal.events;

import io.github.integerlimit.lifesteal.LifeSteal;
import io.github.integerlimit.lifesteal.commands.ExtractHeartsCommand;
import io.github.integerlimit.lifesteal.commands.GetHeartsCommand;
import io.github.integerlimit.lifesteal.commands.SetMaxHeartsCommand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler
{
    @SubscribeEvent
    public static void deathManager(LivingDeathEvent event) {
        LifeSteal.getLogger().info("[DeathManager] Living Death Event Triggered");
        // Checks if it was player. If it is, ports value to player variable.
        if (!(event.getEntity() instanceof Player player))
            return;

        LifeSteal.getLogger().info("[DeathManager] Is Player's death.");

        // Spawn heart
        var deathPos = player.getOnPos();

        // Use block's spawn method
        Block.popResource(player.level, deathPos, new ItemStack(LifeSteal.HEART.get()));

        LifeSteal.getLogger().info("[DeathManager] Heart Spawned, from {} dying.", player.getName());
    }


    @SubscribeEvent
    public static void playerCloneManager(PlayerEvent.Clone event) {
        LifeSteal.getLogger().info("[PlayerCloneManager] Player.Clone Event Triggered");
        if (!event.isWasDeath())
            return;

        LifeSteal.getLogger().info("[PlayerCloneManager] Player Died.");

        // Reduce Health
        var oldHealth = event.getOriginal().getMaxHealth();
        var newHealth = event.getEntity().getAttribute(Attributes.MAX_HEALTH);
        if (newHealth == null)
            return;

        double health = oldHealth - 2;

        newHealth.setBaseValue(health);

        event.getEntity().setHealth((float) health);

        LifeSteal.getLogger().info("[PlayerCloneManager] {} has had their health reduced by 2. New health is {}, used to be {}.", event.getEntity().getName(), (int) oldHealth, (int) health);
    }

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        SetMaxHeartsCommand.register(dispatcher);
        ExtractHeartsCommand.register(dispatcher);
        GetHeartsCommand.register(dispatcher);
    }
}
