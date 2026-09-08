package io.github.tylerpro09.anonmc.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public final class AnonMcClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> AliasRegistry.clear());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> AliasRegistry.clear());
    }
}
