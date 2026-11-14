package me.aleksilassila.litematica.printer.v1_21_4.guides.interaction;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.SchematicBlockState;
import me.aleksilassila.litematica.printer.v1_21_4.mixin.AxeItemAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
//? >=26.3 {
/*import java.util.HashMap;
import me.aleksilassila.litematica.printer.v1_21_4.mixin.MatchingBlocksPredicateAccessor;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;*/
//?}

public class LogStrippingGuide extends InteractionGuide {
    static final Item[] AXE_ITEMS = new Item[]{
            Items.NETHERITE_AXE,
            Items.DIAMOND_AXE,
            Items.GOLDEN_AXE,
            Items.IRON_AXE,
            Items.STONE_AXE,
            Items.WOODEN_AXE
    };

    //? <26.3 {
    public static final Map<Block, Block> STRIPPED_BLOCKS = AxeItemAccessor.getStrippedBlocks();
    //?} else {
    /*public static final Map<Block, Block> STRIPPED_BLOCKS = getStrippedBlocks();

    private static Map<Block, Block> getStrippedBlocks() {
        Map<Block, Block> blocks = new HashMap<>();
        RuleBasedStateProvider provider = (RuleBasedStateProvider) AxeItemAccessor.getStrippingData().blockStateProvider().value();
        for (RuleBasedStateProvider.Rule rule : provider.rules()) {
            if (rule.ifTrue() instanceof MatchingBlocksPredicate predicate
                    && rule.then().value() instanceof CopyPropertiesProvider copy
                    && copy.source().value() instanceof SimpleStateProvider target) {
                ((MatchingBlocksPredicateAccessor) predicate).getBlocks().forEach(
                        block -> blocks.put(block.value(), target.state().getBlock()));
            }
        }
        return Map.copyOf(blocks);
    }*/
    //?}

    public LogStrippingGuide(SchematicBlockState state) {
        super(state);
    }

    @Override
    public boolean canExecute(LocalPlayer player) {
        if (!LitematicaMixinMod.STRIP_LOGS.getBooleanValue()) return false;

        if (!super.canExecute(player)) return false;

        Block strippingResult = STRIPPED_BLOCKS.get(currentState.getBlock());
        return strippingResult == targetState.getBlock();
    }

    @Override
    protected @NotNull List<ItemStack> getRequiredItems() {
        return Arrays.stream(AXE_ITEMS).map(ItemStack::new).toList();
    }
}
