# Copper Hopper

Copper Hopper adds a new kind of hopper that performs simple item filtering
while retaining a vanilla Minecraft feel.

A Copper Hopper behaves similarly to a normal hopper, but can be configured
to only accept specific item types. Copper Hopper Minecarts provide the same
filtering behavior in mobile form.

The goal of the mod is to provide simple, intuitive item sorting without
requiring large redstone item sorter designs. The mod should feel like a
natural extension of vanilla Minecraft automation. :contentReference[oaicite:0]{index=0}

## Design Philosophy

When making changes, prioritize:

1. Simplicity over feature creep.
2. Vanilla-friendly gameplay.
3. Predictable hopper behavior.
4. Compatibility with existing automation systems.
5. Performance and reliability.

Avoid adding features that significantly increase complexity or transform
Copper Hopper into a general-purpose automation framework.

Favor solutions that feel like a natural extension of vanilla Minecraft.

## Project Structure

This project supports multiple mod loaders.

- `common/` contains shared gameplay logic.
- `fabric/` contains Fabric-specific code.
- `neoforge/` contains NeoForge-specific code.

Whenever possible, shared gameplay behavior should live in `common/`.

Loader-specific code should remain isolated to the appropriate platform
module.

## Repository Scope

This repository contains the source code for Copper Hopper.

Only inspect files tracked by git.

Ignore:

- `build/`
- `.gradle/`
- `run/`
- `logs/`
- generated resources
- IDE metadata
- temporary files
- crash reports

Do not spend time analyzing generated files or build outputs.

## Cost-Aware Development

Repository-wide scans are expensive and should be avoided.

Before exploring the repository:

- Prefer targeted analysis.
- Read only files likely to be relevant.
- Start from files explicitly mentioned in the task.
- Follow references outward only as needed.
- Do not read entire directory trees unless necessary.

When discovering files, prefer:

```bash
git ls-files