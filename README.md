# Litematica Printer

A fork of [IceTank/litematica-printer](https://github.com/IceTank/litematica-printer) (the 2b2t-optimized printer fork) for Minecraft 26.2.

## Changes from upstream

- Ports the mod to Minecraft 26.2: Mojang mappings, Java 25, and upstream malilib/litematica from Modrinth.
- Builds every pushed commit as a 90-day GitHub Actions artifact, and retains the [latest successful build](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc26.2.jar).

Usage, 2b2t setup guides (grim bypass configs), and limitations are documented in the [upstream README](https://github.com/IceTank/litematica-printer#readme).

Build with `./gradlew build` (Java 25).
