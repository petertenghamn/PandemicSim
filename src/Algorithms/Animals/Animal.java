package Algorithms.Animals;

import Algorithms.Plants.Plant;

/**
 *  The Foundation and master class for Animals used in simulation inside the DynamicAlgorithm
 *
 * @see Algorithms.DynamicAlgorithm
 * @see Plant
 */
public abstract class Animal {
    public Animal(int x, int y, String species, double maxAge, int sight, double age, boolean female, int energy, int sexNeed, int hunger, String diet) {
        this.x = x;
        this.y = y;
        this.age = age;
        this.energy = energy;
        this.sexNeed = sexNeed;
        this.hunger = hunger;
        this.female = female;

        this.diet = diet;
        this.species = species;
        this.maxAge = maxAge;
        this.sight = sight;
    }

    // Variables
    private int x; // MIN = 0 | MAX = GRID_LENGTH
    private int y; // MIN = 0 | MAX = GRID_HEIGHT
    private double age; // MIN = 0 | MAX depends on species
    private boolean resting; // Determines if the animal can move or not
    private boolean female; // The determination of the animal's sex True = Female | False = Male
    private boolean pregnant; // If the animal is expecting babies
    private String lifeStage; // Baby - must stay with parents | Young Adult - focus of becoming Alpha | Adult - focus on Reproduction
    private int energy; // MIN = 0 | MAX = 100
    private int sexNeed; // MIN = 0 | MAX = 100
    private int hunger; // MIN = 0 | MAX = 100

    private Animal[] babies; // tracks of the animal's babies
    private Animal[] parents; // tracks the animal's parents

    // *** Initialized in Constructor of SubClass ***
    private String diet; // Type of diet Carnivore | Omnivore | Herbivore
    private String species; // Name of the species
    private double maxAge; // Maximum age of the species
    private int sight; // How many grid spaces the animal can spot other entities hardcoded MAX = 3 see DynamicAlgorithm

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

    public boolean isPregnant() {
        return pregnant;
    }

    public Animal[] getBabies() {
        return babies;
    }

    public Animal[] getParents() {
        return parents;
    }

    public int getHunger() {
        return hunger;
    }

    public String getSpecies() {
        return species;
    }

    public boolean isFemale() {
        return female;
    }

    public String getLifeStage() {
        return lifeStage;
    }

    public double getMaxAge() {
        return maxAge;
    }

    public int getSight() {
        return sight;
    }

    public boolean isResting() {
        return resting;
    }

    public String getDiet() {
        return diet;
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

    public void setResting(boolean resting) {
        this.resting = resting;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public void setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
    }

    public void setBabies(Animal[] babies) {
        this.babies = babies;
    }

    public void setParents(Animal[] parents) {
        this.parents = parents;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }
}