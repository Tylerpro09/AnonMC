package io.github.tylerpro09.anonmc.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Keeps aliases only for the current client session.
 * Real names are never changed in GameProfile/network identity.
 */
public final class AliasRegistry {
    private static final Map<UUID, String> ALIASES = new LinkedHashMap<>();
    private static final Map<String, String> NAME_TO_ALIAS = new LinkedHashMap<>();
    private static int nextIndex = 0;

    private AliasRegistry() {
    }

    public static synchronized String aliasFor(UUID uuid) {
        return ALIASES.computeIfAbsent(uuid, ignored -> newAlias());
    }

    public static synchronized String observe(PlayerInfo info) {
        if (info == null) {
            return "Anonimo";
        }
        return observe(info.getProfile());
    }

    public static synchronized String observe(GameProfile profile) {
        if (profile == null) {
            return "Anonimo";
        }

        String alias = aliasFor(profile.id());
        String realName = profile.name();
        if (realName != null && !realName.isBlank()) {
            NAME_TO_ALIAS.put(realName, alias);
        }
        return alias;
    }

    public static synchronized List<Map.Entry<String, String>> replacements() {
        List<Map.Entry<String, String>> copy = new ArrayList<>(NAME_TO_ALIAS.entrySet());
        // Replace long names first so "Alex123" is not partially replaced as "Alex".
        copy.sort(Comparator.comparingInt((Map.Entry<String, String> e) -> e.getKey().length()).reversed());
        return copy;
    }

    public static synchronized void clear() {
        ALIASES.clear();
        NAME_TO_ALIAS.clear();
        nextIndex = 0;
    }

    private static String newAlias() {
        int index = nextIndex++;
        return index == 0 ? "Anonimo" : "Anonimo" + index;
    }
}
