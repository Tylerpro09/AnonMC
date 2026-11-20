# AnonMC

AnonMC is a Fabric mod for Minecraft 26.2 focused on reducing visible player identity leaks in multiplayer.

## 2.1.0

### Privacy improvements
- Server-authoritative aliases: clients cannot choose or submit their alias.
- Aliases use the fixed format `Anonimo`, `Anonimo1`, `Anonimo2`...
- A player receives a fresh alias after disconnecting and reconnecting, reducing long-term correlation.
- Shared alias snapshots are synchronized to compatible AnonMC clients.
- The server sends a fresh authoritative snapshot immediately after the client handshake.
- Optional server enforcement with `requireClientMod=true`.
- Configurable handshake grace period through `requireClientModTimeoutTicks`.

### Visible identity protection
- Player name tags are anonymized.
- TAB/player-list names are anonymized.
- Known real usernames are sanitized from chat and system messages.
- Messages that do not contain a username keep their original rich component unchanged.
- Optional anonymous skin replacement using the project-selected question-mark skin.

## Configuration

The file is generated at `config/anonmc.properties`.

```properties
anonymizeSelf=true
anonymousSkins=true
sharedServerAliases=true
requireClientMod=false
requireClientModTimeoutTicks=100
sanitizeChat=true
sanitizeScoreboards=true
sanitizeCommandSuggestions=true
```

Alias names are deliberately **not configurable**. The authoritative server controls them.

`requireClientModTimeoutTicks=100` gives a client 5 seconds at 20 TPS to complete the AnonMC handshake. Accepted values are clamped between 20 and 1200 ticks.

## Privacy model

AnonMC reduces identity exposure in the normal game UI. It does not provide cryptographic anonymity. Server administrators, logs, packet inspection, voice chat, modified clients and third-party mods may still expose identity information.

## Requirements

- Minecraft Java Edition 26.2
- Fabric Loader 0.19.5+
- Fabric API 0.159.0+26.2
- Java 25

## Build

Use Java 25 and run:

```powershell
gradle clean build
```

The JAR will be generated under `build/libs/`.

MIT License.
