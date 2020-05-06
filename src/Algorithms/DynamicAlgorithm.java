package Algorithms;

import Algorithms.DynamicSubClasses.Animal;
import Algorithms.DynamicSubClasses.Bunny;
import Algorithms.DynamicSubClasses.Fox;
import Algorithms.DynamicSubClasses.Plant;
import Data.SimVariables;

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
    public DynamicAlgorithm(int init_MAP_LENGTH, int init_MAP_HEIGHT, int init_Grass_Pop, int init_Bunny_Pop, int init_Fox_Pop) {
        checkConstructorValidity(init_MAP_LENGTH, init_MAP_HEIGHT, init_Bunny_Pop, init_Fox_Pop, init_Grass_Pop);

        MAP_LENGTH = init_MAP_LENGTH;
        MAP_HEIGHT = init_MAP_HEIGHT;

        MAP = new Object[MAP_LENGTH][MAP_HEIGHT];

        System.out.println("-------------------------------------------------");
        System.out.println("*** Initializing Dynamic Algorithm Simulation ***");
        System.out.println("-------------------------------------------------");
        initializeMap(init_Grass_Pop,init_Bunny_Pop, init_Fox_Pop);
    }

    /**
     * The main method (of 2) that communicates with outside of this class
     * This method iterates through the simulation updating population values based on changing variables
     *
     *          MAP:
     *                  simulation is ran on a grid which
     *                  affects ALL the following dynamically depending on position
     *
     *          Grass:
     *                  reproduction
     *                  TODO age
     *          Bunny:
     *                  reproduction
     *                  energy levels
     *                  hunger levels
     *                  predator evasion
     *                  TODO age
     *                  TODO sight
     *          Fox:
     *                  reproduction
     *                  energy levels
     *                  hunger levels
     *                  prey chase
     *                  TODO sight
     *
     *
     * @param input the simulationVariables before the iteration
     * @return updated simulationVariables after the iteration
     */
    @Override
    public SimVariables calculate(SimVariables input) {

        // used to track empty cells for movement and plant reproduction
        int emptyCount;

        // Iterates through plant turns
        for (Plant plant: grass){
            age(plant);

            // Plants reproduce if any cell around them is NULL
            emptyCount = checkSurroundings(plant);
            if (emptyCount > 0){
                reproduce(plant);
            }
        }

        // When babyGrass becomes adults add them to the grass list
        // TODO implement aging
        grass.addAll(babyGrass);
        babyGrass.clear();

        // Updates the SimVariables
        input.grass = grass.size();
        input.bunnies = bunnies.size();
        input.foxes = foxes.size();

        return input;
    }

    // Constants
    public final int MAP_LENGTH; // Number of Columns in grid
    public final int MAP_HEIGHT; // Number of Rows in grid

    // Variables
    private int x_Empty; // X Coordinate of found empty cell
    private int y_Empty; // Y Coordinate of found empty cell
    private int x_Prey; // X Coordinate of found prey
    private int y_Prey; // Y Coordinate of found prey

    private ArrayList<Object> spottedSurroundings = new ArrayList<>(); // A list of the current entity spotted surroundings
    private ArrayList<Plant> grass = new ArrayList<>(); // List of all the alive grass in the simulation
    private ArrayList<Plant> babyGrass = new ArrayList<>(); // List of all new grass in this iteration
    private ArrayList<Bunny> bunnies = new ArrayList<>(); // List of all the alive bunnies in the simulation
    private ArrayList<Bunny> babyBunnies = new ArrayList<>(); // List of all the new bunnies in this iteration
    private ArrayList<Fox> foxes = new ArrayList<>(); // List of all the alive foxes in the simulation
    private ArrayList<Fox> babyFoxes = new ArrayList<>(); // List of all the new foxes in this iteration

    // Widgets
    private Random random = new Random(); // Random Generator used in random placement in initialization
    public Object[][] MAP; // The environment in which the simulation takes place

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be called by the constructor of the class ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     * *** Should ONLY be used by Class constructor ***
     * This method is used by the constructor of the class as a verification of the parameters
     *
     *                      parameters must be >= 0
     *           Population parameter must be <= MAP area (Length * Height)
     *
     * @param init_MAP_LENGTH the value sent to the constructor for MAP_LENGTH
     * @param init_MAP_HEIGHT the value sent to the constructor for MAP_HEIGHT
     * @param init_Bunny_Pop  the initial value sent to the constructor
     * @param init_Fox_Pop    the initial value sent to the constructor
     * @param init_Grass_Pop  the initial value sent to the constructor
     * @throws IllegalArgumentException if parameters don't meet above requirements
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
        if (init_Bunny_Pop + init_Fox_Pop + init_Grass_Pop > init_MAP_LENGTH * init_MAP_HEIGHT){
            throw new IllegalArgumentException("total simulation population higher than map area " +
                    "| " + init_MAP_LENGTH * init_MAP_HEIGHT + " |");
        }
    }

    /**
     * *** Should ONLY be used by Class constructor ***
     * This method populates the map using the stats sent to the constructor of the class
     */
    private void initializeMap(int grassPopulation, int bunnyPopulation, int foxPopulation) {
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
                grass.add(plant);
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
        *** The methods in this section should only be accessed by Plants ***
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

    /**
     * Checks the cell at the parameter coordinates.
     * If the item is present inside the spottedSurroundings then it is ignored.
     * Else the coordinates are verified to be inside the map bounds
     *
     * If the coordinates are inside the map bounds and the item hasn't been checked the item is returned
     *  *** The item is not added to spottedSurroundings in this method ***
     *  certain coordinates will be stored class wide such as:
     *                  If there is no item (NULL) the coordinates will be saved x_Empty | y_Empty
     *                  IF item is a prey (Bunny) the coordinates will be saved x_Prey | y_Prey
     *
     *  Else coordinates are not inside map bounds a recursive call to this method will be sent with bounded coordinates
     *
     * @param x x-Coordinate of the item that will be checked
     * @param y y-Coordinate of the item that will be checked
     * @return true if item has been checked else return spotted Object
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
                        } else if (MAP[x][y] instanceof Bunny){
                            x_Prey = x;
                            y_Prey = y;
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

        return 0;
    }

    private int checkSurroundings(Animal animal){
        return 0;
    }

    private void calculateEnergy(Animal animal) {

    }

    private void calculateSexualNeed(Animal animal) {

    }

    private void calculateEvasion(Animal prey, Animal predator) {

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
        babyGrass.add(babyPlant);
    }

    private void reproduce(Animal animal) {

    }

    private void age(Plant plant){
        plant.setAge(plant.getAge()+1);
    }

    private void age(Animal animal) {
        animal.setAge(animal.getAge()+1);
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

    /**
     *  *** TEST purposes ONLY ***
     *  Acts as a template for how the DynamicAlgorithm class should work in other classes
     *
     * TODO
     */
    public static void main(String[] args) {
        // Initialize the SimVariables
        SimVariables simVariables = new SimVariables();
        simVariables.grass = 1;
        simVariables.bunnies = 0;
        simVariables.foxes = 0;

        // This is for testing
        System.out.println("*** Initial Populations ***");
        System.out.println("Plant Population: " + simVariables.grass);
        System.out.println("Bunny Population: " + simVariables.bunnies);
        System.out.println("Fox Population:   " + simVariables.foxes);

        // Initialize the DynamicAlgorithm Simulation
        // TODO input MAP_LENGTH and MAP_HEIGHT from the user
        DynamicAlgorithm simulation = new DynamicAlgorithm(3, 5,
                                                            simVariables.grass,
                                                            simVariables.bunnies,
                                                            simVariables.foxes);

        int TOTAL_ITERATIONS = 6;

        for (int i = 0; i < TOTAL_ITERATIONS; i++){
            simulation.printMAP();
            simulation.calculate(simVariables);
        }
        simulation.printMAP();

        // Results after the iterations
        System.out.println("Plant Population: " + simVariables.grass);
        System.out.println("Bunny Population: " + simVariables.bunnies);
        System.out.println("Fox Population:   " + simVariables.foxes);
    }
}
