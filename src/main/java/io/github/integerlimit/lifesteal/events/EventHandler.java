package io.github.integerlimit.lifesteal.events;

import io.github.integerlimit.lifesteal.LifeSteal;
import io.github.integerlimit.lifesteal.commands.ExtractHeartsCommand;
import io.github.integerlimit.lifesteal.commands.GetHeartsCommand;
import io.github.integerlimit.lifesteal.commands.SetMaxHeartsCommand;
import io.github.integerlimit.lifesteal.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
     * @param entity The entity. This is used for logging purposes and for determining the level.
     * @param pos The Block Pos of which the event occurred.
     * @param event The event. This is used to cancel.
     * @return Returns true if breaching spawn block protection.
     */
    private static boolean manageSpawnProt(@Nonnull Entity entity, @Nonnull BlockPos pos, @Nonnull Event event) {
        LifeSteal.getLogger().info("[SpawnBlockProtectionManager] Event " + event + " occurred");
        Level level = entity.getLevel();
        if (!level.dimension().equals(Level.OVERWORLD) || ServerConfig.getGeneralConfig().spawnProtectionRadius.get() == 0)
            return false;

        int posX = pos.getX();
        int posZ = pos.getZ();
        int worldX = level.getLevelData().getXSpawn();
        int worldZ = level.getLevelData().getZSpawn();
        int spawnProtection = ServerConfig.getGeneralConfig().spawnProtectionRadius.get();

        // Entity outside spawn protection
        if (posX > worldX + spawnProtection || posZ > worldZ + spawnProtection
                || posX < worldX - spawnProtection || posZ < worldZ - spawnProtection)
            return false;

        LifeSteal.getLogger().info("[SpawnBlockProtectionManager] Entity " + entity.getName() + " tried to edit spawn protection zone");

        if (event.isCancelable())
            event.setCanceled(true);
        else
            LifeSteal.getLogger().error("[SpawnBlockProtectionManager] Failed to cancel event " + event + " caused by entity " + entity.getName());

        return true;
    }

    // Generic test and cancel
    @SubscribeEvent
    public static void onBlockPlacedEvent(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() != null) {
            manageSpawnProt(event.getEntity(), event.getPos(), event);
        }
    }

    // Player specific. Stops player before even starts, and on the client too, resulting in no ghost item.
    @SubscribeEvent
    public static void onPlayerRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        if (manageSpawnProt(event.getEntity(), event.getPos(), event) && event.getLevel().isClientSide)
            event.getEntity().displayClientMessage(Component.translatable("error.spawn_block_protection")
                    .withStyle(ChatFormatting.RED), true);

    }

    // Generic test and cancel
    @SubscribeEvent
    public static void onBlockBrokenEvent(LivingDestroyBlockEvent event) {
        if (event.getEntity() != null){
            manageSpawnProt(event.getEntity(), event.getPos(), event);
        }
    }

    // Explosion Test and Cancel
    @SubscribeEvent
    public static void onBlockExplodedEvent(ExplosionEvent.Detonate event) {
        LifeSteal.getLogger().info("[ExplosionEventManager] Explosion Event Occurred");

        Level level = event.getLevel();

        if (!level.dimension().equals(Level.OVERWORLD))
            return;

        List<BlockPos> editList = event.getAffectedBlocks();
        List<BlockPos> iterateList =  new ArrayList<>(editList);
        for (BlockPos pos : iterateList) {
            int posX = pos.getX();
            int posZ = pos.getZ();
            int worldX = level.getLevelData().getXSpawn();
            int worldZ = level.getLevelData().getZSpawn();

            int spawnProtection = ServerConfig.getGeneralConfig().spawnProtectionRadius.get();

            // No need to check for air
            if (event.getLevel().getBlockState(pos).isAir()
                    || posX > worldX + spawnProtection || posZ > worldZ + spawnProtection
                    || posX < worldX - spawnProtection || posZ < worldZ - spawnProtection)
                continue;

            editList.remove(pos);
            LifeSteal.getLogger().info("[ExplosionEventManager] Removed " + pos + "from changed block list.");
        }
    }

    // Player specific, sends message
    @SubscribeEvent
    public static void onPlayerBlockBrokenEvent(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null)
            if (manageSpawnProt(event.getPlayer(), event.getPos(), event)){
                event.getPlayer().displayClientMessage(Component.translatable("error.spawn_block_protection")
                        .withStyle(ChatFormatting.RED), true);
            }
    }
}
