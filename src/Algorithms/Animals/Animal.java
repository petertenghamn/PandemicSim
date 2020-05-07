package Algorithms.Animals;

import Algorithms.Plants.Plant;

/**
 *  The Foundation and master class for Animals used in simulation inside the DynamicAlgorithm
 *
 * @see Algorithms.DynamicAlgorithm
 * @see Plant
 */
public abstract class Animal {
    public Animal(int x, int y, String species, double maxAge, int sight, double age, int energy, int sexNeed, int hunger) {
        this.x = x;
        this.y = y;
        this.age = age;
        this.energy = energy;
        this.sexNeed = sexNeed;
        this.hunger = hunger;

        this.species = species;
        this.maxAge = maxAge;
        this.sight = sight;
    }

    // Variables
    private int x; // MIN = 0 | MAX = GRID_LENGTH
    private int y; // MIN = 0 | MAX = GRID_HEIGHT
    private double age; // MIN = 0 | MAX depends on species
    private int energy; // MIN = 0 | MAX = 100
    private int sexNeed; // MIN = 0 | MAX = 100
    private int hunger; // MIN = 0 | MAX = 100

    // *** Generated in Constructor of SubClass ***
    private String species; // Name of the species
    private double maxAge; // Maximum age of the species
    private int sight; // How many grid spaces the animal can spot other entities

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getAge() {
        return age;
    }

    public int getEnergy() {
        return energy;
    }

    public int getSexNeed() {
        return sexNeed;
    }

    public int getHunger() {
        return hunger;
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

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setSexNeed(int sexNeed) {
        this.sexNeed = sexNeed;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }
}