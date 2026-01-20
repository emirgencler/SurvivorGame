# SurvivorGame

A console-based survival game written in Java with multiple locations and a combat system.

## Locations
- Safe House
- Tool Store
- Cave
- Forest
- Beach

## Gameplay
- Fight enemies and collect loot
- Manage your inventory (food, water, materials)
- Beach is intentionally harder than other locations
- Progress through Cave and Forest before going to the Beach

## How to Run
You can run the project using an IDE (Eclipse / IntelliJ) or from the terminal.

## Design Notes
- Difficulty increases as the player progresses through different locations.
- Resource management and combat balance are core gameplay elements.
- Player progression is encouraged through exploration and equipment upgrades.


```bash
javac -d out src/SurvivorGame/*.java
java -cp out SurvivorGame.Main
