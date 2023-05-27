package io.github.integerlimit.lifesteal.events;

import io.github.integerlimit.lifesteal.LifeSteal;
import io.github.integerlimit.lifesteal.commands.ExtractHeartsCommand;
import io.github.integerlimit.lifesteal.commands.GetHeartsCommand;
import io.github.integerlimit.lifesteal.commands.SetMaxHeartsCommand;
import io.github.integerlimit.lifesteal.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

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


    /**
     * @param entity The entity of the event. This is different for different events. This is needed to determine the dimension.
     * @param event The event. This is used to cancel, and to get the level spawn, as well as the event pos.
     * @return Returns true if breaching spawn block protection.
     */
    private static boolean manageSpawnProt(@Nonnull Entity entity, @Nonnull BlockEvent event) {
        LifeSteal.getLogger().info("BlockEvent occurred");
        if (!entity.getLevel().dimension().equals(Level.OVERWORLD) || ServerConfig.getGeneralConfig().spawnProtectionRadius.get() == 0)
            return false;

        int posX = event.getPos().getX();
        int posZ = event.getPos().getZ();
        int worldX = event.getLevel().getLevelData().getXSpawn();
        int worldZ = event.getLevel().getLevelData().getZSpawn();
        int spawnProtection = 16;
        if (posX > worldX + spawnProtection || posZ > worldZ + spawnProtection
                || posX < worldX - spawnProtection || posZ < worldZ - spawnProtection) {
            if (event.isCancelable())
                event.setCanceled(true);
            else
                LifeSteal.getLogger().error("[SpawnBlockProtectionManager] Failed to cancel event " + event + " caused by entity " + entity.getName());
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onBlockBrokenEvent(BlockEvent.BreakEvent event) {
        if (manageSpawnProt(event.getPlayer(), event))
            event.getPlayer().displayClientMessage(Component.translatable("error.spawn_block_protection", ServerConfig.getGeneralConfig().spawnProtectionRadius)
                    .withStyle(ChatFormatting.RED), true);
    }

    @SubscribeEvent
    public static void onBlockBrokenEvent(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() != null)
            if (manageSpawnProt(event.getEntity(), event) && event.getEntity() instanceof Player player)
                player.displayClientMessage(Component.translatable("error.spawn_block_protection", ServerConfig.getGeneralConfig().spawnProtectionRadius)
                        .withStyle(ChatFormatting.RED), true);
    }
}
