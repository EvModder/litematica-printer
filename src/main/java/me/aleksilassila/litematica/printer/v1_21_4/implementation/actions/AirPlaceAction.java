package me.aleksilassila.litematica.printer.v1_21_4.implementation.actions;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import me.aleksilassila.litematica.printer.v1_21_4.mixin.MixinAccessorMultiPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.ItemStack;
//? <26.3 {
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
//?}
//? >=26.3 {
/*import net.minecraft.world.item.component.SwingAnimation;*/
//?}
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class AirPlaceAction extends InteractAction {
    private static final Minecraft mc = Minecraft.getInstance();
    /**
     * The hand a batch places with while it keeps the item swapped into it, so its placements share one pair of swaps.
     */
    private static @Nullable InteractionHand batchHand = null;

    public AirPlaceAction(PrinterPlacementContext context) {
        super(context);
    }

    private static InteractionHand otherHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    /**
     * Swaps the item to the other hand for the air placements that follow, until {@link #endBatch()}.
     *
     * @param holding The hand that holds the item
     */
    public static void beginBatch(InteractionHand holding) {
        if (batchHand != null || placesWithMainHand()) return;
        if (swapHands()) batchHand = otherHand(holding);
    }

    public static void endBatch() {
        if (batchHand == null) return;
        swapHands();
        batchHand = null;
    }

    private static boolean placesWithMainHand() {
        return mc.player != null && mc.hasSingleplayerServer() && mc.player.getAbilities().instabuild;
    }

    @Override
    protected InteractionResult interact(Minecraft client, LocalPlayer player, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.isInside() ? hitResult.getBlockPos() : hitResult.getBlockPos().relative(hitResult.getDirection());

        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        }
        airPlace(pos, hand);
        return InteractionResult.PASS;
    }

    /**
     * @param holding The hand that holds the item, unless a batch has swapped it already
     */
    private void airPlace(BlockPos pos, InteractionHand holding) {
        ClientPacketListener connection = mc.getConnection();
        MultiPlayerGameMode interactionManager = mc.gameMode;
        if (mc.player == null || connection == null || interactionManager == null) return;

        if (placesWithMainHand()) {
            BlockHitResult blockHitResult = new BlockHitResult(this.context.hitResult.getLocation(), this.context.hitResult.getDirection(), pos, true);
            mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHitResult);
            //? >=26.3 {
            /*mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);*/
            //?} else {
            mc.player.swing(InteractionHand.MAIN_HAND);
            //?}
            return;
        }

        // The placement goes out between two hand swaps, with the hand the item is swapped into.
        boolean ownSwap = batchHand == null;
        if (ownSwap) swapHands();

        InteractionHand hand = ownSwap ? otherHand(holding) : batchHand;
        BlockHitResult blockHitResult = new BlockHitResult(this.context.hitResult.getLocation(), this.context.hitResult.getDirection(), pos, true);
        interactionManager.useItemOn(mc.player, hand, blockHitResult);
        //? >=26.3 {
        /*mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);*/
        //?} else {
        mc.player.swing(InteractionHand.MAIN_HAND);
        connection.send(new ServerboundSwingPacket(hand));
        //?}
        if (ownSwap) swapHands();
    }

    /**
     * Swaps the main and off hand stacks on the server and mirrors it locally.
     * The raw SWAP_ITEM_WITH_OFFHAND packet swaps hands server-side only (ServerGamePacketListenerImpl
     * swaps the two hand stacks via setItemInHand), so the client inventory must be swapped in lockstep
     * or the useItemOn prediction runs against a stale offhand stack and the two sides drift apart
     * (the old offhand-eviction + slot-update-suppression approach; it dropped offhand items when the
     * inventory was full).
     *
     * @return True if the swap was sent
     */
    private static boolean swapHands() {
        LocalPlayer player = mc.player;
        ClientPacketListener connection = mc.getConnection();
        if (player == null || connection == null || mc.gameMode == null) return false;

        // The server swaps the slot it believes is selected, which is stale if the slot changed this tick.
        ((MixinAccessorMultiPlayerGameMode) mc.gameMode).syncSelectedSlot();

        ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offStack = player.getItemInHand(InteractionHand.OFF_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, offStack);
        player.setItemInHand(InteractionHand.OFF_HAND, mainStack);
        connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
        return true;
    }
}
