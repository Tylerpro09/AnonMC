package io.github.tylerpro09.anonmc.server;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import io.github.tylerpro09.anonmc.network.AliasSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerAliasManager {
    private static final Map<UUID, String> ALIASES = new LinkedHashMap<>();
    private static final Set<UUID> MODDED_CLIENTS = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Integer> REQUIRE_CHECK_TICKS = new ConcurrentHashMap<>();
    private static int nextIndex = 0;

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

        // Remove the mapping when the player leaves so reconnecting receives
        // a fresh alias. This prevents long-term correlation by alias.
        ALIASES.remove(uuid);

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
        return ALIASES.computeIfAbsent(uuid, ignored -> {
            int index = nextIndex++;
            return index == 0 ? AnonConfig.aliasPrefix : AnonConfig.aliasPrefix + index;
        });
    }

    public static synchronized void markClientModded(ServerPlayer player) {
        UUID uuid = player.getUUID();
        MODDED_CLIENTS.add(uuid);
        REQUIRE_CHECK_TICKS.remove(uuid);

        // A hello packet is the first reliable proof that the client has AnonMC.
        // Send a fresh authoritative snapshot immediately after the handshake.
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
