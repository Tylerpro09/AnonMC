# AnonGate

AnonGate is the gateway layer for AnonMC.

## Goal

Authenticate the real Minecraft account at the proxy, then replace the backend-facing identity with a disposable session identity.

## Recommended topology

```text
Minecraft client + AnonMC
        |
        | Tor
        v
AnonGate Onion Service
        |
        v
Velocity on 127.0.0.1:25566
        |
        v
Minecraft backend on 127.0.0.1:25565
```

## Velocity settings

Use:

```toml
bind = "127.0.0.1:25566"
online-mode = true
player-info-forwarding-mode = "none"

[servers]
backend = "127.0.0.1:25565"
try = ["backend"]

[forced-hosts]
```

## Backend settings

The backend must not be exposed publicly.

Recommended `server.properties` values:

```properties
server-ip=127.0.0.1
server-port=25565
online-mode=false
enforce-secure-profile=false
```

With forwarding disabled, the backend does not receive the authenticated client IP/UUID through Velocity forwarding. The AnonGate plugin also replaces the proxy-side profile name with a random session alias before the backend connection.

This protects the backend administrator from normal in-game identity visibility, but it cannot prevent the operator of the AnonGate machine itself from instrumenting or modifying the gateway to observe authentication data.
