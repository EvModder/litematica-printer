package me.aleksilassila.litematica.printer.v1_21_4.implementation.actions;


import me.aleksilassila.litematica.printer.v1_21_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21_4.actions.PrepareLook;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

/*
 * @author IceTank
 * @since 08.03.2026
 */
public class UseItemActionImpl extends InteractAction {
    public UseItemActionImpl(PrinterPlacementContext context) {
        super(context);
    }
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    protected BlockPos getAffectedPos() {
        return context.hitResult.getBlockPos(); // The item is used on the block that was aimed at, not next to it
    }

    @Override
    protected InteractionResult interact(Minecraft client, LocalPlayer player, InteractionHand hand, BlockHitResult hitResult) {
        // Using an item aims with the player's own rotation, both on the client and in the packet the server raycasts with.
        float yRot = player.getYRot();
        float xRot = player.getXRot();
        float[] rotation = PrepareLook.getNeededRotations(player, hitResult.getLocation());
        player.setYRot(rotation[0]);
        player.setXRot(rotation[1]);
        InteractionResult result;
        try {
            result = client.gameMode.useItem(player, hand);
        } finally {
            player.setYRot(yRot);
            player.setXRot(xRot);
        }
        if (!result.consumesAction()) {
            if (PrinterConfig.PRINTER_DEBUG_LOG.getBooleanValue()) System.out.println("Failed to interact with block got " + result);
        }
        //? >=26.3 {
        /*mc.player.swing(InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, false);*/
        //?} else {
        mc.player.swing(InteractionHand.MAIN_HAND);
        //?}
        return result;
    }
}
