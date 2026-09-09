package io.github.tylerpro09.anonmc;

import io.github.tylerpro09.anonmc.config.AnonConfig;
import io.github.tylerpro09.anonmc.network.AnonNetworking;
import io.github.tylerpro09.anonmc.server.ServerAliasManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class AnonMc implements ModInitializer {
    public static final String MOD_ID = "anonmc";

    @Override
    public void onInitialize() {
        AnonConfig.load();
        AnonNetworking.registerPayloads();
        AnonNetworking.registerServerReceivers();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                ServerAliasManager.onJoin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                ServerAliasManager.onLeave(handler.getPlayer().getUUID()));
        ServerTickEvents.END_SERVER_TICK.register(ServerAliasManager::tick);
    }
}
