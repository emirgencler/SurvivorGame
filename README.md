# Survivor Game - A Statistical Approach to Game Balance

A Java-based survival game whose combat balance is validated using Monte Carlo simulation and ablation studies, showing that difficulty is driven by enemy scaling rather than overpowered abilities.

## Project Overview

This project demonstrates:
- Object-oriented game design with location-based progression system
- Turn-based combat mechanics with multiple enemy types and strategic resource management
- Monte Carlo simulation (2000+ samples, 200 trials each) to analyze ability impact on survival rates
- Data-driven game balancing using ablation study methodology

**Key Finding**: Survival probability is primarily determined by enemy count scaling (±2% ability impact), indicating well-balanced core mechanics where strategic resource allocation matters more than individual buffs.



##  Why This Project?

I built this survival game from scratch to explore a fundamental game design question: 
**"How can we validate game balance with data instead of gut feeling?"**

Rather than spending weeks playtesting different ability combinations, I implemented a Monte Carlo simulation framework that:
- Tests thousands of random character builds automatically
- Compares survival rates across all enemy types and difficulties
- Uses ablation methodology to isolate the impact of each game mechanic

This approach demonstrates how statistical analysis can replace manual playtesting during development.

The project combines game development with data science, showing that well-designed systems can be validated mathematically before players ever see them.




## Game Features

### Locations
- Safe House - Starting area
- Tool Store - Equipment upgrades
- Cave - Mid-game challenge
- Forest - Advanced combat zone
- Beach - Final endgame location (highest difficulty)

### Combat System
- Turn-based encounters with multiple enemies per location
- Mechanics include:
  - Player attacks with damage-based progression
  - Enemy counter-attacks with health depletion
  - Random dodge chances (1/9 probability)
  - Hidden pre-encounter attacks
- Victory rewards: food, water, crafting materials

### Resource Management
Resources are required to:
- Restore health between battles
- Upgrade equipment and stats
- Progress through increasingly difficult encounters

### Difficulty Scaling
- Easy: 3 enemies
- Medium: 5 enemies  
- Hard: 7 enemies
- Impossible: 10 enemies

Difficulty increases primarily through enemy count rather than individual stats, creating a survival-oriented decision-making experience.

---

## Combat Balance Analysis

This repository includes `Analysis.java` - an experimental module that evaluates ability impact on survival probability using Monte Carlo simulation.

### Methodology

The analysis implements an ablation study:
- Simulates combat under identical conditions
- Compares survival rates with abilities ON vs OFF
- Tests across all enemy types and difficulty levels
- Uses 2000 random character builds × 200 trials per scenario

### Character Build System

Players allocate 30 points across:
- Health: 1 point = 1 HP
- Damage: 1 damage = 3 points
- Abilities (unlocked with 10+ leftover points):
  - DamageDealer: +10% Health
  -  Evolution: +10% Damage


### Results

| Enemy     | Difficulty     | OFF (%) | ON (%) | Δ (ON − OFF) |
| --------- | -------------- | ------: | -----: | -----------: |
| Zombie    | Easy (1)       |    9.50 |  11.50 |        +2.00 |
| Vampire   | Easy (1)       |  100.00 | 100.00 |         0.00 |
| Big Slime | Easy (1)       |    1.50 |   2.00 |        +0.50 |
| Zombie    | Medium (2)     |    0.50 |   1.00 |        +0.50 |
| Vampire   | Medium (2)     |   32.50 |  30.50 |        -2.00 |
| Big Slime | Medium (2)     |    0.00 |   0.50 |        +0.50 |
| Zombie    | Hard (3)       |    0.00 |   0.50 |        +0.50 |
| Vampire   | Hard (3)       |    2.00 |   2.00 |         0.00 |
| Big Slime | Hard (3)       |    0.00 |   0.00 |         0.00 |
| Zombie    | Impossible (4) |    0.00 |   0.00 |         0.00 |
| Vampire   | Impossible (4) |    0.50 |   0.00 |        -0.50 |
| Big Slime | Impossible (4) |    0.00 |   0.00 |         0.00 |

**Configuration**: buildSamples=2000, trialsPerBuild=200, seed=42


### Interpretation


Ability buffs result in only marginal changes in survival probability (typically within ±2%).
This indicates that the game’s core mechanics are well-balanced, with no single ability dominating outcomes.

Difficulty emerges primarily from enemy count scaling rather than ability selection, ensuring that survival depends on strategic stat allocation and resource management rather than luck.

The stability of results across simulations confirms that combat outcomes reward planning over randomness.
Overall, the analysis validates the intended design goal: a survival system where balance is driven by mechanics, not buffs, and where Monte Carlo simulation can replace extensive manual playtesting during development.


---

## How to Run

### Play the Game

Using IDE (Eclipse/IntelliJ):
```bash
Run Main.java
```

Using terminal:
```bash
javac -d out src/SurvivorGame/*.java
java -cp out SurvivorGame.Main
```

### Run Balance Analysis
```bash
javac -d out src/SurvivorGame/*.java
java -cp out SurvivorGame.Analysis 2000 200 42 > results.txt
```

Parameters: `[buildSamples] [trialsPerBuild] [randomSeed]`

---

## What I Learned

- Monte Carlo methods for game balance testing
- Statistical validation of game design decisions
- Object-oriented design for scalable game systems
- Importance of data-driven balancing over intuition



## License

MIT License - See [LICENSE](LICENSE) file for details



## Contact

Feel free to reach out for questions about the methodology or implementation!
