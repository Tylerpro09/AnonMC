# AnonMC

AnonMC is a Fabric mod for Minecraft 26.2 focused on reducing visible player identity leaks in multiplayer.

## 2.2.0 privacy model

AnonMC now treats identity as a server-controlled session property instead of a client-selected nickname.

### Server-authoritative aliases
- Clients cannot choose or submit an alias.
- The server generates a random unique alias such as `Anonimo482731`.
- Aliases are unique among currently connected players.
- The alias is deleted on disconnect.
- Reconnecting generates a new alias.
- Sequential join-order leakage from `Anonimo1`, `Anonimo2`, etc. is removed.

### Client/server protocol
- The AnonMC handshake has its own protocol version.
- Incompatible protocol versions are rejected instead of silently desynchronizing aliases.
- Alias sync packets are size-bounded before decoding.
- The server sends an authoritative alias snapshot after a valid handshake.

### Visible identity protection
- Player display names are anonymized.
- TAB/player-list names are anonymized.
- Chat and system messages sanitize known real usernames.
- Rich chat styling is preserved per flattened styled segment when replacements are needed.
- Scoreboard/team-formatted player identities are sanitized.
- Optional anonymous skin replacement uses the selected question-mark skin.
- `anonymizeSelf=false` is respected for both local display name and skin.

## Configuration

Generated at `config/anonmc.properties`:

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

Alias names are deliberately not configurable by clients.

## Remaining privacy limits

AnonMC is a UI/privacy layer, not cryptographic anonymity. A server administrator, modified client, packet inspection, server logs, voice chat or a third-party mod may still expose UUID/profile identity.

Command-suggestion sanitization is still being hardened for Minecraft 26.2's current `CommandSuggestions` implementation; the config key exists, but this surface should not yet be treated as complete privacy coverage.

## Requirements

- Minecraft Java Edition 26.2
- Fabric Loader 0.19.5+
- Fabric API 0.159.0+26.2
- Java 25

## Build

```powershell
gradle clean build
```

Output:

```text
build/libs/anonmc-2.2.0.jar
```

MIT License.
