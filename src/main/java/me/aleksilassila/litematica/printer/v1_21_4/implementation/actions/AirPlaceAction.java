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

public class AirPlaceAction extends InteractAction {
    private static final Minecraft mc = Minecraft.getInstance();
    /**
     * Set while a batch keeps the placement item in the off hand, so its placements share one pair of swaps.
     */
    private static boolean batchSwapped = false;

    public AirPlaceAction(PrinterPlacementContext context) {
        super(context);
    }

    /**
     * Moves the held item to the off hand for the air placements that follow, until {@link #endBatch()}.
     */
    public static void beginBatch() {
        if (batchSwapped || placesWithMainHand()) return;
        batchSwapped = swapHands();
    }

    public static void endBatch() {
        if (!batchSwapped) return;
        swapHands();
        batchSwapped = false;
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
        airPlace(pos);
        return InteractionResult.PASS;
    }

    private void airPlace(BlockPos pos) {
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

        boolean ownSwap = !batchSwapped;
        if (ownSwap) swapHands();

        InteractionHand hand = InteractionHand.OFF_HAND;
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
