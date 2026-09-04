# Litematica Printer

A fork of [IceTank/litematica-printer](https://github.com/IceTank/litematica-printer) (the 2b2t-optimized printer fork) for Minecraft 26.2.

## Changes from upstream

- Ports the mod to Minecraft 26.2: Mojang mappings, Java 25, and upstream malilib/litematica from Modrinth.
- Fixes freelook player rotation drifting away from the camera rotation.
- Stops the printer config tab from duplicating litematica's Generic options.
- Builds Minecraft 26.2 and 1.21.4 from one source tree via Stonecutter.
- Builds a 90-day GitHub artifact for every commit, and retains latest successful build for [26.2](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc26.2.jar) and [1.21.4](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc1.21.4.jar).

Usage, 2b2t setup guides (grim bypass configs), and limitations are documented in the [upstream README](https://github.com/IceTank/litematica-printer#readme).

Build with `./gradlew build` (Java 25).
