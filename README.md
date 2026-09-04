# Litematica Printer

A fork of [IceTank/litematica-printer](https://github.com/IceTank/litematica-printer) (the 2b2t-optimized printer fork) for Minecraft 26.2.

Usage, 2b2t setup guides (grim bypass configs), and limitations are documented in the [upstream README](https://github.com/IceTank/litematica-printer#readme).

Build with `./gradlew build` (Java 25).

## Changes from upstream

- Fixes airplace throwing out offhand items: hand swaps are mirrored client-side instead of evicting through the cursor, so nothing is dropped when the inventory is full (`printerAirPlaceOffhandSlotSuppress` is now a legacy opt-in, default off).
- Fixes freelook player rotation drifting away from the camera rotation.
- Stops the printer config tab from duplicating litematica's Generic options.
- Keeps printing when the chat is open even if `printerDisableInGuis` is enabled.
- Builds Minecraft 26.2 and 1.21.4 from one source tree via Stonecutter.
- Builds a 90-day GitHub artifact for every commit, and retains latest successful build for [26.2](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc26.2.jar) and [1.21.4](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc1.21.4.jar).
