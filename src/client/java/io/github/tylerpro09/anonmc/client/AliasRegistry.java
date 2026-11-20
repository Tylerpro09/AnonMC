package io.github.tylerpro09.anonmc.client;

import com.mojang.authlib.GameProfile;
import io.github.tylerpro09.anonmc.config.AnonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class AliasRegistry {
    private static final Map<UUID, String> ALIASES = new LinkedHashMap<>();
    private static final Map<String, String> NAME_TO_ALIAS = new LinkedHashMap<>();
    private static final Set<String> USED_LOCAL_ALIASES = new HashSet<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    private AliasRegistry() {}

    public static synchronized void replaceAll(Map<UUID, String> aliases) {
        ALIASES.clear();
        ALIASES.putAll(aliases);

        USED_LOCAL_ALIASES.clear();
        USED_LOCAL_ALIASES.addAll(aliases.values());

        // Rebuild name mappings lazily from the current PlayerInfo list so an
        // authoritative server remap cannot leave stale real-name -> alias pairs.
        NAME_TO_ALIAS.clear();
    }

    public static synchronized String aliasFor(UUID uuid) {
        return ALIASES.computeIfAbsent(uuid, ignored -> newLocalFallbackAlias());
    }

    private static String newLocalFallbackAlias() {
        for (int attempt = 0; attempt < 100; attempt++) {
            String alias = AnonConfig.aliasPrefix + (100000 + RANDOM.nextInt(900000));
            if (USED_LOCAL_ALIASES.add(alias)) {
                return alias;
            }
        }

        String alias;
        do {
            alias = AnonConfig.aliasPrefix + UUID.randomUUID().toString().substring(0, 8);
        } while (!USED_LOCAL_ALIASES.add(alias));
        return alias;
    }

    public static synchronized String observe(PlayerInfo info) {
        return info == null ? AnonConfig.aliasPrefix : observe(info.getProfile());
    }

    public static synchronized String observe(GameProfile profile) {
        if (profile == null) return AnonConfig.aliasPrefix;

        Minecraft minecraft = Minecraft.getInstance();
        if (!AnonConfig.anonymizeSelf
                && minecraft.player != null
                && minecraft.player.getUUID().equals(profile.id())) {
            return profile.name();
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
        copy.sort(Comparator.comparingInt(
                (Map.Entry<String, String> e) -> e.getKey().length()
        ).reversed());
        return copy;
    }

    public static synchronized void clear() {
        ALIASES.clear();
        NAME_TO_ALIAS.clear();
        USED_LOCAL_ALIASES.clear();
    }
}
