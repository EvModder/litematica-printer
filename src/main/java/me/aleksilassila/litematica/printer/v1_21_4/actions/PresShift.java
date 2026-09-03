package me.aleksilassila.litematica.printer.v1_21_4.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.player.Input;

public class PresShift extends Action {
    @Override
    public boolean send(Minecraft client, LocalPlayer player) {
        Input input = player.input.keyPresses;
        player.input.keyPresses = new Input(input.forward(), input.backward(), input.left(), input.right(), input.jump(), true, input.sprint());
        player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY));
        return true;
    }
}
