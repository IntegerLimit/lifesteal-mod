package io.github.integerlimit.lifesteal.block.entity;

import io.github.integerlimit.lifesteal.LifeSteal;
import io.github.integerlimit.lifesteal.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LifeSteal.MODID);

    public static final RegistryObject<BlockEntityType<ReviveBlockEntity>> REVIVE_BLOCK =
            BLOCK_ENTITIES.register("revive_block", () ->
                    BlockEntityType.Builder.of(ReviveBlockEntity::new,
                            ModBlocks.REVIVE_BLOCK.get()).build(null));

    public static void init(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
