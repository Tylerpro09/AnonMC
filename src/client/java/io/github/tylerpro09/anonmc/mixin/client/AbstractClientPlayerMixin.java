package io.github.tylerpro09.anonmc.mixin.client;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces the official AnonMC anonymous skin while anonymousSkins is enabled.
 * The skin source is the Skindex skin selected by the project owner.
 */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    private static final String ANONMC_SKIN_URL =
            "https://www.minecraftskins.com/uploads/skins/2023/10/01/signo-de-pregunta-x2-22013399.png?v695";

    private static final Identifier ANONMC_SKIN_ID =
            Identifier.fromNamespaceAndPath("anonmc", "anonymous/question_skin");

    private static final PlayerSkin ANONMC_SKIN = PlayerSkin.insecure(
            new ClientAsset.DownloadedTexture(ANONMC_SKIN_ID, ANONMC_SKIN_URL),
            null,
            null,
            PlayerModelType.WIDE
    );

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private void anonmc$forceAnonymousSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        if (AnonConfig.anonymousSkins) {
            cir.setReturnValue(ANONMC_SKIN);
        }
    }
}
