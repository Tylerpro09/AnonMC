package io.github.tylerpro09.anonmc.network;

import io.github.tylerpro09.anonmc.server.ServerAliasManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class AnonNetworking {
    private AnonNetworking() {}

    public static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(AliasSyncPayload.TYPE, AliasSyncPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(HelloPayload.TYPE, HelloPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(HelloPayload.TYPE, (payload, context) ->
                ServerAliasManager.markClientModded(context.player().getUUID()));
    }
}
