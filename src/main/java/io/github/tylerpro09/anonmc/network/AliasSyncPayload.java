package io.github.tylerpro09.anonmc.network;

import io.github.tylerpro09.anonmc.AnonMc;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public record AliasSyncPayload(Map<UUID, String> aliases) implements CustomPacketPayload {
    public static final Type<AliasSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AnonMc.MOD_ID, "alias_sync"));
    public static final StreamCodec<FriendlyByteBuf, AliasSyncPayload> CODEC = new StreamCodec<>() {
        @Override
        public AliasSyncPayload decode(FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Map<UUID, String> map = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) map.put(buf.readUUID(), buf.readUtf(64));
            return new AliasSyncPayload(map);
        }
        @Override
        public void encode(FriendlyByteBuf buf, AliasSyncPayload payload) {
            buf.writeVarInt(payload.aliases.size());
            payload.aliases.forEach((uuid, alias) -> { buf.writeUUID(uuid); buf.writeUtf(alias, 64); });
        }
    };
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
