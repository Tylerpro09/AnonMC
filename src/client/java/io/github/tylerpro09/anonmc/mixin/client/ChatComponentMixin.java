package io.github.tylerpro09.anonmc.mixin.client;

import io.github.tylerpro09.anonmc.client.TextSanitizer;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Censors known real usernames before chat/system messages are stored for rendering. */
@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @ModifyVariable(method = "addPlayerMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component anonmc$sanitizePlayerMessage(Component message) {
        return TextSanitizer.sanitize(message);
    }

    @ModifyVariable(method = "addServerSystemMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component anonmc$sanitizeServerSystemMessage(Component message) {
        return TextSanitizer.sanitize(message);
    }

    @ModifyVariable(method = "addClientSystemMessage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Component anonmc$sanitizeClientSystemMessage(Component message) {
        return TextSanitizer.sanitize(message);
    }
}
