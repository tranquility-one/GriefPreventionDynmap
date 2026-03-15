# GriefPreventionDynmap

Render [GriefPrevention](https://github.com/TechFortress/GriefPrevention) claims on [Dynmap](https://github.com/webbukkit/dynmap).

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
- [Configuration](#configuration)
- [Commands](#commands)
- [Permissions](#permissions)
- [Maintainers](#maintainers)

## Background

GriefPreventionDynmap is a Spigot plugin that periodically syncs GriefPrevention claim data to Dynmap area markers. Each claim is rendered as a colored rectangle on the web map, with configurable styling and an info popup showing the owner, dimensions, and trusted players.

### Requirements

- Java 21+
- Spigot (or Paper) 1.21+
- [Dynmap](https://github.com/webbukkit/dynmap) 3.4+
- [GriefPrevention](https://github.com/TechFortress/GriefPrevention) 16.18+

## Install

1. Download the latest JAR from the [releases page](https://github.com/tranquility-one/GriefPreventionDynmap/releases).
2. Place it in your server's `plugins/` directory.
3. Restart the server. A default `config.yml` will be generated in `plugins/GriefPreventionDynmap/`.

### Building from source

```sh
mvn clean package
```

The output JAR is in `target/`.

## Usage

Once installed, a "Claims" layer appears on your Dynmap. Claim markers update automatically based on the configured refresh rate (default: 30 seconds). Click a claim marker to see owner info, dimensions, and trusted players.

Admin claims and regular claims can be styled independently.

## Configuration

The config file is generated at `plugins/GriefPreventionDynmap/config.yml` on first run.

### Layer settings

| Key | Default | Description |
|---|---|---|
| `layer.name` | `Claims` | Name of the claims layer on Dynmap |
| `layer.priority` | `10` | Render priority of the layer |
| `layer.hiddenByDefault` | `false` | Whether the layer is hidden by default |

> Layer settings require a server reboot to take effect.

### Marker settings

| Key | Default | Description |
|---|---|---|
| `marker.refreshRateInSeconds` | `30` | How often claim markers are refreshed |
| `marker.claim.showChildren` | `false` | Show child (subdivision) claims |
| `marker.claim.showArea` | `true` | Show claim block count in popup |
| `marker.claim.showDimensions` | `false` | Show claim dimensions in popup |
| `marker.claim.showBuilders` | `true` | Show users with full trust |
| `marker.claim.showContainers` | `false` | Show users with container trust |
| `marker.claim.showAccessors` | `false` | Show users with access trust |
| `marker.claim.showManagers` | `false` | Show users with permission trust |

### Style settings

Both `marker.style` (regular claims) and `marker.admin.style` (admin claims) support:

| Key | Default | Description |
|---|---|---|
| `border.color` | `FF0000` | Border color (hex) |
| `border.weight` | `2` | Border thickness |
| `border.opacity` | `0.8` | Border transparency |
| `fill.color` | `FF0000` | Fill color (hex) |
| `fill.opacity` | `0.35` | Fill transparency |

### Admin claim settings

| Key | Default | Description |
|---|---|---|
| `marker.admin.nameOverride` | `an administrator` | Display name for admin claims |
| `marker.admin.showPermissions` | `false` | Show trusted players on admin claims |

## Commands

| Command | Description |
|---|---|
| `/gpdreload` | Reload the configuration file |

## Permissions

| Permission | Description |
|---|---|
| `gpd.admin.reload` | Allows use of `/gpdreload` |

## Maintainers

[@tranquility-one](https://github.com/tranquility-one)
