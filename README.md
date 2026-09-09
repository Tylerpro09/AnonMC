# AnonMC

AnonMC is a Fabric mod for Minecraft 26.2 that anonymizes visible player identity.

## 2.0.0
- Configurable aliases through `config/anonmc.properties`
- Shared aliases assigned by the server companion
- Optional `requireClientMod=true` mode
- Name-tag and TAB anonymization
- Chat/system-message sanitization
- Client/server synchronization protocol

## Config
```properties
aliasPrefix=Anonimo
anonymizeSelf=true
anonymousSkins=true
sharedServerAliases=true
requireClientMod=false
sanitizeChat=true
sanitizeScoreboards=true
sanitizeCommandSuggestions=true
```

## Current privacy coverage
The v2 core handles synchronized aliases, visible names and chat. The config keys for anonymous skins, scoreboard sanitization and command suggestion sanitization are included for the next rendering/UI hooks; they should not be treated as complete privacy coverage until those hooks are verified against 26.2 mappings.

## Important privacy note
AnonMC reduces visible identity leaks but is not cryptographic anonymity. UUIDs, server logs, packet inspection, voice chat and third-party mods can still reveal identity.

## Requirements
- Minecraft 26.2
- Fabric Loader 0.19.5+
- Fabric API
- Java 25

MIT License.
