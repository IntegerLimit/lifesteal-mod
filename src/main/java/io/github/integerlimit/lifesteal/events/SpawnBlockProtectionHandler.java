package io.github.integerlimit.lifesteal.events;

import io.github.integerlimit.lifesteal.LifeSteal;
import io.github.integerlimit.lifesteal.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SpawnBlockProtectionHandler {
    // Util Spawn Protection method: Used for all events apart from explosion
    /**
     * @param entity The entity. This is used for logging purposes and for determining the level.
     * @param pos The Block Pos of which the event occurred.
     * @param event The event. This is used to cancel.
     * @return Returns true if breaching spawn block protection.
     */
    private static boolean manageSpawnProtection(@Nonnull Entity entity, @Nonnull BlockPos pos, @Nonnull Event event) {
        LifeSteal.getLogger().debug("[SpawnBlockProtectionManager] Event " + event + " occurred");
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

    /* EVENT HANDLERS */
    // Generic test and cancel
    @SubscribeEvent
    public static void onBlockPlacedEvent(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() != null) {
            manageSpawnProtection(event.getEntity(), event.getPos(), event);
        }
    }

    // Player specific. Stops player before even starts, and on the client too, resulting in no ghost item.
    @SubscribeEvent
    public static void onPlayerRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        if (manageSpawnProtection(event.getEntity(), event.getPos(), event) && event.getLevel().isClientSide)
            // getEntity returns a Player
            event.getEntity().displayClientMessage(Component.translatable("error.spawn_block_protection")
                    .withStyle(ChatFormatting.RED), true);

    }

    // Item Use specific. aka buckets, flint & steel, etc.
    @SubscribeEvent
    public static void onFluidPlacedEvent(PlayerInteractEvent.RightClickItem event) {
        if (manageSpawnProtection(event.getEntity(), event.getPos(), event) && event.getLevel().isClientSide)
            // getEntity returns a Player
            event.getEntity().displayClientMessage(Component.translatable("error.spawn_block_protection")
                    .withStyle(ChatFormatting.RED), true);
    }

    // Generic test and cancel
    @SubscribeEvent
    public static void onBlockBrokenEvent(LivingDestroyBlockEvent event) {
        if (event.getEntity() != null){
            manageSpawnProtection(event.getEntity(), event.getPos(), event);
        }
    }

    // Explosion Test and Cancel
    @SubscribeEvent
    public static void onBlockExplodedEvent(ExplosionEvent.Detonate event) {
        LifeSteal.getLogger().debug("[ExplosionEventManager] Explosion Event Occurred");

        Level level = event.getLevel();

        if (!level.dimension().equals(Level.OVERWORLD) || ServerConfig.getGeneralConfig().spawnProtectionRadius.get() == 0)
            return;

        int worldX = level.getLevelData().getXSpawn();
        int worldZ = level.getLevelData().getZSpawn();
        int spawnProtection = ServerConfig.getGeneralConfig().spawnProtectionRadius.get();

        List<BlockPos> editList = event.getAffectedBlocks();
        List<BlockPos> iterateList =  new ArrayList<>(editList);
        for (BlockPos pos : iterateList) {
            int posX = pos.getX();
            int posZ = pos.getZ();

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
            if (manageSpawnProtection(event.getPlayer(), event.getPos(), event)){
                event.getPlayer().displayClientMessage(Component.translatable("error.spawn_block_protection")
                        .withStyle(ChatFormatting.RED), true);
            }
    }
}
