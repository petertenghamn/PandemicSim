package Algorithms.Plants;

import Algorithms.Animals.Animal;

/**
 *  A Base class for Plants used for simulation inside the DynamicAlgorithm
 *  *** currently there is no plan to make other types of plants ***
 *
 * @see Animal
 * @see Algorithms.DynamicAlgorithm
 */
public abstract class Plant {
    public Plant(int x, int y, String species, double maxAge, int sight, double age) {
        this.x = x;
        this.y = y;
        this.age = age;
        this.species = species;
        this.maxAge = maxAge;
        this.sight = sight;
    }

    private int x; // MIN 0 | MAX GRID_LENGTH
    private int y; // MIN 0 | MAX GRID_HEIGHT
    private double age; // MIN 0 | MAX Depends on species

    // *** Generated in Constructor of SubClass ***
    private String species; // Name of the species
    private double maxAge; // Maximum age of the species
    private int sight; // How many grid spaces the Plant can spot other entities hardcoded MAX of 1 see DynamicAlgorithm

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getAge() {
        return age;
    }

    public String getSpecies() {
        return species;
    }

    public double getMaxAge() {
        return maxAge;
    }

    public int getSight() {
        return sight;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setAge(double age) {
        this.age = age;
    }
}
