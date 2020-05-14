package Algorithms.Plants;

/**
 * Class used to simulate european grass inside the DynamicAlgorithm
 *
 * @see "https://www.wikiwand.com/en/Festuca_rubra"
 */
public class Grass extends Plant {
    public Grass(int x, int y, int age) {
        super(x, y, SPECIES, SIGHT, age, SEEDS_AMOUNT, SAPLING_AGE, MATURE_AGE);
    }

    private static final int SEEDS_AMOUNT = 6;
    private static final int SAPLING_AGE = 1 * 24 * 7 * 2; // 2-4 Weeks after seeds sprout H-D-W-M
    private static final int MATURE_AGE = SAPLING_AGE * 2; // 1 Month after saplings produce seeds
    private static final int SIGHT = 1; // MIN = 1
    private static final String SPECIES = "Grass"; // The name of the species of animal

}
