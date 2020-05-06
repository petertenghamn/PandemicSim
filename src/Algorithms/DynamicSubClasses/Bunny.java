package Algorithms.DynamicSubClasses;

/**
 *  Class that is used to simulate a rabbit inside the DynamicAlgorithm
 *
 * @see "https://www.wikiwand.com/en/Domestic_rabbit"
 * @see Algorithms.DynamicSubClasses.Animal
 * @see Algorithms.DynamicAlgorithm
 */
public class Bunny extends Animal {
    public Bunny(int age, int energy, int sexNeed, int hunger, int x, int y) {
        super(age, energy, sexNeed, hunger, x, y);
    }

    // MAX AGE = 2 Years
    private String species = "Bunny";

    public String getSpecies() {
        return species;
    }
}
