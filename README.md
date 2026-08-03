# Split Distance

Fabric client mod for Minecraft 1.21.11.

Set your in-game render distance to 32. The server sends 32 chunks. Xaero's World Map and
JourneyMap index all 32. Only 12 get drawn.

Frame time behaves like 12 chunks. RAM and bandwidth behave like 32.

## Why this works

Minecraft already separates the two things:

- **What the client stores** is `ClientChunkCache`, sized by the server's `ClientboundSetChunkCacheRadius`
  packet. Nothing to do with your render distance option.
- **What the client draws** is `Options#getEffectiveRenderDistance()`, which feeds section building,
  fog and culling.
- **What gets asked of the server** is `Options#buildPlayerInformation()`, which reads the raw
  `renderDistance` option, *not* the effective one.

So capping `getEffectiveRenderDistance()` shrinks what's drawn without shrinking what's requested.
The far chunks still arrive, still sit in the cache, and map mods reading the world find them.

That's the entire mod. One mixin.

## Config

With **Mod Menu** installed: Mods → Split Distance → the gear icon. Vanilla-style screen, a slider
and a toggle. Changes apply the moment you hit Done — no restart, the chunk sections rebuild the same
way they do when you drag the vanilla render distance slider.

Without Mod Menu: edit `config/splitdistance.properties`, written on first launch, and restart.

```properties
renderChunks=12
threadGuard=true
```

- `renderChunks` — how many chunks actually render. `0` disables the mod.
- `threadGuard` — return the *uncapped* distance to callers outside the render thread. Xaero and
  JourneyMap map on their own worker threads, so if either of them queries render distance to pick a
  scan radius, they still see the full 32. Turn it off if a mod misbehaves.

## Caveats, in order of how likely they are to bite you

1. **The server has to be configured for a high `view-distance`.** You get `min(your setting, server's
   view-distance)`. Ask for 32, a 28-chunk server gives you 28, a 12-chunk server gives you 12 and
   there is nothing extra for the map to index. Most public servers cap at 8–12. No client mod can
   change that. This is mainly for singleplayer, LAN, and servers you control.
2. **This is not "performance as if 12 chunks".** GPU cost, mesh building and frame time drop to
   12-chunk levels — that's the win. Network, chunk deserialization, and heap all stay at 32-chunk
   cost. Roughly 4200 chunks resident; budget a few hundred MB.
3. **Moving works, and must.** The server sends each chunk once. When you walk toward a chunk that's
   already in the cache, `LevelRenderer` picks it up on the next frustum update — vanilla handles the
   handoff. This is exactly why the mod does not try to discard far chunks after the map has indexed
   them: throw one away and you get a permanent hole that never refills, because the server considers
   it already delivered.

## Build

```sh
./gradlew build      # jar lands in build/libs/
./gradlew runClient  # dev client, with Mod Menu installed so you can reach the config screen
```

Uses Mojang mappings and Fabric Loader. Mod Menu 17.0.0 is a compile-only dependency for the config
screen entrypoint; the mod runs fine without it. No Fabric API dependency, no Cloth Config — the
screen is built from vanilla `OptionInstance` widgets.

## Check

The mod's only real logic is one pure function, and it runs standalone:

```sh
java src/main/java/dev/thm/splitdistance/Cap.java   # prints "ok"
```

## Not done

- No light-data stripping for far chunks. It would save a few hundred MB of nibble arrays but breaks
  the night layer on both map mods. Add it if the heap actually hurts.
- Not tested against Sodium. Sodium replaces the section renderer but still reads
  `getEffectiveRenderDistance()`, so it should be fine. Should.

## Dependencies

Nothing at runtime beyond the game itself.

| | Version | Scope | Why |
|---|---|---|---|
| Minecraft | `1.21.11` | required | |
| Fabric Loader | `>=0.19.0` (built against `0.19.3`) | required | mixin host |
| Java | `>=21` | required | |
| Mod Menu | `17.0.0` | **optional** | config screen only; `modCompileOnly`, not in the jar's `depends` |
| Fabric API | `0.141.6+1.21.11` | **dev only** | Mod Menu needs it; `modLocalRuntime`, never shipped |

Build-time only: Gradle `9.6.1` (wrapper), Fabric Loom `1.17.17`, Mojang official mappings.

No Fabric API, no Cloth Config, no config library, no Kotlin. The config screen is built from
vanilla `OptionInstance` widgets and the config file is `java.util.Properties`.

## License

GPL-3.0-or-later — see `LICENSE`.
