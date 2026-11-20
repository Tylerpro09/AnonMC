package io.github.tylerpro09.anonmc.server;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import io.github.tylerpro09.anonmc.network.AliasSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerAliasManager {
    private static final Map<UUID, String> ALIASES = new LinkedHashMap<>();
    private static final Set<String> USED_ALIASES = new HashSet<>();
    private static final Set<UUID> MODDED_CLIENTS = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Integer> REQUIRE_CHECK_TICKS = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    private ServerAliasManager() {}

    public static synchronized void onJoin(ServerPlayer player) {
        aliasFor(player.getUUID());

        MinecraftServer server = player.level().getServer();
        if (server != null) {
            broadcast(server);
        }

        if (AnonConfig.requireClientMod) {
            REQUIRE_CHECK_TICKS.put(player.getUUID(), AnonConfig.requireClientModTimeoutTicks);
        }
    }

    public static synchronized void onLeave(MinecraftServer server, UUID uuid) {
        MODDED_CLIENTS.remove(uuid);
        REQUIRE_CHECK_TICKS.remove(uuid);

        String oldAlias = ALIASES.remove(uuid);
        if (oldAlias != null) {
            USED_ALIASES.remove(oldAlias);
        }

        if (server != null) {
            broadcast(server);
        }
    }

    public static void tick(MinecraftServer server) {
        if (!AnonConfig.requireClientMod || REQUIRE_CHECK_TICKS.isEmpty()) return;

        for (Map.Entry<UUID, Integer> entry : REQUIRE_CHECK_TICKS.entrySet()) {
            UUID uuid = entry.getKey();

            if (MODDED_CLIENTS.contains(uuid)) {
                REQUIRE_CHECK_TICKS.remove(uuid);
                continue;
            }

            int left = entry.getValue() - 1;
            if (left > 0) {
                REQUIRE_CHECK_TICKS.put(uuid, left);
                continue;
            }

            REQUIRE_CHECK_TICKS.remove(uuid);
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                player.connection.disconnect(Component.literal(
                        "AnonMC is required on this server. Install a compatible AnonMC client."
                ));
            }
        }
    }

    public static synchronized String aliasFor(UUID uuid) {
        return ALIASES.computeIfAbsent(uuid, ignored -> newUniqueAlias());
    }

    private static String newUniqueAlias() {
        for (int attempt = 0; attempt < 100; attempt++) {
            String alias = AnonConfig.aliasPrefix + (100000 + RANDOM.nextInt(900000));
            if (USED_ALIASES.add(alias)) {
                return alias;
            }
        }

        // Extremely unlikely fallback if random collisions happen repeatedly.
        String alias;
        do {
            alias = AnonConfig.aliasPrefix + UUID.randomUUID().toString().substring(0, 8);
        } while (!USED_ALIASES.add(alias));
        return alias;
    }

    public static synchronized void markClientModded(ServerPlayer player) {
        UUID uuid = player.getUUID();
        MODDED_CLIENTS.add(uuid);
        REQUIRE_CHECK_TICKS.remove(uuid);
        syncTo(player);
    }

    public static synchronized void syncTo(ServerPlayer player) {
        if (!AnonConfig.sharedServerAliases) return;

        if (ServerPlayNetworking.canSend(player, AliasSyncPayload.TYPE)) {
            ServerPlayNetworking.send(player, new AliasSyncPayload(Map.copyOf(ALIASES)));
        }
    }

    public static synchronized void broadcast(MinecraftServer server) {
        if (!AnonConfig.sharedServerAliases || server == null) return;

        AliasSyncPayload payload = new AliasSyncPayload(Map.copyOf(ALIASES));
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (ServerPlayNetworking.canSend(player, AliasSyncPayload.TYPE)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }
}
