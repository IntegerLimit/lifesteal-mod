package io.github.integerlimit.lifesteal.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class HeartItem extends Item {
    private final int minHearts;
    private final int maxHearts;
    public HeartItem(int minHearts, int maxHearts, Rarity rarity) {
        super(new Item.Properties().fireResistant().rarity(rarity).stacksTo(1));
        this.maxHearts = maxHearts;
        this.minHearts = minHearts;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack holding = player.getItemInHand(hand);

        if (!runHeartCode(player))
            return InteractionResultHolder.fail(holding);

        holding.shrink(1);
        player.containerMenu.broadcastChanges();


        return InteractionResultHolder.consume(holding);
    }

    // Return true if to consume
    private boolean runHeartCode (LivingEntity entity) {
        if (entity instanceof Player player){
            if (player.isCreative()){
                player.displayClientMessage(Component.translatable("message.hearts.creative").withStyle(ChatFormatting.YELLOW), true);
                return false;
            }

            if (player.getMaxHealth() < minHearts){
                player.displayClientMessage(Component.translatable("message.hearts.min").withStyle(ChatFormatting.YELLOW), true);
                return false;
            }
            if (player.getMaxHealth() >= maxHearts){
                player.displayClientMessage(Component.translatable("message.hearts.max").withStyle(ChatFormatting.RED), true);
                return false;
            }

            Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(player.getMaxHealth() + 2);
            player.heal(2);

            player.displayClientMessage(Component.translatable("message.hearts.add").withStyle(ChatFormatting.GREEN), true);

            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable("tooltip.heart.right_click").withStyle(ChatFormatting.AQUA));
        if (Screen.hasShiftDown()) {
            if (minHearts != 0)
                components.add(Component.translatable("tooltip.heart.min", minHearts).withStyle(ChatFormatting.RED));
            components.add(Component.translatable("tooltip.heart.max", maxHearts).withStyle(ChatFormatting.RED));
        }
        else
            components.add(Component.translatable("tooltip.heart.shift").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(stack, level, components, flag);
    }
}
