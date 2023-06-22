package io.github.integerlimit.lifesteal.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AxolotlSwitcher extends Item {
    private Axolotl.Variant[] allVariants = null;

    public AxolotlSwitcher() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return super.use(level, player, hand);
    }

    @Override
    @NotNull
    public InteractionResult useOn(@NotNull UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    @NotNull
    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player, @NotNull LivingEntity entity, @NotNull InteractionHand hand) {
        if (!(entity instanceof Axolotl axolotl))
            return InteractionResult.FAIL;

        int originalVariant = axolotl.getVariant().getId();
        Axolotl.Variant[] variants = getAxolotlVariants();

        if (stack.getTag() != null && stack.getTag().contains("variant")) {
            // Mod it by length just in case...
            int nbtVariant = stack.getTag().getInt("variant") % variants.length;
            if (nbtVariant != originalVariant){
                axolotl.setVariant(variants[nbtVariant]);
                return InteractionResult.SUCCESS;
            }
        }

        int newVariant = (originalVariant + 1) % variants.length;

        axolotl.setVariant(variants[newVariant]);

        CompoundTag tag = stack.hasTag() ? stack.getTag().copy() : new CompoundTag();
        tag.putInt("variant", newVariant);

        ItemStack result = stack.copy();
        result.setTag(tag);

        player.setItemInHand(hand, result);

        return InteractionResult.SUCCESS;
    }

    private Axolotl.Variant[] getAxolotlVariants() {
        if (allVariants == null)
            allVariants = Axolotl.Variant.values();

        return allVariants;
    }
}
