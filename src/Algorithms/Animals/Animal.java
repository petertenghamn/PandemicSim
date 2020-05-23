package Algorithms.Animals;

import Algorithms.Plants.Plant;

import java.util.ArrayList;

/**
 *  The Foundation and master class for Animals used in simulation inside the DynamicAlgorithm
 *
 * @see Algorithms.DynamicAlgorithm
 * @see Plant
 */
public abstract class Animal {
    public Animal(int x, int y, String species, double maxAge, int sight, double age, int speed, boolean female, int energy, int sexNeed, int hunger, String diet, int gestationMax, int literSize, int maxBabyAge, int maxYAAge) {
        this.x = x;
        this.y = y;
        this.age = age;
        this.energy = energy;
        this.sexNeed = sexNeed;
        this.hunger = hunger;
        this.female = female;


        this.maxBabyAge = maxBabyAge;
        this.maxYAAge = maxYAAge;
        this.gestationMax = gestationMax;
        this.literSize = literSize;
        this.diet = diet;
        this.species = species;
        this.maxAge = maxAge;
        this.sight = sight;
        this.speed = speed;
    }

    // Variables
    private int x; // MIN = 0 | MAX = GRID_LENGTH
    private int y; // MIN = 0 | MAX = GRID_HEIGHT
    private double age; // MIN = 0 | MAX depends on species
    private boolean resting; // Determines if the animal can move or not
    private boolean female; // The determination of the animal's sex True = Female | False = Male
    private boolean pregnant; // If the animal is expecting babies
    private boolean alpha; // It the animal is the leader of the territory
    private String lifeStage; // Baby - must stay with parents | Young Adult - focus of becoming Alpha | Adult - focus on Reproduction
    private String status; // Exploring | Hunting | Eating | Drinking | Mating | ect.
    private int gestation; // Time from start of pregnancy (0) to time of birth (gestationMaX)
    private int energy; // MIN = 0 | MAX = 100
    private int speed; // The amount of spaces the that the animal can move | MIN = 1 |
    private int sexNeed; // MIN = 0 | MAX = 100

    private int hunger; // MIN = 0 | MAX = 100
    private int starvingCounter; // Turns that the animal has been at 100 hunger

    private int maxBabyAge;
    private int maxYAAge;

    private Animal mate; // tracks the mate of the animal
    private ArrayList<Animal> babies = new ArrayList<>(); // tracks of the animal's babies
    private ArrayList<Animal> parents = new ArrayList<>(); // tracks the animal's parents

    // *** Initialized in Constructor of SubClass ***
    private String diet; // Type of diet Carnivore | Omnivore | Herbivore
    private String species; // Name of the species
    private double maxAge; // Maximum age of the species
    private int gestationMax; // MAX time of being pregnant
    private int literSize; // MAX number of babies per pregnancy
    private int sight; // How many grid spaces the animal can spot other entities hardcoded MAX = 3 see DynamicAlgorithm

    public int getX() {
        return x;
    }

    public int getSpeed() {
        return speed;
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

    public ArrayList<Animal> getBabies() {
        return babies;
    }

    public ArrayList<Animal> getParents() {
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

    public boolean isAlpha() {
        return alpha;
    }

    public int getStarvingCounter() {
        return starvingCounter;
    }

    public void setStarvingCounter(int starvingCounter) {
        this.starvingCounter = starvingCounter;
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

    public int getGestation() {
        return gestation;
    }

    public int getGestationMax() {
        return gestationMax;
    }

    public int getMaxBabyAge() {
        return maxBabyAge;
    }

    public int getMaxYAAge() {
        return maxYAAge;
    }

    public int getLiterSize() {
        return literSize;
    }

    public Animal getMate() {
        return mate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void setMate(Animal mate) {
        this.mate = mate;
    }

    public void setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
    }

    public void addBabies(ArrayList<Animal> babies) {
        this.babies.addAll(babies);
    }

    public void addParents(ArrayList<Animal> parents) {
        this.parents.addAll(parents);
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    public void setGestation(int gestation) {
        this.gestation = gestation;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }
}