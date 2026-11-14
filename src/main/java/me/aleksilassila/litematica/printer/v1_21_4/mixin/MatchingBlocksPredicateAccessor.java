package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate")
public interface MatchingBlocksPredicateAccessor {
    @Accessor("blocks")
    HolderSet<Block> getBlocks();
}
