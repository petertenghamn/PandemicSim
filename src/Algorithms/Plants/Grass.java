package Algorithms.Plants;

/**
 * Class used to simulate european grass inside the DynamicAlgorithm
 *
 * @see "https://www.wikiwand.com/en/Festuca_rubra"
 */
public class Grass extends Plant {
    public Grass(int x, int y, int age) {
        super(x, y, SPECIES, AGE_MAX, SIGHT, age);
    }

    private static final double AGE_MAX = 25 ; // see source
    private static final int SIGHT = 1; // MIN = 1
    private static final String SPECIES = "Grass"; // The name of the species of animal

}
