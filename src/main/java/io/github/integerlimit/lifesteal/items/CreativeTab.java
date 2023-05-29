package io.github.integerlimit.lifesteal.items;

import io.github.integerlimit.lifesteal.LifeSteal;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.integerlimit.lifesteal.LifeSteal.MODID;

@Mod.EventBusSubscriber(modid = LifeSteal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTab {
    public static CreativeModeTab LIFESTEAL_TAB;
    @SubscribeEvent
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        LIFESTEAL_TAB = event.registerCreativeModeTab(new ResourceLocation(MODID, "lifesteal"),
                builder -> builder.icon(() -> new ItemStack(ModItems.HEART.get()))
                        .title(Component.translatable("creativemodetab.lifesteal"))
        );
    }
}
