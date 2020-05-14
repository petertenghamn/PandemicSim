package Algorithms.Plants;

import Algorithms.Animals.Animal;

import javax.swing.text.StyledEditorKit;

/**
 *  A Base class for Plants used for simulation inside the DynamicAlgorithm
 *  *** currently there is no plan to make other types of plants ***
 *
 * @see Animal
 * @see Algorithms.DynamicAlgorithm
 */
public abstract class Plant {
    public Plant(int x, int y, String species, int sight, double age, int seedCount, int saplingAge, int matureAge) {
        this.x = x;
        this.y = y;
        this.age = age;
        this.seedCount = seedCount;
        this.seedlingMAX = saplingAge;
        this.saplingMAX = matureAge;
        this.species = species;
        this.sight = sight;
    }

    private int x; // MIN 0 | MAX GRID_LENGTH
    private int y; // MIN 0 | MAX GRID_HEIGHT
    private double age; // MIN 0 | MAX Depends on species

    // *** Generated in Constructor of SubClass ***
    private int seedCount; // Number of seeds the plant has per iteration
    private int seedlingMAX; // Age when a seedling turns into a sapling
    private int saplingMAX; // Age when a sapling turns into a mature plant
    private boolean edible; // If the plant can be eaten as a seedling
    private String lifeStage; // Seedling - must wait and grow | Sapling - edible no reproduction | Mature - edible with reproduction
    private final String species; // Name of the species
    private final int sight; // How many grid spaces the Plant can spot other entities hardcoded MAX of 1 see DynamicAlgorithm

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

    public int getSight() {
        return sight;
    }

    public String getLifeStage() {
        return lifeStage;
    }

    public int getSeedlingMAX() {
        return seedlingMAX;
    }

    public int getSaplingMAX() {
        return saplingMAX;
    }

    public int getSeedCount() {
        return seedCount;
    }

    public boolean isEdible() {
        return edible;
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

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }

    public void setEdible(boolean edible) {
        this.edible = edible;
    }
}
