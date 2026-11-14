package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MixinAccessorMultiPlayerGameMode {
    @Invoker("ensureHasSentCarriedItem")
    void syncSelectedSlot();
}
