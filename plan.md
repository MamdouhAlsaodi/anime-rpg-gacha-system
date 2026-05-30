# Anime RPG Gacha System — Main Plan

> **Current version:** v0.1 Enhanced  
> **Status:** Complete and ready for class/team handoff  
> **Languages:** Arabic, Portuguese, English  
> **Main repository:** https://github.com/MamdouhAlsaodi/anime-rpg-gacha-system

---

## Purpose of this file

This lowercase `plan.md` is the main planning file requested for final delivery. It summarizes the plan that was followed for:

- **Version 0.0** — original OOP + gacha + client/server foundation.
- **Version 0.1** — enhanced gameplay, UI/report layer, fixes, and demo polish.
- **Future updates** — add new tasks under the section at the bottom instead of rewriting the old history.

Detailed historical files are still preserved:

- `PLAN_v0.0.md` — original implementation plan.
- `PLAN_v0.1.md` — enhanced implementation plan.
- `PLAN.md` — short status overview.

---

## v0.0 Plan Followed — Foundation Version

### Goal

Build a complete educational Java OOP project around an anime RPG gacha system, with clean architecture and enough documentation for grading.

### Followed phases

1. **Repository setup**
   - Created project structure, README, plan, and Java source layout.

2. **Foundation models**
   - Added abstract root classes such as `BaseEntity`, `Character`, and `InventoryItem`.
   - Added enums for rarity, element, and weapon type.
   - Added concrete character/item types: `Hero`, `Support`, `Antagonist`, `Weapon`, `Artifact`, and `Relic`.

3. **Custom exceptions**
   - Added `GachaBaseException` hierarchy.
   - Protected invalid states such as insufficient gems, full inventory, and duplicate character handling.

4. **Factory + gacha engine**
   - Added generic `SummonFactory<T>`.
   - Added character, weapon, and artifact factories.
   - Added gacha rates, pity system, and central `GameEngine` orchestration.

5. **API layer**
   - Added `CommandCode`, `GameRequest`, and `GameResponse`.
   - Added command handlers and `CommandRouter`.

6. **TCP server**
   - Added `GachaGameServer` for local socket communication.

7. **Client network layer**
   - Added `ServerConnector` to communicate with the TCP server.

8. **Swing UI foundation**
   - Added main screen, summon screen, inventory screen, reusable cards, gem bar, and star panel.

9. **Gacha animation**
   - Added reveal animation and rarity-colored visual feedback.

10. **Delivery/testing**
   - Added `Main.java` stress/demo script.
   - Documented OOP principles, architecture, test scenarios, and grading coverage.

### v0.0 Result

The project had a clear 3-layer architecture, OOP coverage, basic UI, gacha logic, and command-based API flow.

---

## v0.1 Plan Followed — Enhanced Version

### Goal

Turn the project from a basic gacha simulator into a more complete demo experience with better presentation, gameplay purpose, and stronger classroom handoff.

### Followed upgrades

1. **Bug fixes and stability**
   - Improved duplicate character handling.
   - Improved constellation upgrades.
   - Reduced crash-like behavior around inventory/full states.

2. **Username and profile entry**
   - Added username flow for CLI and Swing.
   - Added special demo-friendly behavior for the `mamdouh` profile.

3. **Stats and reporting**
   - Added better player stats and pull analytics.
   - Improved player status output and in-app reporting.

4. **Inventory/loadout improvements**
   - Improved inventory visibility for characters and items.
   - Connected owned characters/items to gameplay choices.

5. **Progression and missions**
   - Added missions, rewards, materials, and upgrade economy concepts so the loop feels meaningful.

6. **2D adventure mode**
   - Added playable combat loop: choose loadout → enter stage → fight waves/bosses → earn rewards.
   - Added five stage themes and stage preview visuals.

7. **Presentation/report layer**
   - Upgraded `PresentationScreen.java` into an 8-slide in-app showcase.
   - Added architecture, features, gameplay loop, five-stage teaser, OOP highlights, gacha/resources, and demo path.

8. **Documentation polish**
   - Updated README with language availability, architecture rationale, API documentation, and presentation layer explanation.
   - Added this `plan.md` as the main final handoff plan.

### v0.1 Result

The project is now easier to demo because it explains itself visually, has a clearer gameplay loop, and includes documentation for architecture, API, and future updates.

---

## Current verification commands

```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out Main
```

For GUI demo:

```bash
java -cp out server.GachaGameServer
java -cp out client.GachaClientApp
```

---

## Future Updates — Add here only

Use this section for any next versions. Keep the older v0.0 and v0.1 history unchanged.

### v0.2 Ideas

- [ ] Add save/load game state.
- [ ] Add featured banner system.
- [ ] Add sound effects for summon and battle.
- [ ] Add generated UML image under `docs/uml/`.
- [x] Add multilingual summary files: `README-ar.md` and `README-pt.md`.

### New update notes

- _Add future notes here._
