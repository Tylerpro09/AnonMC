# AnonMC

AnonMC is a Fabric mod for Minecraft 26.2 that anonymizes visible player identity.

## 2.0.0 features
- Configurable alias prefix via `config/anonmc.properties`
- Shared aliases assigned by the server companion
- Optional server requirement for AnonMC clients
- Name-tag and TAB anonymization
- Chat/system-message sanitization
- Foundation for anonymous skins, scoreboard sanitization and command-suggestion sanitization

## Config
`config/anonmc.properties` is generated automatically.

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

## Important privacy note
AnonMC reduces visible identity leaks but is not cryptographic anonymity. UUIDs, server logs, packet inspection, voice chat and third-party mods can still reveal identity.

## Requirements
- Minecraft 26.2
- Fabric Loader 0.19.5+
- Fabric API
- Java 25

MIT License.
