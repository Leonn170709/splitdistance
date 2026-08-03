# Publishing checklist — Modrinth

Everything needed to create the project and upload 1.0.0. Copy the fields straight out of here.
Checked against <https://modrinth.com/legal/rules>; the compliance notes are at the bottom.

---

## 1. Project settings

| Field | Value |
|---|---|
| Project type | Mod |
| Name | `Split Distance` |
| Slug / URL | `splitdistance` |
| Icon | `docs/modrinth-icon.png` (512×512) |
| Client side | **Required** |
| Server side | **Unsupported** |
| License | `GPL-3.0-or-later` |
| Categories | `Optimization`, `Utility` |
| Source code | `https://github.com/Leonn170709/splitdistance` |
| Issue tracker | `https://github.com/Leonn170709/splitdistance/issues` |

**Summary** (one line, no formatting, must not repeat the title):

```
Renders a small chunk radius while your map mod still indexes the full one the server sends. Frame time of 12 chunks, Xaero's and JourneyMap coverage of 32.
```

---

## 2. Description

Paste as the project description. Plain Markdown, no HTML, no ASCII art — Modrinth requires the
description to stay accessible and to have an English version.

````markdown
Set your render distance to 32. Only 12 chunks get drawn. Xaero's World Map and JourneyMap still
index all 32.

You get the frame time of a 12-chunk render distance and the map coverage of a 32-chunk one.

## What it actually does

Minecraft already keeps two things separate, and this mod simply stops them from being tied to the
same number:

- **What the client stores** is decided by the server, via the chunk cache radius it sends you.
- **What the client draws** is decided by your render distance option.
- **What the client asks the server for** reads the raw option, not the effective one.

So the mod caps the drawing side only. The far chunks still arrive, still sit in the client's chunk
cache, and map mods reading the world still find them. That is the entire mod — one mixin.

## Read this before you download

**Your server has to be configured for a high `view-distance`.** You receive whichever is smaller,
your setting or the server's. Ask for 32, a 28-chunk server gives you 28, a 12-chunk server gives you
12 and there is nothing extra for the map to index. Many public servers cap at 8–12, and no
client-side mod can change that. This is mainly useful in singleplayer, on LAN, and on servers you
control.

**This is not "performance as if 12 chunks".** GPU cost, mesh building and frame time drop to
12-chunk levels — that is the win, and it is a real one. Network traffic, chunk deserialization and
memory stay at 32-chunk cost. Expect a few hundred MB of extra heap.

**It does not bypass anything.** The mod requests exactly what your own render distance option asks
for and receives exactly what the server chooses to send. It shows you no world data that a vanilla
client with the same render distance would not already have. It only draws less of it.

## Configuration

With Mod Menu installed: Mods → Split Distance → gear icon. A slider for the render cap and one
toggle. Changes apply immediately, no restart.

Without Mod Menu: edit `config/splitdistance.properties` and restart.

- `renderChunks` — how many chunks actually render. Default 12. Set to 0 to disable the mod.
- `threadGuard` — report the full, uncapped distance to callers outside the render thread, so map
  mods that check render distance still scan the whole radius. Default on. Turn it off if a map mod
  misbehaves.

## Versions

One jar covers Minecraft 1.21 through 1.21.11, Fabric.

## Dependencies

None at runtime beyond Fabric Loader and the game. Fabric API is **not** required.

Mod Menu is optional and only provides the config screen.

## Source

GPL-3.0-or-later. Source and issue tracker on GitHub.
````

---

## 3. Version upload

| Field | Value |
|---|---|
| Version number | `1.0.0` |
| Version title | `Split Distance 1.0.0` |
| Release channel | Release |
| Loaders | `Fabric` |
| Game versions | 1.21, 1.21.1, 1.21.2, 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.7, 1.21.8, 1.21.9, 1.21.10, 1.21.11 |
| Primary file | `build/libs/splitdistance-1.0.0.jar` |
| Dependencies | Mod Menu — **optional** |

Do **not** add Fabric API as a dependency. It is only used in the development environment, because
Mod Menu needs it there; the shipped jar does not depend on it.

Do **not** attach the `-sources.jar` as an additional file. Modrinth reserves additional files for
special purposes, and the source is on GitHub.

Build the jar first:

```sh
./gradlew clean build
```

**Changelog for 1.0.0:**

```markdown
First release.

- Cap the rendered chunk radius without capping what the client requests from the server, so
  Xaero's World Map and JourneyMap index the full radius while only a smaller one is drawn.
- Config screen via Mod Menu, applied live; `config/splitdistance.properties` otherwise.
- One jar for Minecraft 1.21 through 1.21.11.
```

---

## 4. Gallery (optional)

Modrinth requires gallery images to be relevant and titled. Two that would carry their weight, both
needing a screenshot from you:

1. F3 screen at render distance 32 with the cap at 12 — title it something like
   "Render distance 32 requested, 12 drawn".
2. Xaero's World Map showing the full indexed radius next to the much smaller rendered area —
   title "Map indexes the full radius".

Skip the gallery entirely rather than filling it with decorative screenshots; empty is allowed,
irrelevant is not.

---

## 5. Rule compliance

Checked against Modrinth's content rules.

| Rule | Status |
|---|---|
| Clear and honest function — what it does, why, critical info up front | Description opens with the effect, then a "Read this before you download" section covering the server requirement and the real memory cost |
| Description accessible, plain Markdown, English | No HTML, no ASCII art, no image-only text |
| Title contains only the project name | `Split Distance` |
| Summary avoids repeating the title and formatting | Starts with the behaviour, plain text |
| Metadata filled correctly (license, sides, tags) | GPL-3.0-or-later, client Required / server Unsupported, Optimization + Utility |
| All dependencies specified | Mod Menu optional; Fabric API deliberately not listed, see above |
| External links lead to relevant public resources | GitHub repo and issue tracker, both public |
| Gallery images relevant and titled | None uploaded, or the two above |
| Copyright — you own it or have permission | Own code, GPL-3.0-or-later, no third-party assets. Icon generated for this project |
| No reupload of someone else's work | Xaero's World Map and JourneyMap are named as compatible mods only; none of their code or art is used or redistributed |
| Not a cheat or hack | Requests only what the render distance option asks for, receives only what the server grants, exposes no world data a vanilla client at the same render distance would not have. No server opt-out needed because nothing changes server-side |
| No undisclosed remote data upload | Makes no network connections of its own |
| Does not bypass Mojang server-blocking | Untouched |

One thing to be deliberate about: the description names **Xaero's World Map** and **JourneyMap**
because compatibility with them is the entire point. That is a factual compatibility statement, which
is fine — but do not use their logos or art in the icon or gallery, and do not word anything so it
reads as an endorsement by their authors.
