# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

GriefPreventionDynmap is a Spigot Minecraft server plugin that renders GriefPrevention claims on Dynmap (web-based map). It periodically syncs claim data to Dynmap area markers with configurable styling and info popups.

## Build

The Maven project lives in the `GriefPreventionDynmap/` subdirectory (not the repo root):

```bash
cd GriefPreventionDynmap && mvn clean package
```

The output JAR is produced in `GriefPreventionDynmap/target/`. There are no tests.

## Architecture

Two classes in `com.samoatesgames.griefpreventiondynmap`:

- **GriefPreventionDynmap** (`GriefPreventionDynmap.java`): Main plugin class. Hooks into GriefPrevention and Dynmap APIs on enable, then uses a BukkitScheduler task to periodically query all GP claims and create/update Dynmap `AreaMarker` objects. Handles the `/gpdreload` command.
- **Config** (`Config.java`): YAML configuration via CubeEngine Reflect library. Nested section classes define layer settings, marker styling, claim info display options, and admin-claim overrides.

Key data flow: `GriefPrevention.getDataStore().getClaims()` → create/update `AreaMarker` per claim → apply style + HTML info window → remove stale markers.

## Dependencies

- **Spigot API** 1.15.2 (plugin target, Java source level 1.6)
- **Dynmap API** 2.5
- **GriefPrevention** 16.13.0 (via JitPack)
- **CubeEngine Reflect** 3.0.0 (shaded into JAR via maven-shade-plugin)
