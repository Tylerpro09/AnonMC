package io.github.tylerpro09.anonmc.mixin.client;

import io.github.tylerpro09.anonmc.client.AliasRegistry;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin {
    @Inject(method = "getNameForDisplay", at = @At("HEAD"), cancellable = true)
    private void anonmc$replaceTabName(PlayerInfo info, CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(Component.literal(AliasRegistry.observe(info)));
    }
}
