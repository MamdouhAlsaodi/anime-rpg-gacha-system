# 🎮 Anime RPG Gacha System

> A complete **Gacha (Loot Box) Summoning Engine** inspired by Genshin Impact, Solo Leveling, and Fate/Grand Order — built with **pure Java**, showcasing advanced **Object-Oriented Programming** principles, **Client-Server architecture**, and **animated Swing UI**.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│           CLIENT LAYER (Swing UI)                   │
│  Screens + Components + Animations + User Events    │
└──────────────────┬──────────────────────────────────┘
                   │ TCP Socket (localhost:8080)
                   │ Serializable Objects
┌──────────────────▼──────────────────────────────────┐
│           API / PROTOCOL LAYER                      │
│  Request | Response | CommandHandler | Router       │
└──────────────────┬──────────────────────────────────┘
                   │ Method Calls
┌──────────────────▼──────────────────────────────────┐
│           SERVER / BUSINESS LAYER                   │
│  GameEngine | Services | Models | Factory | Exceptions│
└─────────────────────────────────────────────────────┘
```

**3-Layer Architecture** — strict separation of concerns:
- **Server Layer**: Business logic, models, factories, game engine
- **API Layer**: Protocol definitions, command handlers, request routing
- **Client Layer**: Swing UI, network connector, animations

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

## 🚀 How to Run

### Prerequisites
- Java 17+ (JDK)
- No external dependencies — pure Java SE

### 1. Compile
```bash
javac -d out src/**/*.java
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
