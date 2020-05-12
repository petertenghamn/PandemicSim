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
        super(x, y, SPECIES, AGE_MAX, SIGHT, age,isFemale,100, 0, 0, DIET, GESTATION_TIME, LITER_SIZE_MAX);
    }

    private static final int AGE_MAX = 9 * 24 * 365; // 9 Years
    private static final int SIGHT = 2; // MIN = 1
    private static final int GESTATION_TIME = 31 * 24; // Time it takes for babies to be born from start pregnancy (31 days)
    private static final int LITER_SIZE_MAX = 12; // The MAX number of babies per pregnancy
    private static final String SPECIES = "Bunny"; // The name of the species of animal
    private static final String DIET = "Herbivore"; // Will only eat Plants
}
