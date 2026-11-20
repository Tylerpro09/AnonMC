package io.github.tylerpro09.anonmc.client;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.Map;

public final class TextSanitizer {
    private TextSanitizer() {}

    public static Component sanitize(Component original) {
        if (original == null) return Component.empty();
        if (!AnonConfig.sanitizeChat) return original;

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            for (PlayerInfo info : connection.getOnlinePlayers()) {
                AliasRegistry.observe(info);
            }
        }

        String raw = original.getString();
        String sanitized = replaceKnown(raw);

        // Preserve the original rich component completely when no username was found.
        if (sanitized.equals(raw)) {
            return original;
        }

        // Only messages that actually leak a username are rebuilt.
        // Root styling is preserved; deeper interactive formatting may still be flattened.
        return Component.literal(sanitized).setStyle(original.getStyle());
    }

    public static String replaceKnown(String input) {
        if (input == null || input.isEmpty()) return input;

        String out = input;
        for (Map.Entry<String, String> replacement : AliasRegistry.replacements()) {
            out = replaceIgnoreCase(out, replacement.getKey(), replacement.getValue());
        }
        return out;
    }

    private static String replaceIgnoreCase(String input, String target, String replacement) {
        if (input == null || target == null || target.isEmpty()) return input;

        String lowerInput = input.toLowerCase(Locale.ROOT);
        String lowerTarget = target.toLowerCase(Locale.ROOT);
        StringBuilder output = new StringBuilder(input.length());
        int cursor = 0;
        int match;

        while ((match = lowerInput.indexOf(lowerTarget, cursor)) >= 0) {
            output.append(input, cursor, match).append(replacement);
            cursor = match + target.length();
        }

        return output.append(input, cursor, input.length()).toString();
    }
}
