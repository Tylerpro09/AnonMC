package io.github.tylerpro09.anonmc.mixin.client;

import io.github.tylerpro09.anonmc.client.TextSanitizer;
import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sanitizes names after vanilla applies scoreboard/team prefixes and suffixes.
 * This covers scoreboard-rendered player names and other team-formatted identity surfaces.
 */
@Mixin(PlayerTeam.class)
public abstract class PlayerTeamMixin {
    @Inject(method = "formatNameForTeam", at = @At("RETURN"), cancellable = true)
    private static void anonmc$sanitizeTeamName(
            Team team,
            Component name,
            CallbackInfoReturnable<MutableComponent> cir
    ) {
        if (!AnonConfig.sanitizeScoreboards) return;

        Component sanitized = TextSanitizer.sanitizeIdentity(cir.getReturnValue());
        cir.setReturnValue(sanitized.copy());
    }
}
