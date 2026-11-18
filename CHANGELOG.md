# Changelog

## 0.1.0

### Added

- iBeacon ranging on iOS and Android, with targets specified by UUID, major, and minor.
- Commands to start and stop ranging, plus revisioned execution snapshots.
- Subscriptions for start, measurement, termination, and measurement parse error events.
- A `useRanging` hook with optional event callbacks and automatic observation lifecycle management.
- An example app demonstrating permissions, ranging commands, and observer mounting and unmounting.
- TypeScript, Swift, and Kotlin tests for domain rules and observation behavior.
