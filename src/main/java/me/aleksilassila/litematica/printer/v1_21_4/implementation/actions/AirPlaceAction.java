package me.aleksilassila.litematica.printer.v1_21_4.implementation.actions;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class AirPlaceAction extends InteractAction {
    private final Minecraft mc = Minecraft.getInstance();
    public AirPlaceAction(PrinterPlacementContext context) {
        super(context);
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

        if (mc.hasSingleplayerServer() && mc.player.getAbilities().instabuild) {
            BlockHitResult blockHitResult = new BlockHitResult(this.context.hitResult.getLocation(), this.context.hitResult.getDirection(), pos, true);
            mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHitResult);
            mc.player.swing(InteractionHand.MAIN_HAND);
            return;
        }

        if (mc.player.getInventory().getItem(Inventory.SLOT_OFFHAND).getItem() instanceof BlockItem) {
            mc.gameMode.handleContainerInput(0, 45, 0, ContainerInput.PICKUP, mc.player);
            mc.getConnection().send(new ServerboundContainerClosePacket(mc.player.inventoryMenu.containerId));
        }
        connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));

        InteractionHand hand = InteractionHand.OFF_HAND;
        BlockHitResult blockHitResult = new BlockHitResult(this.context.hitResult.getLocation(), this.context.hitResult.getDirection(), pos, true);
        interactionManager.useItemOn(mc.player, hand, blockHitResult);
        mc.player.swing(InteractionHand.MAIN_HAND);
        connection.send(new ServerboundSwingPacket(hand));
        connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
    }
}
