package io.github.integerlimit.lifesteal.block;

import io.github.integerlimit.lifesteal.items.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static io.github.integerlimit.lifesteal.LifeSteal.MODID;

public class ModBlocks {
    // Blocks registerer
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> REVIVE_BLOCK = registerBlock("revive_block",
            ReviveBlock::new, new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Properties itemProperties) {
        RegistryObject<T> toReturn = REGISTER.register(name, block);
        registerBlockItem(name, toReturn, itemProperties);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, Item.Properties properties) {
        ModItems.REGISTER.register(name, () -> new BlockItem(block.get(),
                properties));
    }

    public static void init(IEventBus bus){
        // Register the Block Register to the event bus
        REGISTER.register(bus);
    }
}
