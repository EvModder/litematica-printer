package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import net.minecraft.world.level.block.Block;
//? <26.3 {
import net.minecraft.world.item.AxeItem;
//?} else {
/*import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.core.component.BlockTransformer;
import org.spongepowered.asm.mixin.gen.Invoker;*/
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * This class apparently fixes an issue with Quilt.
 */
//? <26.3 {
@Mixin(AxeItem.class)
//?} else {
/*@Mixin(BlockTransformers.class)*/
//?}
public interface AxeItemAccessor {
    //? <26.3 {
    @Accessor("STRIPPABLES")
    static Map<Block, Block> getStrippedBlocks() {
        throw new AssertionError("Untransformed @Accessor");
    }
    //?} else {
    /*@Invoker("axeStrippables")
    static BlockTransformer.BlockTransformData getStrippingData() {
        throw new AssertionError("Untransformed @Invoker");
    }*/
    //?}

}
