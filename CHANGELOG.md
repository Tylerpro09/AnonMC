# Changelog

## 2.2.0
- Replaced sequential aliases with random, unique per-session aliases such as `Anonimo482731`.
- Reconnecting assigns a fresh alias to reduce long-term correlation.
- Added protocol versioning to the client/server handshake.
- Incompatible AnonMC protocol versions are rejected cleanly.
- Added bounds checking to alias synchronization payloads.
- Limited alias sync payloads to 2048 entries and 32 characters per alias.
- Local fallback aliases are now random instead of sequential.
- Fixed stale real-name-to-alias mappings after an authoritative server resync.
- `anonymizeSelf=false` now applies consistently to display names and anonymous skins.
- Chat sanitization now preserves styled segments, including colors and interaction styles.
- Added scoreboard/team-formatted identity sanitization through `PlayerTeamMixin`.
- Registered the new scoreboard sanitizer mixin.
- Bumped project version to 2.2.0.

## 2.1.0
- Made aliases fully server-authoritative.
- Clients cannot choose or submit aliases.
- Players receive a fresh alias after reconnecting.
- Added authoritative alias resync immediately after the AnonMC handshake.
- Removed stale disconnected-player alias mappings from the server.
- Added configurable client-mod handshake timeout.
- Hardened configuration parsing.
- Preserved rich chat components when no username replacement is needed.
- Removed the machine-specific Java path from the repository.
- Updated the selected anonymous skin source.

## 2.0.0
- Added server companion support.
- Added shared server aliases.
- Added optional client-mod requirement.
- Added anonymous skin support.
- Added synchronized aliases and client/server networking.

## 1.0.0
- Initial client-side anonymization release.
