package Algorithms.Animals;

/**
 *  Class that is used to simulate a rabbit inside the DynamicAlgorithm
 *
 * @see "https://www.wikiwand.com/en/European_rabbit"
 * @see Animal
 * @see Algorithms.DynamicAlgorithm
 */
public class Bunny extends Animal {
    public Bunny(int x, int y, int age, boolean isFemale) {
        super(x, y, SPECIES, AGE_MAX, SIGHT, age,isFemale,100, 0, 0);
    }

    private static final int AGE_MAX = 9 ; // see source
    private static final int SIGHT = 2; // MIN = 1
    private static final String SPECIES = "Bunny"; // The name of the species of animal
}
