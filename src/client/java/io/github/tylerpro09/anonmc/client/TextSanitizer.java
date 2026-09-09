package io.github.tylerpro09.anonmc.client;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;

public final class TextSanitizer {
    private TextSanitizer() {}

    public static Component sanitize(Component original) {
        if (original == null || !AnonConfig.sanitizeChat) return original == null ? Component.empty() : original;
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) for (PlayerInfo info : connection.getOnlinePlayers()) AliasRegistry.observe(info);
        return sanitizeRecursive(original);
    }

    private static MutableComponent sanitizeRecursive(Component component) {
        String base = replaceKnown(component.getString());
        MutableComponent rebuilt = Component.literal(base).setStyle(component.getStyle());
        return rebuilt;
    }

    public static String replaceKnown(String input) {
        String out = input;
        for (Map.Entry<String,String> replacement : AliasRegistry.replacements()) {
            out = replaceIgnoreCase(out, replacement.getKey(), replacement.getValue());
        }
        return out;
    }

    private static String replaceIgnoreCase(String input, String target, String replacement) {
        if (input == null || target == null || target.isEmpty()) return input;
        String lowerInput = input.toLowerCase(java.util.Locale.ROOT);
        String lowerTarget = target.toLowerCase(java.util.Locale.ROOT);
        StringBuilder output = new StringBuilder(input.length());
        int cursor = 0, match;
        while ((match = lowerInput.indexOf(lowerTarget, cursor)) >= 0) {
            output.append(input, cursor, match).append(replacement);
            cursor = match + target.length();
        }
        return output.append(input, cursor, input.length()).toString();
    }
}
