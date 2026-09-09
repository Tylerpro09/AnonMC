package io.github.tylerpro09.anonmc.network;

import io.github.tylerpro09.anonmc.AnonMc;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HelloPayload(String version) implements CustomPacketPayload {
    public static final Type<HelloPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AnonMc.MOD_ID, "hello"));
    public static final StreamCodec<FriendlyByteBuf, HelloPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeUtf(payload.version, 32),
            buf -> new HelloPayload(buf.readUtf(32))
    );
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
