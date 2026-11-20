package io.github.tylerpro09.anonmc.mixin.client;

import io.github.tylerpro09.anonmc.client.AliasRegistry;
import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void anonmc$replaceDisplayName(CallbackInfoReturnable<Component> cir) {
        Player self = (Player) (Object) this;

        Minecraft minecraft = Minecraft.getInstance();
        if (!AnonConfig.anonymizeSelf
                && minecraft.player != null
                && minecraft.player.getUUID().equals(self.getUUID())) {
            return;
        }

        cir.setReturnValue(Component.literal(AliasRegistry.aliasFor(self.getUUID())));
    }
}
