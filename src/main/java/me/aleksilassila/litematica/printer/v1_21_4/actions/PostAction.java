package me.aleksilassila.litematica.printer.v1_21_4.actions;

import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import me.aleksilassila.litematica.printer.v1_21_4.implementation.PrinterPlacementContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class PostAction extends PrepareAction {
    public PostAction(PrinterPlacementContext context, LocalPlayer player) {
        super(context);
        this.pitch = player.getXRot();
        this.yaw = player.getYRot();
    }

    @Override
    public boolean send(Minecraft client, LocalPlayer player) {
        if (context.canStealth) {
            ServerboundMovePlayerPacket.Rot packet = new ServerboundMovePlayerPacket.Rot(this.yaw, this.pitch, player.onGround(), player.horizontalCollision);

            if (PrinterConfig.ROTATE_PLAYER.getBooleanValue()) {
                player.setYRot(this.yaw);
                player.setXRot(this.pitch);
            }

            player.connection.send(packet);
        }
        return true;
    }
}
