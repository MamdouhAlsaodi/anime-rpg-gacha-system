# 🎮 Anime RPG Gacha System

> A complete **Gacha (Loot Box) Summoning Engine** inspired by Genshin Impact, Solo Leveling, and Fate/Grand Order — built with **pure Java**, showcasing advanced **Object-Oriented Programming** principles, **Client-Server architecture**, and **animated Swing UI**.

---


## 🌐 Available in Three Languages

This project is prepared for classmates and reviewers in **three languages**:
- 🇸🇦 **Arabic (العربية)** — [README-ar.md](README-ar.md)
- 🇧🇷 **Portuguese (Português)** — [README-pt.md](README-pt.md)
- 🇬🇧 **English** — this README file

> The code stays in English for Java conventions, while the documentation summary and presentation handoff are available in Arabic, Portuguese, and English.

---

## 🏗️ Architecture Overview

The project follows a **strict 3-layer architecture** so the game is easy to explain, test, and extend.

```
┌─────────────────────────────────────────────────────┐
│           CLIENT / PRESENTATION LAYER               │
│  Swing Screens + Components + Animations + Events   │
└──────────────────┬──────────────────────────────────┘
                   │ TCP Socket (localhost:8080)
                   │ Serializable GameRequest objects
┌──────────────────▼──────────────────────────────────┐
│           API / PROTOCOL LAYER                      │
│  CommandCode | GameRequest | GameResponse | Router  │
└──────────────────┬──────────────────────────────────┘
                   │ Validated method calls
┌──────────────────▼──────────────────────────────────┐
│           SERVER / BUSINESS LAYER                   │
│  GameEngine | Services | Models | Factories | Rules │
└─────────────────────────────────────────────────────┘
```

### Why this structure?

- **Client Layer = presentation only**: Swing screens display the game, collect user actions, and show animations. They do not own the gacha rules.
- **API Layer = clean contract**: `GameRequest`, `GameResponse`, `CommandCode`, handlers, and `CommandRouter` define exactly how the client talks to the game engine.
- **Server Layer = business truth**: `GameEngine`, services, models, factories, and exceptions keep the rules in one place: gems, pity, inventory, upgrades, missions, and rewards.

This design gives the project:
- **Separation of concerns** — UI, protocol, and business logic are not mixed.
- **Testability** — the server/game engine can be tested from `Main.java` without opening Swing.
- **Extensibility** — a new UI, command, item type, or game mode can be added without rewriting everything.
- **Good OOP demonstration** — abstractions, inheritance, polymorphism, encapsulation, factories, and exceptions are visible in the structure.

---

## 🔌 API & Protocol Documentation

The API layer is an internal Java socket protocol. The client sends a serializable `GameRequest`; the server routes it; then the server returns a serializable `GameResponse`.

### Command Codes

| Command | Gem Cost | Purpose |
|---------|----------|---------|
| `SUMMON_SINGLE` | 160 | Perform one gacha pull. |
| `SUMMON_TEN` | 1600 | Perform ten pulls with pity/rarity logic. |
| `VIEW_INVENTORY` | 0 | Return owned characters and items. |
| `VIEW_PLAYER` | 0 | Return player profile, gems, pity, and stats. |
| `LEVEL_UP` | 0 | Level up a character through the inventory service. |
| `ENHANCE_ITEM` | 0 | Enhance an owned weapon/artifact/relic. |
| `HELP` | 0 | Return available commands. |
| `EXIT` | 0 | Disconnect from the server. |

### Request Object

`GameRequest` contains:
- `CommandCode command` — the action to execute.
- `Map<String, Object> params` — optional command parameters.
- `String requestId` — UUID used to match response to request.

### Response Object

`GameResponse` contains:
- `boolean success` — whether the command succeeded.
- `String message` — user-friendly result or error.
- `Object data` — returned payload, such as inventory, player stats, or summon results.
- `String requestId` — echoed from the request by the router.
- `LocalDateTime timestamp` — response creation time.

### Router + Handlers Flow

```
Client UI
  │
  ├── creates GameRequest(CommandCode.SUMMON_TEN)
  │
  ▼
ServerConnector / TCP socket
  │
  ▼
GachaGameServer
  │
  ▼
CommandRouter
  │
  ├── SummonHandler      → SUMMON_SINGLE, SUMMON_TEN
  ├── InventoryHandler   → VIEW_INVENTORY, LEVEL_UP, ENHANCE_ITEM
  └── PlayerHandler      → VIEW_PLAYER
  │
  ▼
GameEngine + services
  │
  ▼
GameResponse(success, message, data)
```

The router makes the API easy to extend: to add a new feature, create a new `CommandCode`, add a handler method, and route it without changing the whole client.

---

## 📦 Package Structure

```
src/
├── server/                          # Server & Business Logic
│   ├── GachaGameServer.java         # TCP Server (port 8080)
│   ├── model/
│   │   ├── abstracts/               # Abstract base classes
│   │   │   ├── BaseEntity.java      # Root entity (id, timestamps)
│   │   │   ├── Character.java       # Abstract character + Template Method
│   │   │   └── InventoryItem.java   # Abstract item
│   │   ├── characters/              # Character subtypes
│   │   │   ├── Hero.java            # DPS/Tank hybrid
│   │   │   ├── Support.java         # Healer/Buffer
│   │   │   └── Antagonist.java      # Elemental damage dealer
│   │   ├── items/                   # Item subtypes
│   │   │   ├── Weapon.java          # Equippable weapon
│   │   │   ├── Artifact.java        # Stat booster
│   │   │   └── Relic.java           # Special ability item
│   │   ├── player/
│   │   │   ├── Player.java          # Player entity (gems, pity)
│   │   │   └── Inventory.java       # Encapsulated collection
│   │   └── enums/
│   │       ├── Rarity.java          # COMMON, RARE, LEGENDARY
│   │       ├── Element.java         # Fire, Ice, Lightning, etc.
│   │       └── WeaponType.java      # Sword, Bow, Staff, etc.
│   ├── factory/                     # Factory Method Pattern
│   │   ├── SummonFactory.java       # Generic interface
│   │   ├── CharacterFactory.java    # Creates random characters
│   │   ├── WeaponFactory.java       # Creates random weapons
│   │   └── ArtifactFactory.java     # Creates random artifacts
│   ├── service/                     # Business services
│   │   ├── GachaSystem.java         # Summon logic + rates + pity
│   │   ├── InventoryManager.java    # Inventory operations
│   │   ├── ConstellationSystem.java # Duplicate → constellation upgrade
│   │   └── ProgressionSystem.java   # Leveling & enhancement
│   ├── exception/                   # Custom exception hierarchy
│   │   ├── GachaBaseException.java  # Abstract parent
│   │   ├── InsufficientGemsException.java
│   │   ├── InventoryFullException.java
│   │   └── DuplicateCharacterException.java
│   └── engine/
│       └── GameEngine.java          # Central game orchestrator
│
├── api/                             # Protocol Layer
│   ├── protocol/
│   │   ├── GameRequest.java         # Serializable request
│   │   ├── GameResponse.java        # Serializable response
│   │   └── CommandCode.java         # Enum with all commands
│   ├── handler/
│   │   ├── CommandHandler.java      # Interface
│   │   ├── SummonHandler.java       # Handles summon commands
│   │   ├── InventoryHandler.java    # Handles inventory queries
│   │   └── PlayerHandler.java       # Handles player status
│   └── router/
│       └── CommandRouter.java       # Routes to correct handler
│
├── client/                          # Client Layer
│   ├── GachaClientApp.java          # Main client entry point
│   ├── network/
│   │   └── ServerConnector.java     # TCP socket manager
│   └── ui/
│       ├── screens/
│       │   ├── PresentationScreen.java  # 6-slide project showcase
│       │   ├── MainScreen.java          # Main navigation
│       │   ├── SummonScreen.java        # Gacha summon interface
│       │   └── InventoryScreen.java     # Collection viewer
│       ├── components/
│       │   ├── CharacterCard.java       # Character display card
│       │   ├── GemBar.java              # Currency display
│       │   └── StarPanel.java           # Star rating display
│       └── animation/
│           └── GachaAnimation.java      # Summon reveal animation
│
└── Main.java                        # Stress test script
```

---

## 🧬 OOP Principles Demonstrated

### 1. Abstraction
- `BaseEntity` — abstract root with shared fields (`id`, `createdAt`)
- `Character` — abstract with Template Method (`generateReport()`)
- `InventoryItem` — abstract item with `calculateAttackBonus()`

### 2. Inheritance
- `Hero`, `Support`, `Antagonist` extend `Character`
- `Weapon`, `Artifact`, `Relic` extend `InventoryItem`
- `InsufficientGemsException`, `InventoryFullException`, `DuplicateCharacterException` extend `GachaBaseException`

### 3. Polymorphism
- Heterogeneous `List<Character>` — dynamic dispatch on `calculateCombatPower()`
- `CommandHandler` interface — different handlers respond to same `process()` call
- Factory Method — `SummonFactory<T>` produces different types

### 4. Encapsulation
- `Inventory` — private `Map` collections, no direct access
- `Player` — gems can never go negative (validated via `spendGems()`)
- `InventoryItem` — enhancement level controlled through methods

### 5. Interface + Factory Method
- `SummonFactory<T extends BaseEntity>` — generic factory interface
- `CharacterFactory`, `WeaponFactory`, `ArtifactFactory` — concrete implementations
- `CommandHandler` — handler interface with `canHandle()` + `process()`

### 6. Custom Exception Hierarchy
- `GachaBaseException` (abstract) → `InsufficientGemsException`, `InventoryFullException`, `DuplicateCharacterException`
- Each exception carries business context (current gems vs required, character name, etc.)

---

## 🎰 Gacha System Design

### Drop Rates
| Rarity | Rate | Guarantee |
|--------|------|-----------|
| ⭐⭐⭐⭐⭐ Legendary | 0.6% | Hard Pity at 90 pulls |
| ⭐⭐⭐⭐ Rare | 5.1% | Soft Pity at 10 pulls |
| ⭐⭐⭐ Common | 94.3% | — |

### Pity System
- **90 summons** guarantee a Legendary character
- **10 summons** guarantee at least a Rare
- Pity counter resets on successful pull

### Constellation System
- Duplicate characters **don't create new objects**
- Instead, the existing character's **Constellation Level increases** (0→6)
- Each constellation level unlocks stat bonuses

---

## 🎨 Visual Theme

| Element | Color | Code |
|---------|-------|------|
| Background | Deep Cosmic Black | `#0A0A0F` |
| Accent | Ancient Gold | `#C9A84C` |
| Rare Glow | Neon Blue | `#4FC3F7` |
| Legendary Glow | Stellar Gold | `#FFD700` |

---

## 📺 Presentation / Report Layer Quality

The project includes a polished in-app presentation screen (`PresentationScreen.java`) designed for a classroom demo, not only a basic menu.

### What the presentation layer shows

- **Hero introduction** — explains the project as a Java Swing RPG + gacha + 2D adventure.
- **Updated feature report** — summon, inventory, progression, resources, adventure mode, and presentation.
- **3-layer architecture slide** — visually separates Client, API, and Server layers.
- **Gameplay loop slide** — explains why the player collects characters and items: summon → loadout → fight → earn → upgrade.
- **Five-stage teaser** — forest, desert, cave, volcano, and castle previews drawn with `Graphics2D`.
- **OOP highlights** — abstraction, inheritance, polymorphism, and encapsulation.
- **Gacha/resource explanation** — rates, pity, rewards, daily missions, quests, and achievements.
- **Demo path** — clear order for showing the project to classmates/professor.

### Why this improves the report layer

Instead of explaining the architecture only in text, the project can now **show** its structure and gameplay loop inside the application. This makes the final presentation stronger because the reviewer can see the design decisions, UI polish, and OOP concepts while running the program.

---

## 🚀 How to Run

### Prerequisites
- Java 17+ (JDK)
- No external dependencies — pure Java SE

### 1. Compile
```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
```

### 2. Start Server
```bash
java -cp out server.GachaGameServer
```

### 3. Start Client
```bash
java -cp out client.GachaClientApp
```

### 4. Run Tests
```bash
java -cp out Main
```

---

## 📋 Design Decisions & Justifications

### Q1: Why is `Character` defined as an Abstract Class and not an Interface?

`Character` is an abstract class because it contains **shared state and behavior** that all character types inherit:
- Private fields: `name`, `level`, `constellationLevel`, `rarity`, `element`
- Concrete methods: `levelUp()`, `increaseConstellation()`
- A Template Method: `generateReport()` — fixed structure with varying details

An interface cannot hold state or provide concrete method implementations (prior to Java 8 default methods, which are not suitable for this design). The abstract class allows us to define **what is common** while forcing subclasses to implement **what varies** (`calculateCombatPower()`, `getSpecialAbility()`).

### Q2: Explain the Polymorphism flow in `calculateCombatPower()`

The `GameEngine` maintains a `List<Character>` containing `Hero`, `Support`, and `Antagonist` objects. When the engine calls `calculateCombatPower()` on each element:

1. **Compile time**: The compiler sees a `Character` reference — it verifies the method exists in the abstract class
2. **Runtime**: The JVM performs **dynamic dispatch** — it looks up the actual object type (Hero, Support, or Antagonist) and calls the **overridden** version
3. **Result**: Each subtype computes power differently:
   - `Hero`: `baseAttack × criticalRate × level × 1.2`
   - `Support`: `baseAttack × healingMultiplier × level`
   - `Antagonist`: `baseAttack × elementalDamageBonus × level × 0.85`

The calling code knows nothing about the specific subtype — it just calls `calculateCombatPower()` and gets the correct result. This is **runtime polymorphism** via method overriding.

### Q3: Test scenario that triggers `InsufficientGemsException`

```
Player starts with 0 gems.
Player attempts: SUMMON_SINGLE (cost: 160 gems)

Flow:
1. SummonHandler.process() calls GameEngine.performSummon()
2. GameEngine calls player.spendGems(160)
3. Player.spendGems() checks: 0 < 160 → throws InsufficientGemsException(0, 160)
4. Exception propagates to SummonHandler → caught → GameResponse(success=false)
5. User sees: "❌ You need 160 more gems!"

Why this exception is necessary:
- The business rule is: "A player cannot summon without sufficient gems"
- Without this exception, the system would allow negative gems (invalid state)
- The exception carries context (current vs required) for meaningful user feedback
- It prevents the summon from executing at all — no character is created
```

---

## 🧪 Test Script Scenarios

| Test | Action | Expected Result |
|------|--------|-----------------|
| **Test 1** | Player with 0 gems tries to summon | `InsufficientGemsException` thrown |
| **Test 2** | Summon same character twice | `DuplicateCharacterException` → constellation +1 |
| **Test 3** | Add character when inventory full (cap=1) | `InventoryFullException` thrown |
| **Test 4** | Team of Hero + Support + Antagonist | 3 different `calculateCombatPower()` results |

---

## 📊 Grade Coverage

| Weight | Criterion | Coverage |
|--------|-----------|----------|
| 20% | UML Diagram | All classes + relationships mapped |
| 40% | Java Code (OOP) | Factory, Abstract, Polymorphism, Encapsulation, Collections, Exceptions |
| 20% | README / Justifications | 3 questions answered with code examples |
| 20% | Test Script | 4 failure scenarios with documented logs |

---

## 🗺️ Project Phases

1. **Foundation** — Core models + enums + abstract classes
2. **Exceptions** — Custom exception hierarchy + business rules
3. **Factory & Gacha** — SummonFactory + GachaSystem + Pity + Constellation
4. **API Layer** — Protocol + Handlers + Router
5. **Server** — TCP server on port 8080
6. **Client Network** — ServerConnector + error handling
7. **UI Foundation** — Theme + screens + components
8. **Gacha Animation** — Reveal effects + particles
9. **Presentation** — 6-slide in-app showcase
10. **Delivery** — Test script + README + UML + final review

---

## 📄 License

This project is for educational and portfolio purposes.

---

> Built with ❤️ by **Mamdouh Alsaudi** — Computer Science Student
