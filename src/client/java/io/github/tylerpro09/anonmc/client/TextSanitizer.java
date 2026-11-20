package io.github.tylerpro09.anonmc.client;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;
import java.util.Map;

public final class TextSanitizer {
    private TextSanitizer() {}

    public static Component sanitize(Component original) {
        if (original == null) return Component.empty();
        if (!AnonConfig.sanitizeChat) return original;
        return sanitizeIdentity(original);
    }

    /**
     * Sanitizes only known player identities and preserves per-segment style,
     * click events, hover events and colors by rebuilding from the flattened
     * styled component stream.
     */
    public static Component sanitizeIdentity(Component original) {
        if (original == null) return Component.empty();

        refreshKnownPlayers();

        String raw = original.getString();
        String sanitized = replaceKnown(raw);
        if (sanitized.equals(raw)) {
            return original;
        }

        MutableComponent rebuilt = Component.empty();
        for (Component part : original.toFlatList()) {
            String partText = part.getString();
            String replaced = replaceKnown(partText);
            if (!replaced.isEmpty()) {
                rebuilt.append(Component.literal(replaced).setStyle(part.getStyle()));
            }
        }

        return rebuilt;
    }

    private static void refreshKnownPlayers() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;

        for (PlayerInfo info : connection.getOnlinePlayers()) {
            AliasRegistry.observe(info);
        }
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
