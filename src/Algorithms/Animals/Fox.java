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
        super(x, y, SPECIES, AGE_MAX, SIGHT, age , SPEED, isFemale,100, 0, 0, DIET,
                GESTATION_TIME, LITER_SIZE_MAX, AGE_BABY_MAX, AGE_YA_MAX);
    }

    private static final int AGE_MAX = 5 * 24 * 7 * 4 * 12; // 5 Years
    private static final int AGE_BABY_MAX = 24 * 7 * 4; // 1 Month
    private static final int AGE_YA_MAX = 7 * 24 * 7 * 4; // 7 Months
    private static final int SIGHT = 5; // MIN = 1 | MAX = 5
    private static final int SPEED = 3; // MIN = 1
    private static final int GESTATION_TIME = 45 * 24; // Time it takes for babies to be born from start pregnancy (49->58 days)
    private static final int LITER_SIZE_MAX = 6; // The MAX number of babies per pregnancy
    private static final String SPECIES = "Fox"; // The name of the species of animal
    private static final String DIET = "Carnivore"; // Will only eat Animals TODO implement omnivore
}
