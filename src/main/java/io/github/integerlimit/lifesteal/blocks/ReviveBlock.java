package io.github.integerlimit.lifesteal.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class ReviveBlock extends Block {
    public ReviveBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .sound(SoundType.METAL).strength(6f));
    }
}
