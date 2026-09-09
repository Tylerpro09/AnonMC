package io.github.tylerpro09.anonmc.client;

import com.mojang.authlib.GameProfile;
import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.*;

public final class AliasRegistry {
    private static final Map<UUID, String> ALIASES = new LinkedHashMap<>();
    private static final Map<String, String> NAME_TO_ALIAS = new LinkedHashMap<>();
    private static int nextIndex = 0;
    private AliasRegistry() {}

    public static synchronized void replaceAll(Map<UUID, String> aliases) {
        ALIASES.clear();
        ALIASES.putAll(aliases);
        nextIndex = aliases.size();
    }

    public static synchronized String aliasFor(UUID uuid) {
        return ALIASES.computeIfAbsent(uuid, ignored -> {
            int index = nextIndex++;
            return index == 0 ? AnonConfig.aliasPrefix : AnonConfig.aliasPrefix + index;
        });
    }

    public static synchronized String observe(PlayerInfo info) { return info == null ? AnonConfig.aliasPrefix : observe(info.getProfile()); }
    public static synchronized String observe(GameProfile profile) {
        if (profile == null) return AnonConfig.aliasPrefix;
        String alias = aliasFor(profile.id());
        if (profile.name() != null && !profile.name().isBlank()) NAME_TO_ALIAS.put(profile.name(), alias);
        return alias;
    }
    public static synchronized List<Map.Entry<String,String>> replacements() {
        List<Map.Entry<String,String>> copy = new ArrayList<>(NAME_TO_ALIAS.entrySet());
        copy.sort(Comparator.comparingInt((Map.Entry<String,String> e) -> e.getKey().length()).reversed());
        return copy;
    }
    public static synchronized void clear() { ALIASES.clear(); NAME_TO_ALIAS.clear(); nextIndex = 0; }
}
