package Algorithms.DynamicSubClasses;

/**
 *  The Foundation and master class for Animals used in simulation inside the DynamicAlgorithm
 *
 * @see Algorithms.DynamicAlgorithm
 */
public abstract class Animal {
    public Animal(int age, int energy, int sexNeed, int hunger, int x, int y) {
        this.age = age;
        this.energy = energy;
        this.sexNeed = sexNeed;
        this.hunger = hunger;
        this.x = x;
        this.y = y;
    }

    // Variables
    private int age; // MIN = 0
    private int energy; // MIN = 0 | MAX = 100
    private int sexNeed; // MIN = 0 | MAX = 100
    private int hunger; // MIN = 0 | MAX = 100
    private int x; // MIN = 0 | MAX = GRID_LENGTH
    private int y; // MIN = 0 | MAX = GRID_HEIGHT

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getSexNeed() {
        return sexNeed;
    }

    public void setSexNeed(int sexNeed) {
        this.sexNeed = sexNeed;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
