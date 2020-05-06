package Algorithms.DynamicSubClasses;

/**
 * Class used to simulate a red fox inside the DynamicAlgorithm
 *
 * @see "https://www.wikiwand.com/en/Red_fox"
 * @see Algorithms.DynamicSubClasses.Animal
 * @see Algorithms.DynamicAlgorithm
 */
public class Fox extends Animal {
    public Fox(int age, int energy, int sexNeed, int hunger, int x, int y) {
        super(age, energy, sexNeed, hunger, x, y);
    }

    // MAX AGE = 5 Years
    private String species = "Fox";

    public String getSpecies() {
        return species;
    }
}
