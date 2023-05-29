package io.github.integerlimit.lifesteal.events;

import io.github.integerlimit.lifesteal.LifeSteal;
import io.github.integerlimit.lifesteal.commands.ExtractHeartsCommand;
import io.github.integerlimit.lifesteal.commands.GetHeartsCommand;
import io.github.integerlimit.lifesteal.commands.SetMaxHeartsCommand;
import io.github.integerlimit.lifesteal.items.ModItems;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class EventHandler
{
    @SubscribeEvent
    public static void deathManager(LivingDeathEvent event) {
        LifeSteal.getLogger().info("[DeathManager] Living Death Event Triggered");
        // Checks if it was player. If it is, ports value to player variable.
        if (!(event.getEntity() instanceof Player player))
            return;

        LifeSteal.getLogger().info("[DeathManager] Is Player's death.");

        // Checks
        if (player.getMaxHealth() <= 0) {
            var health = player.getAttribute(Attributes.MAX_HEALTH);
            if (health != null)
                health.setBaseValue(0);
            return;
        }

        // Drop Heart
        List< ItemStack> heartIndex = ModItems.getHeartIndex();
        if (player.getMaxHealth() > heartIndex.size() - 1)
            // Block.popResource(player.level, deathPos, ModItems.HEART_INDEX.get(ModItems.HEART_INDEX.size()).copy());
            player.drop(heartIndex.get(heartIndex.size() - 1).copy(), true, false);

        else
            // Block.popResource(player.level, deathPos, ModItems.HEART_INDEX.get((int) player.getMaxHealth()).copy());
            player.drop(heartIndex.get((int) player.getMaxHealth()).copy(), true, false);

        LifeSteal.getLogger().info("[DeathManager] {} Spawned, from {} dying.", heartIndex.get((int) player.getMaxHealth()).copy(), player.getName());
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
