package Algorithms.Animals;

/**
 * Class used to simulate a red fox inside the DynamicAlgorithm
 *
 * @see "https://www.wikiwand.com/en/Red_fox"
 * @see Animal
 * @see Algorithms.DynamicAlgorithm
 */
public class Fox extends Animal {
    public Fox(int x, int y, int age) {
        super(x, y, SPECIES, AGE_MAX, SIGHT, age,100, 0, 0);
    }

    private static final int AGE_MAX = 5 ; // see source
    private static final int SIGHT = 0; // MIN = 0
    private static final String SPECIES = "Fox"; // The name of the species of animal
}
