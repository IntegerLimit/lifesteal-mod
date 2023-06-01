package io.github.integerlimit.lifesteal.items;

import io.github.integerlimit.lifesteal.LifeSteal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static io.github.integerlimit.lifesteal.LifeSteal.MODID;

public class ModItems {

    // Items registerer
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Heart values. Min is non-inclusive, Max is inclusive
    public static final int DECAYED_MIN = 0;
    public static final int DECAYED_MAX = 20;
    public static final int HEART_MIN = 20;
    public static final int HEART_MAX = 40;
    public static final int ULTIMATE_MIN = 40;
    public static final int ULTIMATE_MAX = 60;
    public static final int MAX_HEALTH = 60;

    // Items
    public static final RegistryObject<Item> DECAYED_HEART = REGISTER.register("decayed_heart",
            () -> new HeartItem(DECAYED_MIN, DECAYED_MAX, Rarity.UNCOMMON));
    public static final RegistryObject<Item> HEART = REGISTER.register("heart",
            () -> new HeartItem(HEART_MIN, HEART_MAX, Rarity.RARE));
    public static final RegistryObject<Item> ULTIMATE_HEART = REGISTER.register("ultimate_heart",
            () -> new HeartItem(ULTIMATE_MIN, ULTIMATE_MAX, Rarity.EPIC));

    // Heart lists
    public static final List<Triple<ItemStack, Integer, Integer>> HEARTS = new ArrayList<>();
    private static List<ItemStack> HEART_INDEX = null;

    public static void init(IEventBus eventBus) {
        // Register the Item Register to the event bus
        REGISTER.register(eventBus);
    }

    public static List<ItemStack> getHeartIndex() {
        if (HEART_INDEX != null)
            return HEART_INDEX;

        HEART_INDEX = new ArrayList<>(MAX_HEALTH + 1);

        // Setup Hearts List
        HEARTS.add(Triple.of(new ItemStack(DECAYED_HEART.get()), DECAYED_MIN, DECAYED_MAX));
        HEARTS.add(Triple.of(new ItemStack(HEART.get()), HEART_MIN, HEART_MAX));
        HEARTS.add(Triple.of(new ItemStack(ULTIMATE_HEART.get()), ULTIMATE_MIN, ULTIMATE_MAX));

        // Setup Heart Index
        HEART_INDEX.add(0, ItemStack.EMPTY);

        for (var triple : HEARTS) {
            // Non inclusive min value, inclusive max value
            for (int i = triple.getMiddle() + 1; i <= triple.getRight(); i++) {
                HEART_INDEX.add(i, triple.getLeft());
            }
        }

        // Log Heart Index
        LifeSteal.getLogger().info("[ModItems]: Setup Heart Index, values are: " + HEART_INDEX);

        return HEART_INDEX;
    }
}
