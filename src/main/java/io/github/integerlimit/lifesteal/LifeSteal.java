package io.github.integerlimit.lifesteal;

import com.mojang.logging.LogUtils;
import io.github.integerlimit.lifesteal.blocks.ModBlocks;
import io.github.integerlimit.lifesteal.config.ServerConfig;
import io.github.integerlimit.lifesteal.events.EventHandler;
import io.github.integerlimit.lifesteal.events.SpawnBlockProtectionHandler;
import io.github.integerlimit.lifesteal.items.CustomCreativeModeTab;
import io.github.integerlimit.lifesteal.items.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LifeSteal.MODID)
public class LifeSteal
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "lifesteal";

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public LifeSteal()
    {
        // Get buses for registering
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register generic event handler
        forgeEventBus.register(EventHandler.class);

        // Register SpawnBlockProtectionHandler
        forgeEventBus.register(SpawnBlockProtectionHandler.class);

        // Register Creative Tab adder
        modEventBus.addListener(this::addToCreative);

        // Items
        ModItems.init(modEventBus);

        // Blocks
        ModBlocks.init(modEventBus);

        // Register Config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.getGeneralSpec());
    }

    public void addToCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CustomCreativeModeTab.LIFESTEAL_TAB) {
            // Items
            event.accept(ModItems.DECAYED_HEART);
            event.accept(ModItems.HEART);
            event.accept(ModItems.ULTIMATE_HEART);

            // Blocks
            event.accept(ModBlocks.REVIVE_BLOCK);
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
