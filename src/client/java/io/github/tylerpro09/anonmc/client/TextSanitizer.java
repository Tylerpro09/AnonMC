package io.github.tylerpro09.anonmc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.Map;

public final class TextSanitizer {
    private TextSanitizer() {
    }

    public static Component sanitize(Component original) {
        if (original == null) {
            return Component.empty();
        }

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            for (PlayerInfo info : connection.getOnlinePlayers()) {
                AliasRegistry.observe(info);
            }
        }

        String raw = original.getString();
        String sanitized = raw;

        for (Map.Entry<String, String> replacement : AliasRegistry.replacements()) {
            sanitized = replaceIgnoreCase(sanitized, replacement.getKey(), replacement.getValue());
        }

        if (sanitized.equals(raw)) {
            return original;
        }

        // Rebuild only messages that actually contained a real username.
        // The root style is preserved; deeply nested hover/click formatting may be flattened.
        return Component.literal(sanitized).setStyle(original.getStyle());
    }

    private static String replaceIgnoreCase(String input, String target, String replacement) {
        if (input == null || target == null || target.isEmpty()) {
            return input;
        }

        String lowerInput = input.toLowerCase(java.util.Locale.ROOT);
        String lowerTarget = target.toLowerCase(java.util.Locale.ROOT);
        StringBuilder output = new StringBuilder(input.length());
        int cursor = 0;
        int match;

        while ((match = lowerInput.indexOf(lowerTarget, cursor)) >= 0) {
            output.append(input, cursor, match);
            output.append(replacement);
            cursor = match + target.length();
        }
        output.append(input, cursor, input.length());
        return output.toString();
    }
}
