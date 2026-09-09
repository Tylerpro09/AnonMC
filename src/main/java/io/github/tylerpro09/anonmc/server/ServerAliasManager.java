package io.github.tylerpro09.anonmc.server;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import io.github.tylerpro09.anonmc.network.AliasSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerAliasManager {
    private static final Map<UUID, String> ALIASES = new LinkedHashMap<>();
    private static final Set<UUID> MODDED_CLIENTS = ConcurrentHashMap.newKeySet();
    private static int nextIndex = 0;

    private ServerAliasManager() {}

    public static synchronized void onJoin(ServerPlayer player) {
        aliasFor(player.getUUID());
        broadcast(player);
        if (AnonConfig.requireClientMod) {
            player.getServer().execute(() -> player.getServer().execute(() -> {
                if (!MODDED_CLIENTS.contains(player.getUUID())) {
                    player.connection.disconnect(Component.literal("AnonMC is required on this server."));
                }
            }));
        }
    }

    public static synchronized void onLeave(UUID uuid) {
        MODDED_CLIENTS.remove(uuid);
    }

    public static synchronized String aliasFor(UUID uuid) {
        return ALIASES.computeIfAbsent(uuid, ignored -> {
            int index = nextIndex++;
            return index == 0 ? AnonConfig.aliasPrefix : AnonConfig.aliasPrefix + index;
        });
    }

    public static void markClientModded(UUID uuid) { MODDED_CLIENTS.add(uuid); }

    public static synchronized void broadcast(ServerPlayer cause) {
        if (!AnonConfig.sharedServerAliases || cause.getServer() == null) return;
        AliasSyncPayload payload = new AliasSyncPayload(Map.copyOf(ALIASES));
        for (ServerPlayer player : cause.getServer().getPlayerList().getPlayers()) {
            if (ServerPlayNetworking.canSend(player, AliasSyncPayload.TYPE)) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }
}
