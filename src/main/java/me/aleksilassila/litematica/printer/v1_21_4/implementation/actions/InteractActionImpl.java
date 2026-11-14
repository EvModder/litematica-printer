package me.aleksilassila.litematica.printer.v1_21_4.implementation.actions;

import me.aleksilassila.litematica.printer.v1_21_4.actions.InteractAction;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public class InteractActionImpl extends InteractAction {
    public InteractActionImpl(PrinterPlacementContext context) {
        super(context);
    }
    private final Minecraft mc = Minecraft.getInstance();
    @Override
    protected InteractionResult interact(Minecraft client, LocalPlayer player, InteractionHand hand, BlockHitResult hitResult) {
        InteractionResult result = client.gameMode.useItemOn(player, hand, hitResult);
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
