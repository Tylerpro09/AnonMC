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
    private static final int MAX_ENTRIES = 2048;
    private static final int MAX_ALIAS_LENGTH = 32;

    public static final Type<AliasSyncPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(AnonMc.MOD_ID, "alias_sync"));

    public AliasSyncPayload {
        if (aliases.size() > MAX_ENTRIES) {
            throw new IllegalArgumentException("Too many AnonMC aliases: " + aliases.size());
        }
        aliases = Map.copyOf(aliases);
    }

    public static final StreamCodec<FriendlyByteBuf, AliasSyncPayload> CODEC = new StreamCodec<>() {
        @Override
        public AliasSyncPayload decode(FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            if (size < 0 || size > MAX_ENTRIES) {
                throw new IllegalArgumentException("Invalid AnonMC alias count: " + size);
            }

            Map<UUID, String> map = new LinkedHashMap<>(size);
            for (int i = 0; i < size; i++) {
                map.put(buf.readUUID(), buf.readUtf(MAX_ALIAS_LENGTH));
            }
            return new AliasSyncPayload(map);
        }

        @Override
        public void encode(FriendlyByteBuf buf, AliasSyncPayload payload) {
            if (payload.aliases.size() > MAX_ENTRIES) {
                throw new IllegalArgumentException("Too many AnonMC aliases: " + payload.aliases.size());
            }

            buf.writeVarInt(payload.aliases.size());
            payload.aliases.forEach((uuid, alias) -> {
                buf.writeUUID(uuid);
                buf.writeUtf(alias, MAX_ALIAS_LENGTH);
            });
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
