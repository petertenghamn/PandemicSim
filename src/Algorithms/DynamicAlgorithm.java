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

        MAP = new ArrayList[MAP_LENGTH][MAP_HEIGHT];

        System.out.println("-------------------------------------------------");
        System.out.println("*** Initializing Dynamic Algorithm Simulation ***");
        System.out.println("-------------------------------------------------");
        initializeMap(init_Grass_Pop,init_Bunny_Pop, init_Fox_Pop);
    }

    // Constants
    public final int MAP_LENGTH; // Number of Columns in grid
    public final int MAP_HEIGHT; // Number of Rows in grid

    // Variables
    private int x_Empty; // X Coordinate of spotted empty cell
    private int y_Empty; // Y Coordinate of spotted empty cell
    private int x_Food; // X Coordinate of spotted prey
    private int y_Food; // Y Coordinate of spotted prey
    private int x_Mate; // X Coordinate of spotted mate
    private int y_Mate; // Y Coordinated of spotted mate

    private ArrayList<Object> spottedObjects = new ArrayList<>(); // A list of the current entity spotted surroundings

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
    private ArrayList<Object>[][] MAP; // The environment in which the simulation takes place

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be accessed by Plants ***
       ---------------------------------------------------------------------------------------------------
     */

    /** *** Part of an Overloaded Method ***
     * Plant aging which calculates a death chance based on the plant's age.
     * The closer it gets to it's MAX AGE the higher the death chance
     *
     * TODO implement Life Stages (seed | sapling | mature)
     *
     * @param plant the plant to be aged by 1
     */
    private void age(Plant plant){

        double MAX_SEEDLING_AGE_PERCENTAGE = 0.02; // MAX age percentage of the "Seedling" lifeStage
        double MAX_SAPLING_AGE_PERCENTAGE = 0.05; // MAX age percentage of the "Sapling" lifeStage

        // Age one iteration
        plant.setAge(plant.getAge() + 1);

        double chanceDeath = plant.getAge() / plant.getMaxAge(); // How close the plant is to MAX age

        // Sets the lifeStage of the plant depending on the percentage of life lived: age / MAX age
        if (chanceDeath <= MAX_SEEDLING_AGE_PERCENTAGE){
            plant.setLifeStage("Seedling");
            plant.setEdible(false);
        }
        else if (MAX_SEEDLING_AGE_PERCENTAGE < chanceDeath && chanceDeath <= MAX_SAPLING_AGE_PERCENTAGE){
            plant.setLifeStage("Sapling");
            plant.setEdible(true);
        }
        else if (MAX_SAPLING_AGE_PERCENTAGE < chanceDeath){
            plant.setLifeStage("Mature");
            plant.setEdible(true);
        }
    }

    /** *** Part of an Overloaded Method ***
     * Plants check their surroundings but stop looking as soon as they find an empty place.
     * Plants search pattern is random and can also be affected by:
     *      sight: MIN - 1 MAX - 1 (hardcoded)
     *
     *  methods used:
     *      generateRandomOrder();
     *      checkCell();
     *
     *  variables changed:
     *      spottedSurroundings
     *
     * @param plant the plant that will look around it's coordinates
     * @return The empty spots that the plant has found (1 | 0)
     */
    private int checkSurroundings(Plant plant){

        ArrayList<Integer> checkingOrder = generateRandomOrder(plant.getSight()); // The order which cells will be checked
        Object spottedObject;

        // New object is searching so it hasn't spotted anything
        spottedObjects.clear();

        spottedObjects.add(plant);

        // Goes through all the cells the Plant can see
        for (int checking: checkingOrder){

            spottedObject = checkCase(checking, plant.getX(), plant.getY());

            // True value is ignored because it means it found itself
            if (!(spottedObject instanceof Boolean)) {

                // Stops searching if the cell checked is empty
                if (spottedObject == null) {
                    return 1;
                } else {
                    spottedObjects.add(spottedObject);
                }
            }
        }

        return 0;
    }

    /** *** Part of an Overloaded Method ***
     * The reproduction method which updates the ArrayList babyGrass and places the seedling on the map
     * Plants reproduce by finding any emptySpace
     *
     * @param plant the plant which is reproducing
     */
    private void reproduce(Plant plant){

        // Only Mature plants can reproduce
        if (plant.getLifeStage().equals("Mature")) {
            // Checks which species of plant it is
            if (plant instanceof Grass) {
                Grass seedling = new Grass(x_Empty, y_Empty, 0);
                MAP[seedling.getX()][seedling.getY()].add(seedling);
                babyGrass.add(seedling);
            }
        }
    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be accessed by Animals ***
       ---------------------------------------------------------------------------------------------------
     */

    /** *** Part of an Overloaded Method ***
     * Animal aging which calculates a death chance based on the animal's age
     * The close it gets to it's MAX AGE the higher the death chance
     *
     * TODO implement Life Stages (baby | young adult | adult)
     *
     * @param animal animal which will be aged
     */
    private void age(Animal animal) {

        double MAX_BABY_AGE_PERCENTAGE = 0.18; // MAX age percentage of the "Baby" lifeStage
        double MAX_YA_AGE_PERCENTAGE = 0.28; // MAX age percentage of the "Young Adult" lifeStage

        // Age one iteration
        animal.setAge(animal.getAge() + 1);

        double deathChance = animal.getAge() / animal.getMaxAge(); // How close the plant is to MAX age

        // Sets the lifeStage of the plant depending on the percentage of life lived: age / MAX age
        if (deathChance <= MAX_BABY_AGE_PERCENTAGE){
            animal.setLifeStage("Baby");
        }
        else if (MAX_BABY_AGE_PERCENTAGE < deathChance && deathChance <= MAX_YA_AGE_PERCENTAGE){
            animal.setLifeStage("Young Adult");
        }
        else if (MAX_YA_AGE_PERCENTAGE < deathChance){
            animal.setLifeStage("Adult");
        }

        // The different cases for the chances that the animal dies the closer it gets to it's MAX age
        if (1 <= deathChance){
            deathChance = deathChance * ThreadLocalRandom.current().nextInt(98, 100); // 1/2 chance it dies
        }
        else if (0.9 <= deathChance){
            deathChance = deathChance * ThreadLocalRandom.current().nextInt(95, 100); // 1/5 chance it dies
        }
        else if (0.8 <= deathChance){
            deathChance = deathChance * ThreadLocalRandom.current().nextInt(90, 100); // 1/10 chance it dies
        }
        else if (0.7 <= deathChance){
            deathChance = deathChance * ThreadLocalRandom.current().nextInt(80, 100); // 1/20 chance it dies
        }
        else if (0.6 <= deathChance){
            deathChance = deathChance * ThreadLocalRandom.current().nextInt(70, 100); // 1/30 chance it dies
        }
        else if (0.5 <= deathChance){
            deathChance = deathChance * ThreadLocalRandom.current().nextInt(50, 100); // 1/50 chance it dies
        }


        // When chance of death is higher than 100% the plant dies
        if (deathChance >= 100){
            deadAnimals.add(animal);
        }
    }

    /** *** Part of an Overloaded Method ***
     * Checks the surrounding cells of the animals based on:
     *      sight: MIN of 1 - MAX of 3 (hardcoded)
     *      animal x & y coordinates
     *
     *  methods used:
     *      generateRandomOrder();
     *      checkCell();
     *      isFood();
     *
     *  variables changed:
     *      x_Food & y_Food - updates the closest food source
     *      spottedSurroundings - updates all spotted objects around
     *
     *
     * @param animal the animal that will look around it's coordinates
     */
    private void checkSurroundings(Animal animal){

        ArrayList<Integer> checkingOrder = generateRandomOrder(animal.getSight()); // The order which cells will be checked
        Object spottedObject = animal;

        // New object is searching so it hasn't spotted anything
        spottedObjects.clear();

        // Adds itself as a spotted object
        spottedObjects.add(spottedObject);

        // Goes through all the cells the Animal can see
        for (int checking: checkingOrder) {

            spottedObject = checkCase(checking, animal.getX(), animal.getY());

            // Checks to see if the spottedObject is food
            if (isFood(spottedObject, animal)){
                if (spottedObject instanceof  Plant){
                    Plant food = (Plant) spottedObject;

                    // Set Coordinates of food
                    x_Food = food.getX();
                    y_Food = food.getY();
                }
                else if (spottedObject instanceof Animal){
                    Animal food = (Animal) spottedObject;

                    // Set Coordinates of food
                    x_Food = food.getX();
                    y_Food = food.getY();
                }
            }

            // True value is ignored because it means it found itself
            if (!(spottedObject instanceof Boolean)) {
                spottedObjects.add(spottedObject);
            }
        }
    }

    /**
     * Determines whether the object parameter is a food source for the animal parameter
     * this is determined using the Animal.diet and the Animal.lifeStage
     * babies can use their parents as a food source
     *
     * TODO determine which Animals / Plants the animal species can eat
     * TODO if parent = male then parent must have food with them
     *
     * @param object The object which is being considered as food
     * @param animal The animal which is looking for food
     * @return True | False depending if it is a viable food source for the Animal
     */
    private boolean isFood(Object object, Animal animal){

        Plant plant;
        Animal prey;

        // Checks to see if the that the object is not empty and is not itself
        if (object != null){
            if (!(object instanceof Boolean)) {

                // Babies use their parents as their food source
                if (animal.getLifeStage().equals("Baby")) {

                    for (Animal parent: animal.getParents()){

                        if (object == parent){
                            return true;
                        }
                    }
                } else {

                    // The Dietary choices available to an animal
                    switch (animal.getDiet()) {
                        case "Herbivore":

                            // Herbivores can eat all plants
                            if (object instanceof Plant) {
                                plant = (Plant) object;

                                // If the plant is at an edible stage return = true;
                                return plant.isEdible();
                            }

                            break;
                        case "Carnivore":

                            // Carnivores can eat all Animals with low chance for cannibalism
                            if (object instanceof Animal) {
                                prey = (Animal) object;

                                if (prey.getSpecies().equals(animal.getSpecies())) {

                                    // Will only result to cannibalism when hunger is unbearable
                                    if (animal.getHunger() >= 99) {
                                        return true;
                                    }
                                    else return false;

                                } else {
                                    return true;
                                }
                            }

                            break;
                        case "Omnivore":

                            // Omnivores can eat all Plants and Animals with low chance for cannibalism
                            if (object instanceof Plant || object instanceof Animal) {

                                prey = (Animal) object;

                                if (prey.getSpecies().equals(animal.getSpecies())) {

                                    // Will only result to cannibalism when hunger is unbearable
                                    if (animal.getHunger() >= 99) {
                                        return true;
                                    }
                                    else return false;

                                } else {
                                    return true;
                                }
                            }

                            break;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Calculates the animal's hunger (range: 0 -> 100) based on its energy levels.
     * The animal has a chance to die (increases with age) if hunger level is at 100
     *
     * TODO Balance for 1 year = 1 iteration
     *
     * @param animal animal which hunger is being calculated for
     */
    private void calculateHunger(Animal animal) {

        double deathChance = animal.getAge() / animal.getMaxAge();
        double energy = animal.getEnergy();
        int hunger = animal.getHunger();

        // The animal has a increasing chance to die based on age when hunger levels are 100
        if (hunger == 100){

            // The different cases for the chances that the animal dies the closer it gets to it's MAX age
            if (1 <= deathChance){
                deathChance = deathChance * ThreadLocalRandom.current().nextInt(98, 100); // 1/2 chance it dies
            }
            else if (0.9 <= deathChance){
                deathChance = deathChance * ThreadLocalRandom.current().nextInt(95, 100); // 1/5 chance it dies
            }
            else if (0.8 <= deathChance){
                deathChance = deathChance * ThreadLocalRandom.current().nextInt(90, 100); // 1/10 chance it dies
            }
            else if (0.7 <= deathChance){
                deathChance = deathChance * ThreadLocalRandom.current().nextInt(80, 100); // 1/20 chance it dies
            }
            else if (0.6 <= deathChance){
                deathChance = deathChance * ThreadLocalRandom.current().nextInt(70, 100); // 1/30 chance it dies
            }
            else if (0.5 <= deathChance){
                deathChance = deathChance * ThreadLocalRandom.current().nextInt(50, 100); // 1/50 chance it dies
            }

            // When chance of death is higher than 100% the plant dies
            if (deathChance >= 100){
                deadAnimals.add(animal);
            }
        }

        // Hunger increases every turn based on animal energy
        // The more energy the animal has the less hunger it gains
        if (90 <= energy){
            // Hunger = Hunger + x
            animal.setHunger(hunger + 1);
        }
        else if (80 <= energy){
            animal.setHunger(hunger + 2);
        }
        else if (70 <= energy){
            animal.setHunger(hunger + 3);
        }
        else if (60 <= energy){
            animal.setHunger(hunger + 4);
        }
        else if (50 <= energy){
            animal.setHunger(hunger + 5);
        }
        else if (40 <= energy){
            animal.setHunger(hunger + 6);
        }
        else if (30 <= energy){
            animal.setHunger(hunger + 7);
        }
        else if (20 <= energy){
            animal.setHunger(hunger + 8);
        }
        else if (10 <= energy){
            animal.setHunger(hunger + 9);
        }
        else{
            animal.setHunger(hunger + 10);
        }
    }

    /**
     * Calculates the animal's energy (range: 0 -> 100) based on its hunger and energy levels.
     * The animal will have an increasing chance to rest depending on its energy level
     * an animal can not rest if:
     *      it is at 100 energy or if it is above a certain hunger
     *      is more hungry than tired
     *
     * an animal which is resting will gain energy
     * an animal which is NOT resting will lose energy
     *
     * TODO Balance for 1 year = 1 iteration
     *
     * @param animal animal which energy is being calculated for
     */
    private void calculateEnergy(Animal animal) {

        int restingChance;
        int energy = animal.getEnergy();
        int hunger = animal.getHunger();

        // Energy is updated every turn if the animal is resting it gaines energy else it loses energy
        if (animal.isResting()){
            // Energy++
            animal.setEnergy(energy + 1);
        } else {
            animal.setEnergy(energy - 1);
        }

        // Depending on the animal's energy and hunger levels there is an increasing chance it will rest
        if (hunger >= 70 || energy >= 100){
            restingChance = 0; // Animal can not rests if it has too much energy or is too hungry
        } else if (hunger > energy){
            restingChance = 0; // Animal won't rests if it's hungrier than tired
        } else if (50 <= energy){
            restingChance = ThreadLocalRandom.current().nextInt(40, 100); // 1 / 60 chance it rests
        } else if (40 <= energy){
            restingChance = ThreadLocalRandom.current().nextInt(50, 100); // 1 / 50 chance it rests
        } else if (30 <= energy){
            restingChance = ThreadLocalRandom.current().nextInt(60, 100); // 1 / 40 chance it rests
        } else if (20 <= energy){
            restingChance = ThreadLocalRandom.current().nextInt(70, 100); // 1 / 30 chance it rests
        } else if (10 <= energy){
            restingChance = ThreadLocalRandom.current().nextInt(80, 100); // 1 / 20 chance it rests
        } else {
            restingChance = ThreadLocalRandom.current().nextInt(90, 100); // 1 / 10 chance it rests
        }


        if (restingChance >= 100){
            animal.setResting(true);
        } else {
            animal.setResting(false);
        }
    }

    // TODO *** WORK IN PROGRESS ***
    private void calculateSexualNeed(Animal animal) {

    }



    // TODO *** WORK IN PROGRESS ***
    private void reproduce(Animal animal) {

    }

    // TODO *** WORK IN PROGRESS ***
    private void movement(Animal animal) {

    }

    // TODO *** WORK IN PROGRESS ***
    private void evasion(Animal prey, Animal predator) {

    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be accessed by Prey ***
       ---------------------------------------------------------------------------------------------------
     */


    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section should only be accessed by Predators ***
       ---------------------------------------------------------------------------------------------------
     */

    /* ---------------------------------------------------------------------------------------------------
        *** The methods past this section are used as tools by the Class ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     * A tool which randomly generates an order in which the cells will be checked
     * The total amount of cells the entity can check depends on the sight of the entity using the formula:
     *              Possible cells = 2(sight^2 + 3sight)
     *
     * order of cells is checked for duplicates before returning the list
     *
     * @param sight the sight of the entity MIN is 0 which is adjacent squares
     * @return the ArrayList of the order cells will be checked
     */
    private ArrayList<Integer> generateRandomOrder(int sight){

        ArrayList<Integer> randomOrder = new ArrayList<>();

        // Mat.pow(base, power) = base^power return type is double
        int totalCells = 2 * (((int) Math.pow(sight, 2)) + 3 * sight);

        while (randomOrder.size() < totalCells) {

            // .nextInt((max - min) + 1) + min = Range min -> max inclusive
            int randomCell = random.nextInt(((totalCells - 1) + 1)) + 1;

            // Checks if that number has been generated
            if (!randomOrder.contains(randomCell)) {
                randomOrder.add(randomCell);
            }
        }

        return randomOrder;
    }

    /**
     * Verifies that the cell the entity wants to check has a coded case
     * then calls the checkCell method to check the coordinate
     *
     * @param checkCell the clockwise cell number
     * @param x the origin x coordinate
     * @param y the origin y coordinate
     * @return the found object * can be null *
     */
    private Object checkCase(int checkCell, int x, int y){

        Object spotted = null;

        // The possible cells that the entity can check
        switch (checkCell){
            case 1: {
                spotted = checkCell(x, y - 1); // UP
                break;
            }
            case 2: {
                spotted = checkCell(x + 1, y - 1); // UP - RIGHT
                break;
            }
            case 3: {
                spotted = checkCell(x + 1, y); // RIGHT
                break;
            }
            case 4: {
                spotted = checkCell(x + 1, y + 1); // DOWN - RIGHT
                break;
            }
            case 5: {
                spotted = checkCell(x, y + 1); // DOWN
                break;
            }
            case 6: {
                spotted = checkCell(x - 1, y + 1); // DOWN - LEFT
                break;
            }
            case 7: {
                spotted = checkCell(x - 1, y); // LEFT
                break;
            }
            case 8: {
                spotted = checkCell(x - 1, y - 1); // UP - LEFT
                break;
            }
            case 9: {
                spotted = checkCell(x, y - 1 - 1); // UP - UP
                break;
            }
            case 10: {
                spotted = checkCell(x + 1, y - 1 - 1); // UP - UP - RIGHT
                break;
            }
            case 11: {
                spotted = checkCell(x + 1 + 1, y - 1); // UP - RIGHT - RIGHT
                break;
            }
            case 12: {
                spotted = checkCell(x + 1 + 1, y); // RIGHT - RIGHT
                break;
            }
            case 13: {
                spotted = checkCell(x + 1 + 1, y + 1); // DOWN - RIGHT - RIGHT
                break;
            }
            case 14: {
                spotted = checkCell(x + 1, y + 1 + 1); // DOWN - DOWN - RIGHT
                break;
            }
            case 15: {
                spotted = checkCell(x, y + 1 + 1); // DOWN - DOWN
                break;
            }
            case 16: {
                spotted = checkCell(x - 1, y + 1 + 1); // DOWN - DOWN - LEFT
                break;
            }
            case 17: {
                spotted = checkCell(x - 1 - 1, y + 1); // DOWN - LEFT - LEFT
                break;
            }
            case 18: {
                spotted = checkCell(x - 1 - 1, y); // LEFT - LEFT
                break;
            }
            case 19: {
                spotted = checkCell(x - 1 - 1, y - 1); // UP - LEFT - LEFT
                break;
            }
            case 20: {
                spotted = checkCell(x - 1, y - 1 - 1); // UP - UP -LEFT
                break;
            }
            case 21: {
                spotted = checkCell(x, y - 1 - 1 - 1); // UP - UP - UP
                break;
            }
            case 22: {
                spotted = checkCell(x + 1, y - 1 - 1 - 1); // UP - UP - UP - RIGHT
                break;
            }
            case 23: {
                spotted = checkCell(x + 1 + 1, y - 1 - 1); // UP - UP - RIGHT - RIGHT
                break;
            }
            case 24: {
                spotted = checkCell(x + 1 + 1 + 1, y - 1); // UP - RIGHT - RIGHT - RIGHT
                break;
            }
            case 25: {
                spotted = checkCell(x + 1 + 1 + 1, y); // RIGHT - RIGHT - RIGHT
                break;
            }
            case 26: {
                spotted = checkCell(x + 1 + 1 + 1, y + 1); // DOWN - RIGHT - RIGHT - RIGHT
                break;
            }
            case 27: {
                spotted = checkCell(x + 1 + 1, y + 1 + 1); // DOWN - DOWN - RIGHT - RIGHT
                break;
            }
            case 28: {
                spotted = checkCell(x + 1, y + 1 + 1 + 1); // DOWN - DOWN - DOWN - RIGHT
                break;
            }
            case 29: {
                spotted = checkCell(x, y + 1 + 1 + 1); // DOWN - DOWN- DOWN
                break;
            }
            case 30: {
                spotted = checkCell(x - 1, y + 1 + 1 + 1); // DOWN - DOWN- DOWN - LEFT
                break;
            }
            case 31: {
                spotted = checkCell(x - 1 - 1, y + 1 + 1); // DOWN - DOWN - LEFT - LEFT
                break;
            }
            case 32: {
                spotted = checkCell(x - 1 - 1 - 1, y + 1); // DOWN - LEFT - LEFT - LEFT
                break;
            }
            case 33: {
                spotted = checkCell(x - 1 - 1 - 1, y); // LEFT - LEFT - LEFT
                break;
            }
            case 34: {
                spotted = checkCell(x - 1 - 1 - 1, y - 1); // UP - LEFT - LEFT - LEFT
                break;
            }
            case 35: {
                spotted = checkCell(x - 1 - 1, y - 1 - 1); // UP - UP - LEFT - LEFT
                break;
            }
            case 36: {
                spotted = checkCell(x - 1, y - 1 - 1 - 1); // UP - UP - UP - LEFT
                break;
            }
        }


        return spotted;
    }

    /**
     * Checks the cell at the parameter coordinates.
     * If the item is present inside the spottedSurroundings then it is ignored.
     * Else the coordinates are verified to be inside the map bounds
     *
     * *** ONLY INDEX 0 is Spotted ***
     *
     * If the coordinates are inside the map bounds and the item hasn't been checked the item is returned
     *  *** The item is not added to spottedSurroundings in this method ***
     *  certain coordinates will be stored class wide such as:
     *                  If there is no item (NULL) the coordinates will be saved x_Empty | y_Empty
     *                  IF item is a prey (Bunny) the coordinates will be saved x_Prey | y_Prey
     *
     *  Else coordinates are not inside map bounds a recursive call to this method will be sent with bounded coordinates
     *
     *  Variables changed:
     *      x_Empty
     *      y_Empty
     *      x_Prey
     *      y_Prey
     *      x_Mate
     *      y_Mate
     *
     * @param x x-Coordinate of the item that will be checked
     * @param y y-Coordinate of the item that will be checked
     * @return true if item has been checked else return spotted Object
     */
    private Object checkCell(int x, int y){

        // Check if this coordinate has been checked already
        for (Object entity: spottedObjects){
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
                        if (MAP[x][y].size() == 0){
                            x_Empty = x;
                            y_Empty = y;
                        }

                        // return a null if the list there is empty
                        if (MAP[x][y].isEmpty()){
                            return null;
                        } else {
                            return MAP[x][y].get(0);
                        }

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

    /**
     * Updates the map with the *** dead *** this is done so that dead are not counted during the iteration
     * and will be shown on the graph after the iteration as null
     *
     */
    private void updateMap(){

        // Updates all the dead plants
        for (Plant plant: deadPlants){

            // Goes through all the objects at that cell
            for (int index = 0; index < MAP[plant.getX()][plant.getY()].size(); index++){

                // If the animal is still on the map get rid of it
                if (MAP[plant.getX()][plant.getY()].get(index) == plant){
                    MAP[plant.getX()][plant.getY()].remove(plant);
                }
            }
        }

        // Updates all the dead animals
        for (Animal animal: deadAnimals){

            // Goes through all the objects at that cell
            for (int index = 0; index < MAP[animal.getX()][animal.getY()].size(); index++){

                // If the animal is still on the map get rid of it
                if (MAP[animal.getX()][animal.getY()].get(index) == animal){
                    MAP[animal.getX()][animal.getY()].remove(animal);
                }
            }
        }

    }

    /** *** TEMPORARY SOLUTION ***
     * TODO print this to the user instead of the console
     */
    public void printMAP(){

        int grassCount; // Here for future development only as only 1 grass can exists in the same cell
        int bunnyCount;
        int foxCount;

        // Printing the map
        System.out.println("-------------------------------------------------");
        for (int row = 0; row < MAP_HEIGHT; row++) {
            System.out.println();
            for (int column = 0; column < MAP_LENGTH; column++) {

                // New cell so new counts
                grassCount = 0;
                bunnyCount = 0;
                foxCount = 0;

                if (column == 0) {
                    System.out.print("| ");
                }



                for (Object object: MAP[column][row]){
                    if (object instanceof Plant){
                        grassCount++;
                    } else if (object instanceof Bunny){
                        bunnyCount++;
                    }
                    else if (object instanceof Fox){
                        foxCount++;
                    }
                }

                // *** DELETE THIS IF MAKING A GUI ***
                // Only shows the first entity in command line
                // If the ArrayList at that cell is empty print a NULL
                if (MAP[column][row].isEmpty()){
                    System.out.print("  ~  ");
                } else if (MAP[column][row].get(0) instanceof Plant){
                    System.out.print("Grass " + grassCount);
                } else if (MAP[column][row].get(0) instanceof Bunny){
                    System.out.print("Bunny " + bunnyCount);
                }
                else if (MAP[column][row].get(0) instanceof Fox){
                    System.out.print(" Fox " + foxCount);
                }

                System.out.print(" | ");
            }
        }
        System.out.println();
        System.out.println();
        System.out.println("-------------------------------------------------");
    }

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
        Bunny bunny = new Bunny(0, 0, 0, false);
        Fox fox = new Fox(0, 0, 0, false);

        // Initializes the grid's rows
        for (int x = 0; x < MAP_LENGTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                MAP[x][y] = new ArrayList<>();
            }
        }

        System.out.print("Generating Plants: ");
        for (int p = 0; p < grassPopulation; p++) {
            column = random.nextInt(MAP_LENGTH);
            row = random.nextInt(MAP_HEIGHT);

            if (MAP[column][row].size() != 0) {
                p--;
            } else {
                grass = new Grass(column, row, random.nextInt((int) grass.getMaxAge()));
                this.grass.add(grass);
                MAP[column][row].add(grass);
                System.out.print(".");
            }
        }
        System.out.println(" Done!");

        System.out.print("Generating Bunnies: ");
        for (int b = 0; b < bunnyPopulation; b++) {
            column = random.nextInt(MAP_LENGTH);
            row = random.nextInt(MAP_HEIGHT);

            if (MAP[column][row].size() != 0) {
                b--;
            } else {
                bunny = new Bunny(column, row, random.nextInt((int) bunny.getMaxAge()), random.nextBoolean());
                bunnies.add(bunny);
                MAP[column][row].add(bunny);
                System.out.print(".");
            }
        }
        System.out.println(" Done!");

        System.out.print("Generating Foxes: ");
        for (int f = 0; f < foxPopulation; f++) {
            column = random.nextInt(MAP_LENGTH);
            row = random.nextInt(MAP_HEIGHT);

            if (MAP[column][row].size() != 0) {
                f--;
            } else {
                fox = new Fox(column, row, random.nextInt((int) fox.getMaxAge()), random.nextBoolean());
                foxes.add(fox);
                MAP[column][row].add(fox);
                System.out.print(".");
            }
        }
        System.out.println(" Done!");

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
     *                  Life Stages ( Seedling | Sapling | Mature )
     *
     *          Bunny:
     *                  age
     *                  sight
     *                  gender
     *                  TODO movement
     *                  TODO reproduction
     *                  dying of old age
     *                  TODO food search
     *                  TODO mate search
     *                  TODO energy levels
     *                  TODO hunger levels
     *                  TODO evasion
     *                  IMPLEMENT Life Stages ( Baby | Young Adult | Adult )
     *
     *          Fox:
     *                  age
     *                  sight
     *                  gender
     *                  TODO movement
     *                  TODO reproduction
     *                  TODO food search
     *                  TODO mate search
     *                  dying of old age
     *                  TODO energy levels
     *                  TODO hunger levels
     *                  TODO evasion
     *                  IMPLEMENT Life Stages ( Baby | Young Adult | Adult )
     *
     *
     * @param input the simulationVariables before the iteration
     * @return updated simulationVariables after the iteration
     */
    @Override
    public SimVariables calculate(SimVariables input) {

        // used to track empty cells for movement and plant reproduction
        int emptyCount;

        // Iterates through grass turns
        for (Plant plant: grass){

            age(plant);

            // Plants reproduce if any cell they can see is NULL
            emptyCount = checkSurroundings(plant);
            if (emptyCount > 0){
                reproduce(plant);
            }
        }

        // Iterates through the bunnies turns
        for (Bunny bunny: bunnies){

            age(bunny);

            checkSurroundings(bunny);
            calculateHunger(bunny);
            calculateEnergy(bunny);
            calculateSexualNeed(bunny);

            //TODO calculate priorities based on spottedObjects then in order:
            // TODO calculateSexNeed();
            // TODO calculateEnergy();
            // TODO calculateHunger();
            // TODO movement();
            // TODO feed();
            // TODO reproduction();
        }

        // Iterates through the foxes turns
        for (Fox fox: foxes){
            age(fox);

            checkSurroundings(fox);
        }

        // *** Remove all dead things BEFORE updating the map ***

        // Removes deadPlants from their respective ArrayLists
        grass.removeAll(deadPlants);

        // Removes deadAnimals from their respective ArrayLists
        bunnies.removeAll(deadAnimals);
        foxes.removeAll(deadAnimals);

        updateMap();

        // *** ADD all the babies AFTER updating the map ***

        grass.addAll(babyGrass); // When babyGrass becomes adults add them to the grass list

        // Clear every List
        deadPlants.clear(); // Clear the List of deadPlants when all the plants have been removed from other Lists
        deadAnimals.clear(); // Clear the list of deadAnimals when all the animals have been removed from other Lists
        babyGrass.clear(); // Clear List of babyAnimals when all new entities have been added to other lists

        // Updates the SimVariables
        input.grass = grass.size();
        input.bunnies = bunnies.size();
        input.foxes = foxes.size();

        return input;
    }

    /**
     *  *** TEST purposes ONLY ***
     *  Acts as a template for how the DynamicAlgorithm class should work in other classes
     */
    public static void main(String[] args) {
        // Initialize the SimVariables
        SimVariables simVariables = new SimVariables();
        simVariables.grass = 1;
        simVariables.bunnies = 1;
        simVariables.foxes = 1;

        // Initialize the DynamicAlgorithm Simulation
        // TODO input MAP_LENGTH and MAP_HEIGHT from the user
        DynamicAlgorithm simulation = new DynamicAlgorithm(22, 38,
                                                            simVariables.grass,
                                                            simVariables.bunnies,
                                                            simVariables.foxes);

        int TOTAL_ITERATIONS = 100;

        simulation.printMAP();

        // Results after the iterations
        System.out.println("Grass Population: " + simVariables.grass);
        System.out.println("Bunny Population: " + simVariables.bunnies);
        System.out.println("Fox Population:   " + simVariables.foxes);

        for (int i = 0; i < TOTAL_ITERATIONS; i++){

            simVariables = simulation.calculate(simVariables);

            simulation.printMAP();

            // Results after the iterations
            System.out.println("Grass Population: " + simVariables.grass);
            System.out.println("Bunny Population: " + simVariables.bunnies);
            System.out.println("Fox Population:   " + simVariables.foxes);
        }
    }
}
