package io.github.integerlimit.lifesteal.block.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReviveBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler handler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Amount of hearts to take away. -1 means that one heart will be taken away (put in 5 hearts, person revived with 4)
    private final int compromise = -1;


    public ReviveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REVIVE_BLOCK.get(), pos, state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("gui.lifesteal.revive_block.title").withStyle(ChatFormatting.AQUA);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return null;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return lazyItemHandler.cast();

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> handler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", handler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        handler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops() {
        if (this.level == null)
            return;

        SimpleContainer inventory = new SimpleContainer(handler.getSlots());
        for (int i = 0; i < handler.getSlots(); i++) {
            inventory.setItem(i, handler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    /**
     * @param entity The entity
     * @return Returns the number of health to spawn player with
     */
    private int removeHearts(ReviveBlockEntity entity) {
        // Get Heart Counts
        int decayed_count = entity.handler.getStackInSlot(0).getCount();
        int normal_count = entity.handler.getStackInSlot(1).getCount();
        int ultimate_count = entity.handler.getStackInSlot(2).getCount();

        // Remove all items
        entity.handler.setStackInSlot(0, ItemStack.EMPTY);
        entity.handler.setStackInSlot(1, ItemStack.EMPTY);
        entity.handler.setStackInSlot(2, ItemStack.EMPTY);

        return (decayed_count + normal_count + ultimate_count + compromise) * 2;
    }

    // Must revive person with at least 1 heart
    private boolean canPerformRevive(ReviveBlockEntity entity) {
        // Get Heart Counts
        int decayed_count = entity.handler.getStackInSlot(0).getCount();
        int normal_count = entity.handler.getStackInSlot(1).getCount();
        int ultimate_count = entity.handler.getStackInSlot(2).getCount();

        return (decayed_count + normal_count + ultimate_count + compromise) > 1;
    }

    /* May be needed later if a tick method is needed
    public static void tick(Level level, BlockPos pos, BlockState state, ReviveBlockEntity entity) {
        if(level.isClientSide()) {
            return;
        }

        if(hasRecipe(entity)) {
            entity.progress++;
            setChanged(level, pos, state);

            if(pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity);
            }
        } else {
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }
    */
}
