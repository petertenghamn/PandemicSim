package Algorithms.Animals;

/**
 * Class used to simulate a red fox inside the DynamicAlgorithm
 *
 * @see "https://www.wikiwand.com/en/Red_fox"
 * @see Animal
 * @see Algorithms.DynamicAlgorithm
 */
public class Fox extends Animal {
    public Fox(int x, int y, int age, boolean isFemale) {
        super(x, y, SPECIES, AGE_MAX, SIGHT, age, isFemale,100, 0, 0, DIET);
    }

    private static final int AGE_MAX = 5 ; // see source 5 Years
    private static final int SIGHT = 3; // MIN = 1
    private static final String SPECIES = "Fox"; // The name of the species of animal
    private static final String DIET = "Carnivore"; // Will only eat Animals TODO implement omnivore
}
