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
    public static boolean sanitizeChat = true;
    public static boolean sanitizeScoreboards = true;
    public static boolean sanitizeCommandSuggestions = true;

    private AnonConfig() {}

    public static void load() {
        Properties p = new Properties();
        if (Files.exists(FILE)) {
            try (var in = Files.newInputStream(FILE)) { p.load(in); } catch (IOException ignored) {}
        }
        anonymizeSelf = Boolean.parseBoolean(p.getProperty("anonymizeSelf", Boolean.toString(anonymizeSelf)));
        anonymousSkins = Boolean.parseBoolean(p.getProperty("anonymousSkins", Boolean.toString(anonymousSkins)));
        sharedServerAliases = Boolean.parseBoolean(p.getProperty("sharedServerAliases", Boolean.toString(sharedServerAliases)));
        requireClientMod = Boolean.parseBoolean(p.getProperty("requireClientMod", Boolean.toString(requireClientMod)));
        sanitizeChat = Boolean.parseBoolean(p.getProperty("sanitizeChat", Boolean.toString(sanitizeChat)));
        sanitizeScoreboards = Boolean.parseBoolean(p.getProperty("sanitizeScoreboards", Boolean.toString(sanitizeScoreboards)));
        sanitizeCommandSuggestions = Boolean.parseBoolean(p.getProperty("sanitizeCommandSuggestions", Boolean.toString(sanitizeCommandSuggestions)));
        save();
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("anonymizeSelf", Boolean.toString(anonymizeSelf));
        p.setProperty("anonymousSkins", Boolean.toString(anonymousSkins));
        p.setProperty("sharedServerAliases", Boolean.toString(sharedServerAliases));
        p.setProperty("requireClientMod", Boolean.toString(requireClientMod));
        p.setProperty("sanitizeChat", Boolean.toString(sanitizeChat));
        p.setProperty("sanitizeScoreboards", Boolean.toString(sanitizeScoreboards));
        p.setProperty("sanitizeCommandSuggestions", Boolean.toString(sanitizeCommandSuggestions));
        try {
            Files.createDirectories(FILE.getParent());
            try (var out = Files.newOutputStream(FILE)) { p.store(out, "AnonMC configuration - aliases are server-controlled and cannot be changed by clients"); }
        } catch (IOException ignored) {}
    }
}
