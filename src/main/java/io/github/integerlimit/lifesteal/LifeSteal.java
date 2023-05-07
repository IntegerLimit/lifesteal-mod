package io.github.integerlimit.lifesteal;

import com.mojang.logging.LogUtils;
import io.github.integerlimit.lifesteal.events.EventHandler;
import io.github.integerlimit.lifesteal.items.HeartItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LifeSteal.MODID)
public class LifeSteal
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "lifesteal";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "lifesteal" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "lifesteal" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final Rarity legendaryRarity = Rarity.create("legendary", ChatFormatting.GOLD);
    public static final RegistryObject<Item> HEART = ITEMS.register("heart", () -> new HeartItem(0, 40, Rarity.EPIC));
    public static final RegistryObject<Item> ULTIMATE_HEART = ITEMS.register("ultimate_heart", () -> new HeartItem(40, 60, legendaryRarity));

    public LifeSteal()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register death handler
        forgeEventBus.register(EventHandler.class);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        forgeEventBus.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addToCreative);
    }

    private void addToCreative(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.COMBAT){
            event.accept(HEART);
            event.accept(ULTIMATE_HEART);
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
