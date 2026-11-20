package io.github.tylerpro09.anonmc.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AnonConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("anonmc.properties");

    /**
     * Alias names are intentionally not configurable.
     * The authoritative server assigns Anonimo, Anonimo1, Anonimo2...
     * Clients never submit or choose their alias.
     */
    public static final String aliasPrefix = "Anonimo";

    public static boolean anonymizeSelf = true;
    public static boolean anonymousSkins = true;
    public static boolean sharedServerAliases = true;
    public static boolean requireClientMod = false;
    public static int requireClientModTimeoutTicks = 100;
    public static boolean sanitizeChat = true;
    public static boolean sanitizeScoreboards = true;
    public static boolean sanitizeCommandSuggestions = true;

    private AnonConfig() {}

    public static void load() {
        Properties p = new Properties();

        if (Files.exists(FILE)) {
            try (var in = Files.newInputStream(FILE)) {
                p.load(in);
            } catch (IOException ignored) {
            }
        }

        anonymizeSelf = readBoolean(p, "anonymizeSelf", anonymizeSelf);
        anonymousSkins = readBoolean(p, "anonymousSkins", anonymousSkins);
        sharedServerAliases = readBoolean(p, "sharedServerAliases", sharedServerAliases);
        requireClientMod = readBoolean(p, "requireClientMod", requireClientMod);
        requireClientModTimeoutTicks = clamp(
                readInt(p, "requireClientModTimeoutTicks", requireClientModTimeoutTicks),
                20,
                1200
        );
        sanitizeChat = readBoolean(p, "sanitizeChat", sanitizeChat);
        sanitizeScoreboards = readBoolean(p, "sanitizeScoreboards", sanitizeScoreboards);
        sanitizeCommandSuggestions = readBoolean(
                p,
                "sanitizeCommandSuggestions",
                sanitizeCommandSuggestions
        );

        save();
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("anonymizeSelf", Boolean.toString(anonymizeSelf));
        p.setProperty("anonymousSkins", Boolean.toString(anonymousSkins));
        p.setProperty("sharedServerAliases", Boolean.toString(sharedServerAliases));
        p.setProperty("requireClientMod", Boolean.toString(requireClientMod));
        p.setProperty("requireClientModTimeoutTicks", Integer.toString(requireClientModTimeoutTicks));
        p.setProperty("sanitizeChat", Boolean.toString(sanitizeChat));
        p.setProperty("sanitizeScoreboards", Boolean.toString(sanitizeScoreboards));
        p.setProperty("sanitizeCommandSuggestions", Boolean.toString(sanitizeCommandSuggestions));

        try {
            Files.createDirectories(FILE.getParent());
            try (var out = Files.newOutputStream(FILE)) {
                p.store(
                        out,
                        "AnonMC configuration - aliases are server-controlled and cannot be changed by clients"
                );
            }
        } catch (IOException ignored) {
        }
    }

    private static boolean readBoolean(Properties p, String key, boolean fallback) {
        String value = p.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value.trim());
    }

    private static int readInt(Properties p, String key, int fallback) {
        String value = p.getProperty(key);
        if (value == null) return fallback;

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
