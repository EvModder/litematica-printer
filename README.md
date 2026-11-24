# Litematica Printer

A fork of [IceTank/litematica-printer](https://github.com/IceTank/litematica-printer) (itself a fork, optimized for 2b2t) for Minecraft 26.3 / 26.2 / 1.21.4.<br>
Usage, 2b2t setup (grim bypass configs), and limitations are documented in the [upstream README](https://github.com/IceTank/litematica-printer#readme).

Build with `./gradlew build` (Java 25).

## Changes from upstream

- Uses an item held in the off hand from there, without changing the hotbar slot (`printerUseOffhand`).
- New defaults: air place and real rotations on; Grim rotation, third-person FreeLook and ghost item fix off.
- `printerWaterlogging` only fills blocks that need water, aims the bucket, and no longer stalls without one.
- Places waterloggable blocks whether or not their spot already holds water.
- Fixes airplace placing from the wrong slot after the hotbar selection changed in the same tick.
- Places up to 9 blocks per tick instead of one (`printerPlacementsPerTick`, `printerInteractLimit`).
- Moves items into an empty hotbar slot when one is free instead of replacing one in use.
- Fixes airplace throwing out offhand item (`printerAirPlaceOffhandSlotSuppress` is now legacy; default off).
- Fixes FreeLook drifting away from the camera rotation, and stops it from fighting with other mods' Freecams.
- Keeps printing when the chat is open even if `printerDisableInGuis` is enabled.
- Stops the printer config tab from duplicating litematica's Generic options.
- Builds Minecraft 26.3, 26.2 and 1.21.4 from one source tree via Stonecutter.
- Builds 90-day GitHub artifacts on every commit, and retains latest successful builds for [26.3](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc26.3.jar), [26.2](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc26.2.jar) and [1.21.4](https://github.com/EvModder/litematica-printer/releases/download/latest-build/litematica-printer-latest-mc1.21.4.jar).
