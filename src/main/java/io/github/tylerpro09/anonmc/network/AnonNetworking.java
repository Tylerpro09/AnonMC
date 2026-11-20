package io.github.tylerpro09.anonmc.network;

import io.github.tylerpro09.anonmc.server.ServerAliasManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;

public final class AnonNetworking {
    private AnonNetworking() {}

    public static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(AliasSyncPayload.TYPE, AliasSyncPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(HelloPayload.TYPE, HelloPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(HelloPayload.TYPE, (payload, context) -> {
            if (payload.protocolVersion() != HelloPayload.PROTOCOL_VERSION) {
                context.player().connection.disconnect(Component.literal(
                        "Incompatible AnonMC protocol. Server protocol: "
                                + HelloPayload.PROTOCOL_VERSION
                                + ", client protocol: "
                                + payload.protocolVersion()
                ));
                return;
            }

            ServerAliasManager.markClientModded(context.player());
        });
    }
}
