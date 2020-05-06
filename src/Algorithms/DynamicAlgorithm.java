package Algorithms;

import Algorithms.DynamicSubClasses.Animal;
import Algorithms.DynamicSubClasses.Bunny;
import Algorithms.DynamicSubClasses.Fox;
import Algorithms.DynamicSubClasses.Plant;
import Data.SimVariables;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *  A Ecosystem simulation Algorithm that keeps track of certain attributes as it is iterated.
 *  A map of the simulation is also tracked within the algorithm in contrast to the static algorithm.
 *  for more information on the algorithm see below
 *
 * @see "gras02.pdf"
 */
public class DynamicAlgorithm implements Interface_DynamicAlgorithm {
    public DynamicAlgorithm(int init_MAP_LENGTH, int init_MAP_HEIGHT, int init_Bunny_Pop, int init_Fox_Pop, int init_Grass_Pop) {
        checkConstructorValidity(init_MAP_LENGTH, init_MAP_HEIGHT, init_Bunny_Pop, init_Fox_Pop, init_Grass_Pop);

        MAP_LENGTH = init_MAP_LENGTH;
        MAP_HEIGHT = init_MAP_HEIGHT;
        bunnyPopulation = init_Bunny_Pop;
        foxPopulation = init_Fox_Pop;
        grassPopulation = init_Grass_Pop;

        MAP = new Object[MAP_LENGTH][MAP_HEIGHT];

        System.out.println("-------------------------------------------------");
        System.out.println("*** Initializing Dynamic Algorithm Simulation ***");
        System.out.println("-------------------------------------------------");
        initializeMap();
    }

    @Override
    public SimVariables calculate(SimVariables input) {

        return null;
    }

    // Constants
    public final int MAP_LENGTH; // Number of Columns in grid
    public final int MAP_HEIGHT; // Number of Rows in grid

    // Variables
    public int grassPopulation;
    public int bunnyPopulation;
    public int foxPopulation;

    private int x_Empty; // X Coordinate of found empty cell
    private int y_Empty; // Y Coordinate of found empty cell
    private int x_Prey; // X Coordinate of found prey
    private int y_Prey; // Y Coordinate of found prey

    private ArrayList<Object> spottedSurroundings = new ArrayList<>(); // A list of the current entity spotted surroundings
    private ArrayList<Plant> plants = new ArrayList<>(); // List of all the alive plants in the simulation
    private ArrayList<Plant> babyPlants = new ArrayList<>(); // List of all new plants in this iteration
    private ArrayList<Bunny> bunnies = new ArrayList<>(); // List of all the alive bunnies in the simulation
    private ArrayList<Bunny> babyBunnies = new ArrayList<>(); // List of all the new bunnies in this iteration
    private ArrayList<Fox> foxes = new ArrayList<>(); // List of all the alive foxes in the simulation
    private ArrayList<Fox> babyFoxes = new ArrayList<>(); // List of all the new foxes in this iteration

    // Widgets
    private Random random = new Random();
    public Object[][] MAP;

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be called by the constructor of the class ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     * ** Should ONLY be used by Class constructor ***
     * This method is used by the constructor of the class in order to make sure that none of the following
     * param are < 0
     *
     * @param init_MAP_LENGTH the value sent to the constructor for MAP_LENGTH
     * @param init_MAP_HEIGHT the value sent to the constructor for MAP_HEIGHT
     * @param init_Bunny_Pop  the initial value sent to the constructor
     * @param init_Fox_Pop    the initial value sent to the constructor
     * @param init_Grass_Pop  the initial value sent to the constructor
     */
    private void checkConstructorValidity(int init_MAP_LENGTH, int init_MAP_HEIGHT, int init_Bunny_Pop, int init_Fox_Pop, int init_Grass_Pop) {
        if (init_MAP_LENGTH < 0) {
            throw new IllegalArgumentException("init_MAP_LENGTH " + "| " + init_MAP_LENGTH + " |" + " must NOT be < 0!");
        }
        if (init_MAP_HEIGHT < 0) {
            throw new IllegalArgumentException("init_MAP_HEIGHT " + "| " + init_MAP_HEIGHT + " |" + " must NOT be < 0!");
        }
        if (init_Bunny_Pop < 0) {
            throw new IllegalArgumentException("init_Bunny_Pop " + "| " + init_Bunny_Pop + " |" + " must NOT be < 0!");
        }
        if (init_Fox_Pop < 0) {
            throw new IllegalArgumentException("init_Fox_Pop " + "| " + init_Fox_Pop + " |" + " must NOT be < 0!");
        }
        if (init_Grass_Pop < 0) {
            throw new IllegalArgumentException("init_Grass_Pop " + "| " + init_Grass_Pop + " |" + " must NOT be < 0!");
        }
    }

    /**
     * ** Should ONLY be used by Class constructor ***
     * This method populates the map using the stats sent to the constructor of the class
     */
    private void initializeMap() {
        int column;
        int row;

        Plant plant;
        Bunny bunny;
        Fox fox;

        System.out.print("Generating Plants: ");
        for (int p = 0; p < grassPopulation; p++) {
            column = random.nextInt(MAP_LENGTH);
            row = random.nextInt(MAP_HEIGHT);

            if (MAP[column][row] != null) {
                p--;
            } else {
                plant = new Plant(0, column, row);
                plants.add(plant);
                MAP[column][row] = plant;
                System.out.print(".");
            }
        }
        System.out.println(" Done!");

        System.out.print("Generating Bunnies: ");
        for (int b = 0; b < bunnyPopulation; b++) {
            column = random.nextInt(MAP_LENGTH);
            row = random.nextInt(MAP_HEIGHT);

            if (MAP[column][row] != null) {
                b--;
            } else {
                bunny = new Bunny(0, 100, 0, 0, column, row);
                bunnies.add(bunny);
                MAP[column][row] = bunny;
                System.out.print(".");
            }
        }
        System.out.println(" Done!");

        System.out.print("Generating Foxes: ");
        for (int f = 0; f < foxPopulation; f++) {
            column = random.nextInt(MAP_LENGTH);
            row = random.nextInt(MAP_HEIGHT);

            if (MAP[column][row] != null) {
                f--;
            } else {
                fox = new Fox(0, 100, 0, 0, column, row);
                foxes.add(fox);
                MAP[column][row] = fox;
                System.out.print(".");
            }
        }
        System.out.println(" Done!");

    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be accessed by Predators ***
       ---------------------------------------------------------------------------------------------------
     */

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be accessed by Prey ***
       ---------------------------------------------------------------------------------------------------
     */


    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be accessed by Predators ***
       ---------------------------------------------------------------------------------------------------
     */

    /* ---------------------------------------------------------------------------------------------------
        *** The methods past this section can be used by more than one entity (Predator / Prey / Plant) ***
       ---------------------------------------------------------------------------------------------------
     */

    private Object checkCell(int x, int y){

        // Check if this coordinate has been checked already
        for (Object entity: spottedSurroundings){
            if (entity instanceof Plant){
                Plant plant = (Plant) entity;
                if (plant.getX() == x && plant.getY() == y){
                    // true value is ignored
                    return true;
                }

            } else if (entity instanceof Animal){
                Animal animal = (Animal) entity;
                if (animal.getX() == x && animal.getY() == y){
                    // true value is ignored
                    return true;
                }

            }
        }

        // Bind the search to within the bounds of the map
        if (x >= 0) {
            if (x <= MAP_LENGTH -1) {
                if (y >= 0) {
                    if (y <= MAP_HEIGHT -1) {
                        // *** Check Action Begins Here ***
                        if (MAP[x][y] == null){
                            x_Empty = x;
                            y_Empty = y;
                        }

                        return MAP[x][y];

                    } else {
                        return checkCell(x, MAP_HEIGHT -1);
                    }
                } else {
                    return checkCell(x, 0);
                }
            } else {
                return checkCell(MAP_LENGTH-1, y);
            }
        } else {
            return checkCell(0, y);
        }
    }

    private int checkSurroundings(Plant plant){

        spottedSurroundings.clear();

        int x = plant.getX();
        int y = plant.getY();
        Object spottedObject;

        spottedSurroundings.add(plant);
        spottedObject = checkCell(x, y-1); // UP
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        spottedObject = checkCell(x+1, y-1); // UP - RIGHT
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        spottedObject = checkCell(x+1, y); // RIGHT
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        spottedObject = checkCell(x+1, y+1); // DOWN - RIGHT
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        spottedObject = checkCell(x, y+1); // DOWN
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        spottedObject = checkCell(x-1, y+1); // DOWN - LEFT
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        spottedObject = checkCell(x-1, y); // LEFT
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        spottedObject = checkCell(x-1, y-1); // UP - LEFT
        if (spottedObject == null){
            return 1;
        } else {
            spottedSurroundings.add(spottedObject);
        }

        if (spottedObject == null){
            return 1;
        } else {
            return 0;
        }
    }

    private int checkSurroundings(Animal animal){
        return 0;
    }

    private void calculateEnergy(Animal animal) {

    }

    private void calculateSexualNeed(Animal animal) {

    }

    private void calculateEvasion() {

    }

    private void calculateHunger(Animal animal) {

    }

    private void movement() {

    }

    private void rest() {

    }

    private void reproduce(Plant plant){
        Plant babyPlant = new Plant(0,x_Empty,y_Empty);
        MAP[x_Empty][y_Empty] = babyPlant;
        babyPlants.add(babyPlant);
    }

    private void reproduce(Animal animal) {

    }

    private void age(Plant plant){
        plant.setAge(plant.getAge()+1);
    }

    private void age(Animal animal) {
        animal.setAge(animal.getAge()+1);
    }

    /**
     *  TODO this class will be replaced by calculate as shown in the interface
     */
    public void iterate() {

        int emptyCount;

        for (Plant plant: plants){
            age(plant);
            emptyCount = checkSurroundings(plant);
            if (emptyCount > 0){
                reproduce(plant);
            }
        }

        plants.addAll(babyPlants);
        babyPlants.clear();
    }

    /** *** TEMPORARY SOLUTION ***
     * TODO print this to the user instead of the console
     */
    public void printMAP(){

        // Printing the map
        System.out.println("-------------------------------------------------");
        for (int row = 0; row < MAP_HEIGHT; row++) {
            System.out.println();
            for (int column = 0; column < MAP_LENGTH; column++) {
                if (column == 0) {
                    System.out.print("| ");
                }

                if (MAP[column][row] instanceof Plant){
                    System.out.print("Grass" + " | ");
                }
                else if (MAP[column][row] instanceof Bunny){
                    System.out.print("Bunny" + " | ");
                }
                else if (MAP[column][row] instanceof Fox){
                    System.out.print("Fox" + " | ");
                }
                else {
                    System.out.print(MAP[column][row] + " | ");
                }
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("-------------------------------------------------");
    }

    public static void main(String[] args) {
        DynamicAlgorithm simulation = new DynamicAlgorithm(3, 5, 0, 0, 1);

        int TOTAL_ITERATIONS = 6;

        for (int i = 0; i < TOTAL_ITERATIONS; i++){
            simulation.printMAP();
            simulation.iterate();
        }
        simulation.printMAP();
    }
}
