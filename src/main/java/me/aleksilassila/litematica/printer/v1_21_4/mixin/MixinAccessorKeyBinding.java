package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface MixinAccessorKeyBinding {
    @Accessor("key")
    InputConstants.Key getBoundKey();
}
