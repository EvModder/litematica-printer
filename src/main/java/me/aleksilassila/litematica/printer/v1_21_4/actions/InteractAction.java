package me.aleksilassila.litematica.printer.v1_21_4.actions;

import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.Printer;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;

abstract public class InteractAction extends Action {
    public final PrinterPlacementContext context;

    public InteractAction(PrinterPlacementContext context) {
        this.context = context;
    }

    protected abstract InteractionResult interact(Minecraft client, LocalPlayer player, InteractionHand hand, BlockHitResult hitResult);

    /**
     * @return The position this interaction changes, which the printer leaves alone until the server has answered
     */
    protected BlockPos getAffectedPos() {
        return context.hitResult.isInside() ? context.hitResult.getBlockPos() : context.hitResult.getBlockPos().relative(context.hitResult.getDirection());
    }

    @Override
    public boolean send(Minecraft client, LocalPlayer player) {
        interact(client, player, InteractionHand.MAIN_HAND, context.hitResult);

        if (LitematicaMixinMod.DEBUG)
            System.out.println("InteractAction.send: Blockpos: " + context.getBlockPos() + " Side: " + context.getDirection() + " HitPos: " + context.getHitPos());
        Printer.addTimeout(getAffectedPos());
        return true;
    }

    @Override
    public String toString() {
        return "InteractAction{" +
                "context=" + context +
                '}';
    }
}
