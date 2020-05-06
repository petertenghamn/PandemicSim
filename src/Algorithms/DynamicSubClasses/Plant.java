package Algorithms.DynamicSubClasses;

/**
 *  A Base class for Plants used for simulation inside the DynamicAlgorithm
 *  *** currently there is no plan to make other types of plants ***
 *
 *  TODO add more variety in plants following the Animal Template
 * @see Animal
 * @see Algorithms.DynamicAlgorithm
 */
public class Plant {
    public Plant(int age, int x, int y) {
        this.age = age;
        this.x = x;
        this.y = y;
    }

    private String species = "Grass";
    private int age; // MIN 0 | MAX 25 years
    private int x; // MIN 0 | MAX GRID_LENGTH
    private int y; // MIN 0 | MAX GRID_HEIGHT


    public String getSpecies() {
        return species;
    }

    public int getAge() {
        return age;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
