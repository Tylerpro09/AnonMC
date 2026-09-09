package io.github.tylerpro09.anonmc.client;

import io.github.tylerpro09.anonmc.network.AliasSyncPayload;
import io.github.tylerpro09.anonmc.network.HelloPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public final class AnonMcClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(AliasSyncPayload.TYPE, (payload, context) ->
                context.client().execute(() -> AliasRegistry.replaceAll(payload.aliases())));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            AliasRegistry.clear();
            if (ClientPlayNetworking.canSend(HelloPayload.TYPE)) {
                String version = FabricLoader.getInstance().getModContainer("anonmc").map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
                ClientPlayNetworking.send(new HelloPayload(version));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> AliasRegistry.clear());
    }
}
