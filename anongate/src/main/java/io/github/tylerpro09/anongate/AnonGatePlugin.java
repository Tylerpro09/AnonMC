package io.github.tylerpro09.anongate;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.util.GameProfile;
import org.slf4j.Logger;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(
        id = "anongate",
        name = "AnonGate",
        version = "1.0.0",
        authors = {"Tylerpro09"}
)
public final class AnonGatePlugin {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Set<String> activeAliases = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<UUID, String> aliasByAnonymousUuid = new ConcurrentHashMap<>();
    private final Logger logger;

    @Inject
    public AnonGatePlugin(Logger logger) {
        this.logger = logger;
        logger.info("AnonGate active: backend identities are ephemeral and server-generated.");
    }

    @Subscribe
    public void onProfile(GameProfileRequestEvent event) {
        String alias = newAlias();
        UUID anonymousUuid = UUID.randomUUID();

        // Never copy the authenticated Mojang profile properties to the backend-facing profile.
        event.setGameProfile(new GameProfile(anonymousUuid, alias, List.of()));
        aliasByAnonymousUuid.put(anonymousUuid, alias);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        UUID anonymousUuid = event.getPlayer().getUniqueId();
        String alias = aliasByAnonymousUuid.remove(anonymousUuid);
        if (alias != null) {
            activeAliases.remove(alias);
        }
    }

    private String newAlias() {
        for (int i = 0; i < 1000; i++) {
            String alias = "Anonimo" + (100000 + RANDOM.nextInt(900000));
            if (activeAliases.add(alias)) {
                return alias;
            }
        }

        String alias;
        do {
            alias = "Anonimo" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        } while (!activeAliases.add(alias));

        return alias;
    }
}
