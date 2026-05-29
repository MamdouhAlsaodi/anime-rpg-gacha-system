# Project: Anime RPG Gacha System

## Tech Stack
- Java 17+ (pure Java SE — no external dependencies)
- Java Swing for UI
- TCP Sockets for Client-Server communication
- ObjectInputStream/ObjectOutputStream for Serializable protocol

## Architecture
3-Layer Architecture:
1. Server Layer — Business logic (models, factories, services, engine)
2. API Layer — Protocol (request/response, handlers, router)
3. Client Layer — Swing UI + Network connector + Animations

## Commands
- `find src -name "*.java" > sources.txt && javac -d out @sources.txt` — Compile
- `java -cp out server.GachaGameServer` — Start server
- `java -cp out client.GachaClientApp` — Start client
- `java -cp out Main` — Run test script

## Conventions
- Class names: PascalCase
- Method names: camelCase
- Variable names: camelCase
- Constants: UPPER_SNAKE_CASE
- Each class in its own file
- Package structure mirrors architecture layers

## OOP Principles Required
1. **Abstraction** — BaseEntity, Character, InventoryItem are abstract
2. **Inheritance** — Hero/Support/Antagonist extend Character
3. **Polymorphism** — List<Character> with dynamic dispatch on calculateCombatPower()
4. **Encapsulation** — Player gems never negative, Inventory with private Maps
5. **Factory Method** — SummonFactory<T> interface with concrete factories
6. **Custom Exceptions** — GachaBaseException hierarchy

## Visual Theme
- Background: #0A0A0F (deep cosmic black)
- Accent: #C9A84C (ancient gold)
- Rare: #4FC3F7 (neon blue)
- Legendary: #FFD700 (stellar gold)
- Anime-style fonts for titles, monospace for stats

## Gacha Rates
- Legendary: 0.6% (hard pity at 90)
- Rare: 5.1% (soft pity at 10)
- Common: 94.3%
- Duplicates → constellation level up (0-6)
