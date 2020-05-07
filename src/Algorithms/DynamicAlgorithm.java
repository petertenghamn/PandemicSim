package Algorithms;

import Algorithms.Animals.Animal;
import Algorithms.Animals.Bunny;
import Algorithms.Animals.Fox;
import Algorithms.Plants.Grass;
import Algorithms.Plants.Plant;
import Data.SimVariables;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
     *                  Entities movements and search patterns are randomized
     *
     *          Grass:
     *                  age
     *                  sight
     *                  reproduction
     *                  dying of old age
     *                  TODO IMPLEMENT Life Stages ( BABY | YOUNG ADULT | ADULT )
     *
     *          Bunny:
     *                  TODO age
     *                  TODO sight
     *                  TODO gender
     *                  TODO reproduction
     *                  TODO dying of old age
     *                  TODO food search
     *                  TODO energy levels
     *                  TODO hunger levels
     *                  TODO predator evasion
     *                  TODO IMPLEMENT Life Stages ( BABY | YOUNG ADULT | ADULT )
     *
     *          Fox:
     *                  TODO age
     *                  TODO sight
     *                  TODO gender
     *                  TODO reproduction
     *                  TODO dying of old age
     *                  TODO food search
     *                  TODO energy levels
     *                  TODO hunger levels
     *                  TODO IMPLEMENT Life Stages ( BABY | YOUNG ADULT | ADULT )
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

        // *** Remove all dead things before updating the map ***

        // Removes deadPlants from their respective ArrayLists
        grass.removeAll(deadPlants);

        // Removes deadAnimals from their respective ArrayLists
        bunnies.removeAll(deadAnimals);
        foxes.removeAll(deadAnimals);

        updateMap();

        // Updates the SimVariables
        input.grass = grass.size();
        input.bunnies = bunnies.size();
        input.foxes = foxes.size();

        // *** New entities must be added after SimVariables have been updated ***

        grass.addAll(babyGrass); // When babyGrass becomes adults add them to the grass list

        // Clear every List
        deadPlants.clear(); // Clear the List of deadPlants when all the plants have been removed from other Lists
        deadAnimals.clear(); // Clear the list of deadAnimals when all the animals have been removed from other Lists
        babyGrass.clear(); // Clear List of babyAnimals when all new entities have been added to other lists


        return input;
    }

    // Constants
    public final int MAP_LENGTH; // Number of Columns in grid
    public final int MAP_HEIGHT; // Number of Rows in grid
    private final int DIRECTIONS_CARDINAL = 8; // Direction an Object can look (1=UP | 2=UP-RIGHT | 3=RIGHT | 4=DOWN-RIGHT | 5=DOWN | 6=DOWN-LEFT | 7=LEFT | 8=UP-LEFT)

    // Variables
    private int x_Empty; // X Coordinate of spotted empty cell
    private int y_Empty; // Y Coordinate of spotted empty cell
    private int x_Prey; // X Coordinate of spotted prey
    private int y_Prey; // Y Coordinate of spotted prey
    private int x_Mate; // X Coordinate of spotted mate
    private int y_Mate; // Y Coordinated of spotted mate

    private ArrayList<Object> spottedSurroundings = new ArrayList<>(); // A list of the current entity spotted surroundings

    private ArrayList<Plant> deadPlants = new ArrayList<>(); // List of all dead grass in this iteration

    private ArrayList<Grass> grass = new ArrayList<>(); // List of all the alive grass in the simulation
    private ArrayList<Grass> babyGrass = new ArrayList<>(); // List of all new grass in this iteration

    private ArrayList<Animal> deadAnimals = new ArrayList<>(); // List of all dead animals in this iteration

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

        Grass grass = new Grass(0, 0,0);
        Bunny bunny = new Bunny(0, 0, 0);
        Fox fox = new Fox(0, 0, 0);

        System.out.print("Generating Plants: ");
        for (int p = 0; p < grassPopulation; p++) {
            column = random.nextInt(MAP_LENGTH);
            row = random.nextInt(MAP_HEIGHT);

            if (MAP[column][row] != null) {
                p--;
            } else {
                grass = new Grass(column, row, random.nextInt((int) grass.getMaxAge()));
                this.grass.add(grass);
                MAP[column][row] = grass;
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
                bunny = new Bunny(column, row, random.nextInt((int) bunny.getMaxAge()));
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
                fox = new Fox(column, row, random.nextInt((int) fox.getMaxAge()));
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

    private int checkSurroundings(Plant plant){

        ArrayList<Integer> checkingOrder = generateRandomOrder(plant.getSight()); // The order which cells will be checked
        Object spottedObject = plant;

        // New object is searching so it hasn't spotted anything
        spottedSurroundings.clear();

        spottedSurroundings.add(plant);

        // Goes through all the cells the Plant can see
        for (int checking: checkingOrder){

            // The possible cells that the Plant can check
            switch (checking){
                case 1:{
                    spottedObject = checkCell(plant.getX(), plant.getY()-1); // UP
                    break;
                }
                case 2:{
                    spottedObject = checkCell(plant.getX()+1, plant.getY()-1); // UP - RIGHT
                    break;
                }
                case 3:{
                    spottedObject = checkCell(plant.getX()+1, plant.getY()); // RIGHT
                    break;
                }
                case 4:{
                    spottedObject = checkCell(plant.getX()+1, plant.getY()+1); // DOWN - RIGHT
                    break;
                }
                case 5:{
                    spottedObject = checkCell(plant.getX(), plant.getY()+1); // DOWN
                    break;
                }
                case 6:{
                    spottedObject = checkCell(plant.getX()-1, plant.getY()+1); // DOWN - LEFT
                    break;
                }
                case 7:{
                    spottedObject = checkCell(plant.getX()-1, plant.getY()); // LEFT
                    break;
                }
                case 8:{
                    spottedObject = checkCell(plant.getX()-1, plant.getY()-1); // UP - LEFT
                    break;
                }
            }

            // True value is ignored because it means it found itself
            if (!(spottedObject instanceof Boolean)) {

                // Stops searching if the cell checked is empty
                if (spottedObject == null) {
                    return 1;
                } else {
                    spottedSurroundings.add(spottedObject);
                }
            }
        }

        return 0;
    }

    private int checkSurroundings(Animal animal){

        ArrayList<Integer> checkingOrder = generateRandomOrder(animal.getSight()); // The order which cells will be checked
        Object spottedObject = animal;

        // New object is searching so it hasn't spotted anything
        spottedSurroundings.clear();

        // Adds itself as a spotted object
        spottedSurroundings.add(spottedObject);

        // Goes through all the cells the Plant can see
        for (int checking: checkingOrder){

            // The possible cells that the Plant can check
            switch (checking){
                case 1:{
                    spottedObject = checkCell(animal.getX(), animal.getY()-1); // UP
                    break;
                }
                case 2:{
                    spottedObject = checkCell(animal.getX()+1, animal.getY()-1); // UP - RIGHT
                    break;
                }
                case 3:{
                    spottedObject = checkCell(animal.getX()+1, animal.getY()); // RIGHT
                    break;
                }
                case 4:{
                    spottedObject = checkCell(animal.getX()+1, animal.getY()+1); // DOWN - RIGHT
                    break;
                }
                case 5:{
                    spottedObject = checkCell(animal.getX(), animal.getY()+1); // DOWN
                    break;
                }
                case 6:{
                    spottedObject = checkCell(animal.getX()-1, animal.getY()+1); // DOWN - LEFT
                    break;
                }
                case 7:{
                    spottedObject = checkCell(animal.getX()-1, animal.getY()); // LEFT
                    break;
                }
                case 8:{
                    spottedObject = checkCell(animal.getX()-1, animal.getY()-1); // UP - LEFT
                    break;
                }
            }

            // True value is ignored because it means it found itself
            if (!(spottedObject instanceof Boolean)) {

                // Stops searching if the cell checked is empty
                if (spottedObject == null) {
                    return 1;
                } else {
                    spottedSurroundings.add(spottedObject);
                }
            }
        }

        // If no empty cells were found
        return 0;
    }

    /**
     * A tool which randomly generates an order in which the cells will be checked
     * The amount of cells depends on the basic 8 DIRECTIONS_CARDINAL + sight of the Entity
     * order of cells is checked for duplicates before returning the list
     *
     * @param sight the sight of the entity MIN is 0 which is adjacent squares
     * @return the ArrayList of the order cells will be checked
     */
    private ArrayList<Integer> generateRandomOrder(int sight){

        ArrayList<Integer> randomOrder = new ArrayList<>();

        while (randomOrder.size() < DIRECTIONS_CARDINAL + sight) {

            // .nextInt((max - min) + 1) + min = Range min -> max inclusive
            int randomCell = random.nextInt(((DIRECTIONS_CARDINAL + sight) - 1) + 1) + 1;


            // Checks if that number has been generated
            if (!randomOrder.contains(randomCell)) {
                randomOrder.add(randomCell);
            }
        }

        return randomOrder;
    }

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
            if (x < MAP_LENGTH) {
                if (y >= 0) {
                    if (y < MAP_HEIGHT) {

                        // *** Check Action Begins Here ***
                        if (MAP[x][y] == null){
                            x_Empty = x;
                            y_Empty = y;
                        }
                        else if (MAP[x][y] instanceof Bunny){

                            // Used by Predators
                            x_Prey = x;
                            y_Prey = y;

                            // Used by Male Bunnies
                            x_Mate = x;
                            y_Mate = y;
                        }
                        else if (MAP[x][y] instanceof Fox){

                            // Used by Male Foxes
                            x_Mate = x;
                            y_Mate = y;
                        }

                        return MAP[x][y];

                    } else {
                        return checkCell(x, MAP_HEIGHT-1);
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

    /**
     * The reproduction method which updates the ArrayList babyGrass and places the seedling on the map
     * Plants reproduce by finding any emptySpace
     * TODO add more complexity to plant reproduction
     *
     * @param plant the plant which is reproducing
     */
    private void reproduce(Plant plant){

        // Checks which species of plant it is
        if (plant instanceof Grass){
            Grass seedling = new Grass(x_Empty,y_Empty,0);
            MAP[seedling.getX()][seedling.getY()] = seedling;
            babyGrass.add(seedling);
        }
    }

    private void reproduce(Animal animal) {

    }

    /**
     * Plant aging which calculates a death chance based on the plant's age.
     * The closer it gets to it's MAX AGE the higher the death chance
     *
     * @param plant the plant to be aged by 1
     */
    private void age(Plant plant){

        // Age one iteration
        plant.setAge(plant.getAge() + 1);

        double chanceDeath = plant.getAge() / plant.getMaxAge(); // How close the plant is to MAX age

        // The different cases for the chances that the animal dies the closer it gets to it's MAX age
        if (chanceDeath >= 0.9){
            chanceDeath = chanceDeath * ThreadLocalRandom.current().nextInt(90, 100);
        }
        else if (chanceDeath >= 0.8){
            chanceDeath = chanceDeath * ThreadLocalRandom.current().nextInt(80, 100);
        }
        else if (chanceDeath >= 0.7){
            chanceDeath = chanceDeath * ThreadLocalRandom.current().nextInt(60, 100);
        }
        else if (chanceDeath >= 0.6){
            chanceDeath = chanceDeath * ThreadLocalRandom.current().nextInt(70, 100);
        }
        else if (chanceDeath >= 0.5){
            chanceDeath = chanceDeath * ThreadLocalRandom.current().nextInt(50, 100);
        }


        // When chance of death is higher than 100% the plant dies
        if (chanceDeath >= 100){
            deadPlants.add(plant);
        }
    }

    private void age(Animal animal) {
        animal.setAge(animal.getAge()+1);
    }

    /**
     * Updates the map with the *** dead *** this is done so that dead are not counted during the iteration
     * and will be shown on the graph after the iteration as null
     *
     */
    private void updateMap(){

        // Updates all the dead plants
        for (Plant plant: deadPlants){

            // If the plant is still on the map get rid of it
            if (MAP[plant.getX()][plant.getY()] == plant){
                MAP[plant.getX()][plant.getY()] = null;
            }
        }

        // Updates all the dead animals
        for (Animal animal: deadAnimals){

            // If the plant is still on the map get rid of it
            if (MAP[animal.getX()][animal.getY()] == animal){
                MAP[animal.getX()][animal.getY()] = null;
            }
        }

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
        simVariables.bunnies = 1;
        simVariables.foxes = 1;

        // Initialize the DynamicAlgorithm Simulation
        // TODO input MAP_LENGTH and MAP_HEIGHT from the user
        DynamicAlgorithm simulation = new DynamicAlgorithm(5, 5,
                                                            simVariables.grass,
                                                            simVariables.bunnies,
                                                            simVariables.foxes);

        int TOTAL_ITERATIONS = 10;

        for (int i = 0; i < TOTAL_ITERATIONS; i++){

            simulation.printMAP();
            simulation.calculate(simVariables);

            // Results after the iterations
            System.out.println("Grass Population: " + simVariables.grass);
            System.out.println("Bunny Population: " + simVariables.bunnies);
            System.out.println("Fox Population:   " + simVariables.foxes);
        }
    }
}
