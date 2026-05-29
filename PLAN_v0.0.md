# 🗺️ Anime RPG Gacha System — Implementation Plan

> **Status:** Phase 0 — Repository initialized, files created
> **Last Updated:** May 2026

---

## 📊 Progress Tracker

| Phase | Name | Status | Files |
|-------|------|--------|-------|
| 0 | Repository Setup | ✅ Done | README, PLAN, .gitignore |
| 1 | Foundation | ⬜ Pending | 12 files |
| 2 | Exceptions | ⬜ Pending | 4 files |
| 3 | Factory & Gacha | ⬜ Pending | 8 files |
| 4 | API Layer | ⬜ Pending | 8 files |
| 5 | Server | ⬜ Pending | 1 file |
| 6 | Client Network | ⬜ Pending | 1 file |
| 7 | UI Foundation | ⬜ Pending | 7 files |
| 8 | Gacha Animation | ⬜ Pending | 1 file |
| 9 | Presentation | ⬜ Pending | 1 file |
| 10 | Delivery | ⬜ Pending | 2 files |

**Total Files:** 45 Java files + docs

---

## Phase 1 — Foundation ⬜
> **Goal:** Core models + enums + abstract classes. No UI, no server.

### Files to implement:
- [ ] `src/server/model/abstracts/BaseEntity.java` — abstract root (id, createdAt, describeYourself())
- [ ] `src/server/model/abstracts/Character.java` — abstract + Template Method (generateReport())
- [ ] `src/server/model/abstracts/InventoryItem.java` — abstract item
- [ ] `src/server/model/enums/Rarity.java` — COMMON, RARE, LEGENDARY
- [ ] `src/server/model/enums/Element.java` — FIRE, ICE, LIGHTNING, WIND, EARTH
- [ ] `src/server/model/enums/WeaponType.java` — SWORD, BOW, STAFF, CLAYMORE, POLEARM
- [ ] `src/server/model/characters/Hero.java` — extends Character
- [ ] `src/server/model/characters/Support.java` — extends Character
- [ ] `src/server/model/characters/Antagonist.java` — extends Character
- [ ] `src/server/model/items/Weapon.java` — extends InventoryItem
- [ ] `src/server/model/items/Artifact.java` — extends InventoryItem
- [ ] `src/server/model/items/Relic.java` — extends InventoryItem

### Also:
- [ ] `src/server/model/player/Player.java` — gems, pity, encapsulated
- [ ] `src/server/model/player/Inventory.java` — Map collections, encapsulated

### Verification:
```java
// Manual test in Main.java
Hero h = new Hero("Rimuru", Rarity.LEGENDARY, Element.WIND, 100);
System.out.println(h.generateReport());
```

---

## Phase 2 — Exceptions ⬜
> **Goal:** Every failure scenario has its own exception.

### Files to implement:
- [ ] `src/server/exception/GachaBaseException.java` — abstract parent (errorCode, errorTime)
- [ ] `src/server/exception/InsufficientGemsException.java` — gems < cost
- [ ] `src/server/exception/InventoryFullException.java` — size >= capacity
- [ ] `src/server/exception/DuplicateCharacterException.java` — duplicate → constellation

### Integration:
- [ ] `Player.spendGems()` throws InsufficientGemsException
- [ ] `Inventory.addCharacter()` throws InventoryFullException + DuplicateCharacterException

### Verification:
```java
Player p = new Player("Test", 0);  // 0 gems
p.spendGems(160);  // Should throw InsufficientGemsException
```

---

## Phase 3 — Factory & Gacha Engine ⬜
> **Goal:** Complete summon logic with probabilities and Pity System.

### Files to implement:
- [ ] `src/server/factory/SummonFactory.java` — generic interface + default createBatch()
- [ ] `src/server/factory/CharacterFactory.java` — creates characters by rarity
- [ ] `src/server/factory/WeaponFactory.java` — creates weapons by rarity
- [ ] `src/server/factory/ArtifactFactory.java` — creates artifacts by rarity
- [ ] `src/server/service/GachaSystem.java` — rates (0.6%/5.1%/94.3%), pity, summon logic
- [ ] `src/server/service/InventoryManager.java` — inventory CRUD operations
- [ ] `src/server/service/ConstellationSystem.java` — duplicate → constellation upgrade
- [ ] `src/server/service/ProgressionSystem.java` — leveling & enhancement
- [ ] `src/server/engine/GameEngine.java` — central orchestrator

### Gacha Rates:
```
Legendary: 0.6%  → Hard Pity: guaranteed at 90 pulls
Rare:      5.1%  → Soft Pity: guaranteed at 10 pulls
Common:   94.3%  → No guarantee
```

### Verification:
```java
GameEngine engine = new GameEngine();
List<Character> result = engine.performSummon(CommandCode.SUMMON_TEN);
result.forEach(c -> System.out.println(c.generateReport()));
```

---

## Phase 4 — API Layer ⬜
> **Goal:** Build the bridge between server and client.

### Files to implement:
- [ ] `src/api/protocol/CommandCode.java` — enum with gem costs
- [ ] `src/api/protocol/GameRequest.java` — Serializable request
- [ ] `src/api/protocol/GameResponse.java` — Serializable response
- [ ] `src/api/handler/CommandHandler.java` — interface (canHandle + process)
- [ ] `src/api/handler/SummonHandler.java` — handles summon commands
- [ ] `src/api/handler/InventoryHandler.java` — handles inventory queries
- [ ] `src/api/handler/PlayerHandler.java` — handles player status
- [ ] `src/api/router/CommandRouter.java` — routes to correct handler

### Verification:
```java
CommandRouter router = new CommandRouter();
GameResponse res = router.route(new GameRequest(CommandCode.SUMMON_SINGLE), engine);
System.out.println(res.isSuccess());
```

---

## Phase 5 — Server ⬜
> **Goal:** TCP server running on localhost:8080.

### Files to implement:
- [ ] `src/server/GachaGameServer.java` — ServerSocket, accept connections, route requests

### Verification:
```bash
java -cp out server.GachaGameServer
# Server listening on port 8080...
```

---

## Phase 6 — Client Network ⬜
> **Goal:** Client connects to server via TCP socket.

### Files to implement:
- [ ] `src/client/network/ServerConnector.java` — Socket + ObjectInputStream/OutputStream

### Verification:
```java
ServerConnector conn = new ServerConnector();
conn.connect("localhost", 8080);
GameResponse res = conn.summonSingle();
```

---

## Phase 7 — UI Foundation ⬜
> **Goal:** Working Swing windows with unified theme.

### Files to implement:
- [ ] `src/client/ui/screens/MainScreen.java` — main window + navigation
- [ ] `src/client/ui/screens/SummonScreen.java` — summon interface
- [ ] `src/client/ui/screens/InventoryScreen.java` — collection viewer
- [ ] `src/client/ui/components/CharacterCard.java` — character display card
- [ ] `src/client/ui/components/GemBar.java` — currency display bar
- [ ] `src/client/ui/components/StarPanel.java` — star rating display
- [ ] `src/client/GachaClientApp.java` — client entry point

### Theme:
```
Background: #0A0A0F
Accent:     #C9A84C
Rare:       #4FC3F7
Legendary:  #FFD700
```

---

## Phase 8 — Gacha Animation ⬜
> **Goal:** Visually striking summon experience.

### Files to implement:
- [ ] `src/client/ui/animation/GachaAnimation.java` — reveal animation + effects

### Features:
- Gold glow for Legendary
- Blue shimmer for Rare
- Particle effects
- x10 sequential reveal

---

## Phase 9 — Presentation Screen ⬜
> **Goal:** In-app project showcase (PowerPoint-style).

### Files to implement:
- [ ] `src/client/ui/screens/PresentationScreen.java` — 6 slides

### Slides:
1. Project title + animated logo
2. 3-layer architecture diagram
3. OOP class tree (visual)
4. Factory Method with live example
5. Gacha system + probability explanation
6. Custom exceptions + failure scenarios

---

## Phase 10 — Delivery ⬜
> **Goal:** Final documentation + test script.

### Files to implement:
- [ ] `src/Main.java` — Official stress test (4 scenarios)
- [ ] `docs/uml/class-diagram.png` — UML class diagram

### Checklist:
- [ ] All 3 justification questions answered in README
- [ ] Test script runs without crashes
- [ ] UML diagram matches code 100%
- [ ] Naming conventions verified (CamelCase, mixedCamelCase)
- [ ] Final `git push` with clean history

---

## 🏗️ Build & Run Commands

```bash
# Compile all
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# Run server
java -cp out server.GachaGameServer

# Run client
java -cp out client.GachaClientApp

# Run tests
java -cp out Main
```