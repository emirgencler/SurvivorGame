package SurvivorGame;

import java.util.Random;

/**
 * SurvivorGame - Balance/Mechanics Study (Ablation)
 *
 * Goal:
 * Compare survival probability with abilities toggled:
 *  - OFF: ignore ability buffs
 *  - ON:  apply ability buffs (DamageDealer +10% HP, Evolotion +10% DMG)
 *
 * This is an "ablation study": it isolates the impact of abilities under the same combat rules.
 */
public class Analysis {

    /** Difficulty -> number of enemies spawned in combat (matches game logic) */
    private static int enemiesForDifficulty(int difficulty) {
        return switch (difficulty) {
            case 1 -> 3;   // Easy
            case 2 -> 5;   // Medium
            case 3 -> 7;   // Hard
            case 4 -> 10;  // Impossible
            default -> throw new IllegalArgumentException("difficulty must be 1..4");
        };
    }

    /** Result of one simulated fight (survived + remaining HP) */
    private static class SimResult {
        boolean survived;
        double remainingHp;

        SimResult(boolean survived, double remainingHp) {
            this.survived = survived;
            this.remainingHp = remainingHp;
        }
    }

    /**
     * Core combat simulation (mirrors BattleLoc-like behavior, without UI).
     * Notes:
     * - Includes a "hidden enemy attack" (random boolean) before each enemy.
     * - Includes a "dodge" chance that can prevent damage for that enemy encounter.
     */
    private static SimResult simulateFight(Random rng, double playerHealth, double playerDamage, Obstacle obstacle, int difficulty) {

        // Guard: If playerDamage <= 0, enemy HP never decreases -> possible infinite loop (especially with dodge).
        if (playerDamage <= 0) return new SimResult(false, 0);

        double totalHealth = playerHealth;
        double enemyBaseHealth = obstacle.getHealth();
        double enemyDamage = obstacle.getDamage();

        int enemyCount = enemiesForDifficulty(difficulty);

        for (int i = 0; i < enemyCount; i++) {

            // Hidden enemy attack before the encounter
            if (rng.nextBoolean()) {
                totalHealth -= enemyDamage;
                if (totalHealth <= 0) return new SimResult(false, 0);
            }

            // Dodge chance (similar to random.nextInt(9) == 0)
            boolean dodgeAll = (rng.nextInt(9) == 0);

            double enemyHealth = enemyBaseHealth;

            // Fight loop: player attacks; enemy attacks unless dodge triggers
            while (enemyHealth > 0 && totalHealth > 0) {
                enemyHealth -= playerDamage;

                // Mirrors your original behavior: player can still take damage in the same turn
                if (!dodgeAll) {
                    totalHealth -= enemyDamage;
                }

                if (totalHealth <= 0) return new SimResult(false, 0);
            }
        }

        return new SimResult(true, totalHealth);
    }

    /** A character build under the 30-point system used in your game */
    private static class Build {
        int health;              // 1 point = 1 HP
        int damage;              // 1 damage = 3 points
        boolean damageDealer;    // +10% HP when enabled
        boolean evolotion;       // +10% damage when enabled

        double finalHealth(boolean abilitiesEnabled) {
            double h = health;
            if (abilitiesEnabled && damageDealer) h += h * 0.1;
            return h;
        }

        double finalDamage(boolean abilitiesEnabled) {
            double d = damage;
            if (abilitiesEnabled && evolotion) d += d * 0.1;
            return d;
        }

        @Override
        public String toString() {
            return "H=" + health + ", D=" + damage + ", DD=" + damageDealer + ", EVO=" + evolotion;
        }
    }

    /**
     * Generates a random build under the 30-point rule.
     * Important: avoids damage=0 when possible, to prevent degenerate cases.
     */
    private static Build randomBuild(Random rng) {
        Build b = new Build();

        // Allocate health first (1..30)
        b.health = 1 + rng.nextInt(30);
        int remaining = 30 - b.health;

        // Allocate damage with remaining points (damage costs 3 points each)
        int maxDamage = remaining / 3;

        // Prefer damage >= 1 when possible
        if (maxDamage <= 0) {
            b.damage = 0;
        } else {
            b.damage = 1 + rng.nextInt(maxDamage); // 1..maxDamage
        }

        int leftover = remaining - 3 * b.damage;

        // Ability selection trigger matches your game: leftover > 10
        if (leftover > 10) {
            b.damageDealer = rng.nextBoolean();
            b.evolotion = rng.nextBoolean();
        } else {
            b.damageDealer = false;
            b.evolotion = false;
        }

        return b;
    }

    /** Estimates survival probability for a single build via repeated simulations */
    private static double survivalRateForBuild(Random rng, Build build, Obstacle enemy, int difficulty, int trials, boolean abilitiesEnabled) {
        int survived = 0;

        for (int i = 0; i < trials; i++) {
            SimResult r = simulateFight(
                    rng,
                    build.finalHealth(abilitiesEnabled),
                    build.finalDamage(abilitiesEnabled),
                    enemy,
                    difficulty
            );
            if (r.survived) survived++;
        }

        return (double) survived / trials;
    }

    public static void main(String[] args) {
        // Optional CLI args: buildSamples trialsPerBuild seed
        int buildSamples = (args.length >= 1) ? Integer.parseInt(args[0]) : 2000;
        int trialsPerBuild = (args.length >= 2) ? Integer.parseInt(args[1]) : 200;
        long seed = (args.length >= 3) ? Long.parseLong(args[2]) : 42;

        System.out.println("Analysis Started");
        System.out.printf("Config: buildSamples=%d, trialsPerBuild=%d, seed=%d%n%n", buildSamples, trialsPerBuild, seed);

        Obstacle[] enemies = { new Zombie(), new Vampire(), new BigSlime() };

        for (int difficulty = 1; difficulty <= 4; difficulty++) {

            for (Obstacle enemy : enemies) {

                // Use a reproducible seed per scenario
                Random rng = new Random(seed + difficulty * 1000 + enemy.getName().hashCode());

                Build bestOff = null;
                double bestRateOff = -1;

                Build bestOn = null;
                double bestRateOn = -1;

                // Random search over valid builds (Monte Carlo search)
                for (int i = 0; i < buildSamples; i++) {
                    Build b = randomBuild(rng);

                    // If damage is forced to 0 (rare), skip this build
                    if (b.damage <= 0) continue;

                    double rateOff = survivalRateForBuild(rng, b, enemy, difficulty, trialsPerBuild, false);
                    if (rateOff > bestRateOff) {
                        bestRateOff = rateOff;
                        bestOff = b;
                    }

                    double rateOn = survivalRateForBuild(rng, b, enemy, difficulty, trialsPerBuild, true);
                    if (rateOn > bestRateOn) {
                        bestRateOn = rateOn;
                        bestOn = b;
                    }
                }

                // Human-readable output: enemy stats + difficulty + OFF/ON + delta
                System.out.printf(
                        "Enemy=%s (H=%.0f D=%.0f) | Difficulty=%d | OFF=%.2f%% | ON=%.2f%% | Δ=%.2f%%%n",
                        enemy.getName(),
                        enemy.getHealth(),
                        enemy.getDamage(),
                        difficulty,
                        bestRateOff * 100.0,
                        bestRateOn * 100.0,
                        (bestRateOn - bestRateOff) * 100.0
                );
            }

            System.out.println("---");
        }
    }
}


