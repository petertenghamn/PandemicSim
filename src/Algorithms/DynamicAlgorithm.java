package Algorithms;

import Algorithms.Animals.Animal;
import Algorithms.Animals.Bunny;
import Algorithms.Animals.Fox;
import Algorithms.Plants.Grass;
import Algorithms.Plants.Plant;
import Data.SimVariables;

import java.text.DecimalFormat;
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

    // Energy gained/lost and costs
    private static final int ENERGY_GAINED_PER_TURN = 10; // Energy gained from resting
    private static final int ENERGY_LOST_PER_TURN = 1; // Energy lost from !resting
    private static final int MOVEMENT_ENERGY_COST = 5; // Energy cost of moving
    private static final int REPRODUCTION_ENERGY_COST = 15; // Energy cost of having sex
    private static final int BIRTH_ENERGY_COST = REPRODUCTION_ENERGY_COST * 5; // Energy cost of giving birth

    private static final int HUNGER_GAIN_PER_TURN = 1; // Hunger gained per turn

    // HUNGER GAINED USED IN attemptToFeed()
    private static final int SEEDLING_HUNGER_GAIN = 100; // Hunger gained from eating a SEEDLING Plant
    private static final int SAPLING_HUNGER_GAIN = 100; // Hunger gained from eating a SAPLING Plant
    private static final int MATURE_HUNGER_GAIN = 100; // Hunger gained from eating a MATURE Plant
    private static final int BABY_HUNGER_GAIN = 100; // Hunger gained from eating a BABY Animal
    private static final int YA_HUNGER_GAIN = 100; // Hunger gained from eating a YOUNG ADULT Animal
    private static final int ADULT_HUNGER_GAIN = 100; // Hunger gained from eating an ADULT Animal
    private static final int MOTHER_HUNGER_GAIN = 15; // Hunger gained from drinking mother's milk


    // Variables
    private int x_Empty; // X Coordinate of spotted empty cell
    private int y_Empty; // Y Coordinate of spotted empty cell

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

        double MAX_SEEDLING_AGE = plant.getSeedlingMAX(); // MAX age percentage of the "Seedling" lifeStage
        double MAX_SAPLING_AGE = plant.getSaplingMAX(); // MAX age percentage of the "Sapling" lifeStage

        // Age one iteration
        plant.setAge(plant.getAge() + 1);

        // Sets the lifeStage of the plant depending on the percentage of life lived: age / MAX age
        if (plant.getAge() < MAX_SEEDLING_AGE){
            plant.setLifeStage("Seedling");
            plant.setEdible(false);
        }
        else if (plant.getAge() >= MAX_SEEDLING_AGE){
            plant.setLifeStage("Sapling");
            plant.setEdible(true);
        }
        else if (plant.getAge() >= MAX_SAPLING_AGE){
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

            // Removes the baby from the baby list of the parents
            if (animal.getParents() != null) {
                for (Animal parent : animal.getParents()) {
                    parent.getBabies().remove(animal);

                    // Some animals will be monogamous until their young are grown
                    if (parent.getMate() != null) {
                        switch (animal.getSpecies()) {
                            case "Fox": {
                                parent.getMate().setMate(null);
                                parent.setMate(null);
                                break;
                            }
                        }
                    }
                }
            }
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

            // True value is ignored because it means it found itself
            if (!(spottedObject instanceof Boolean)) {
                spottedObjects.add(spottedObject);
            }
        }
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
        if (hunger >= 100){

            // The different cases for the chances that the animal dies the closer it gets to it's MAX age
            if (1 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(98, 100); // 1/2 chance it dies
            }
            else if (0.9 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(97, 100); // 1/3 chance it dies
            }
            else if (0.8 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(96, 100); // 1/4 chance it dies
            }
            else if (0.7 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(95, 100); // 1/5 chance it dies
            }
            else if (0.6 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(92, 100); // 1/8 chance it dies
            }
            else if (0.5 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(90, 100); // 1/10 chance it dies
            }
            else if (0.4 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(85, 100); // 1/15 chance it dies
            }
            else if (0.3 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(90, 100); // 1/10 chance it dies
            }
            else if (0.2 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(95, 100); // 1/5 chance it dies
            }
            else if (0.1 <= deathChance){
                deathChance = ThreadLocalRandom.current().nextInt(97, 100); // 1/3 chance it dies
            }
            else {
                deathChance = ThreadLocalRandom.current().nextInt(98, 100); // 1/2 chance it dies
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
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN));
        }
        else if (80 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 2));
        }
        else if (70 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 3));
        }
        else if (60 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 4));
        }
        else if (50 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 5));
        }
        else if (40 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 6));
        }
        else if (30 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 7));
        }
        else if (20 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 8));
        }
        else if (10 <= energy){
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 9));
        }
        else{
            animal.setHunger(hunger + (HUNGER_GAIN_PER_TURN * 10));
        }

        // Hunger can not go above 100
        if (animal.getHunger() >= 100){
            animal.setHunger(100);
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

        int energy = animal.getEnergy();
        int hunger = animal.getHunger();

        // Energy is updated every turn if the animal is resting it gaines energy else it loses energy
         if (animal.isResting()){

             int wakingChance = 0;

            // Energy++
            animal.setEnergy(energy + ENERGY_GAINED_PER_TURN);

             // Depending on the animal's energy and hunger levels there is an increasing chance it will wake up
             if (hunger >= 70 || energy >= 100) {
                 wakingChance = 0; // Animal can not rests if it has too much energy or is too hungry
             } else if (hunger > energy) {
                 wakingChance = 0; // Animal won't rests if it's hungrier than tired
             } else if (90 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(98, 100); // 1 / 2 chance it rests
             } else if (80 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(97, 100); // 1 / 3 chance it rests
             } else if (70 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(96, 100); // 1 / 4 chance it rests
             } else if (60 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(95, 100); // 1 / 5 chance it rests
             } else if (50 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(90, 100); // 1 / 10 chance it rests
             } else if (40 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(85, 100); // 1 / 15 chance it rests
             } else if (30 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(80, 100); // 1 / 20 chance it rests
             } else if (20 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(75, 100); // 1 / 25 chance it rests
             } else if (10 <= energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(70, 100); // 1 / 30 chance it rests
             } else if (0 < energy) {
                 wakingChance = ThreadLocalRandom.current().nextInt(50, 100); // 1 / 50 chance it rests
             }

            // The animal wakes up
            if (wakingChance >= 100){
                animal.setResting(false);
            }

            // Energy can not be greater than 100
             if (animal.getEnergy() > 100){
                 animal.setEnergy(100);
                 animal.setResting(false);
             }
         }
         else {

             int restingChance = 0;

             // Energy --
             animal.setEnergy(energy - ENERGY_LOST_PER_TURN);

             // Depending on the animal's energy and hunger levels there is an increasing chance it will rest
             if (hunger >= 70 || energy >= 100) {
                 restingChance = 0; // Animal can not rests if it has too much energy or is too hungry
             } else if (hunger > energy) {
                 restingChance = 0; // Animal won't rests if it's hungrier than tired
             } else if (50 <= energy) {
                 restingChance = ThreadLocalRandom.current().nextInt(85, 100); // 1 / 15 chance it rests
             } else if (40 <= energy) {
                 restingChance = ThreadLocalRandom.current().nextInt(90, 100); // 1 / 10 chance it rests
             } else if (30 <= energy) {
                 restingChance = ThreadLocalRandom.current().nextInt(93, 100); // 1 / 7 chance it rests
             } else if (20 <= energy) {
                 restingChance = ThreadLocalRandom.current().nextInt(95, 100); // 1 / 5 chance it rests
             } else if (10 <= energy) {
                 restingChance = ThreadLocalRandom.current().nextInt(97, 100); // 1 / 3 chance it rests
             } else if (0 < energy) {
                 restingChance = ThreadLocalRandom.current().nextInt(99, 100); // 1 / 2 chance it rests
             }

             // Makes it so energy can never be less than 0
             if (animal.getEnergy() < 0 ){
                 restingChance = 100; // IF Energy <= 0 animal has to rest
                 animal.setEnergy(0);
             }

             // The animal starts to rest
             if (restingChance >= 100){
                 animal.setResting(true);
             }
         }
    }

    /**
     * Calculates the animal's sex need by increasing it every iteration depending on species rules
     *
     * @param animal animal which sex need will be calculated for
     */
    private void calculateSexualNeed(Animal animal) {

        boolean increaseSexNeed = false;
        int sexNeed = animal.getSexNeed();

        // Bunny section of the method
        if (animal instanceof Bunny){
            if (animal.isAlpha()){
                sexNeed++; // SexNeed increases every iteration for bunnies who are alphas
            } else {
                // Bunnies who are not alphas are monogamous and stay with their partner until no longer pregnant
                // Bunnies who are pregnant don't have a need for sex
                if (!animal.isPregnant()){
                    increaseSexNeed = true;
                }
            }
        }

        // Fox section of the method
        else if (animal instanceof Fox){
            // TODO implement seasons
            // Foxes have sex during Dec - Mar
            // Foxes are monogamous who wait to have sex again until their babies are grown
            if (!animal.isPregnant()){
                if (animal.getBabies() != null) {
                    if (animal.getBabies().size() == 0) {
                        increaseSexNeed = true;
                    }
                }
            }
        }

        // Depending on the conditions above sexNeed will or won't be changed
        if (increaseSexNeed){
            int energy = animal.getEnergy();
            // SexNeed increases based on energy levels
            if (90 <= energy){
                sexNeed = sexNeed + 10;
            } else if (80 <= energy){
                sexNeed = sexNeed + 9;
            } else if (70 <= energy){
                sexNeed = sexNeed + 8;
            } else if (60 <= energy){
                sexNeed = sexNeed + 7;
            } else if (50 <= energy){
                sexNeed = sexNeed + 6;
            } else if (40 <= energy){
                sexNeed = sexNeed + 5;
            } else if (30 <= energy){
                sexNeed = sexNeed + 4;
            } else if (20 <= energy){
                sexNeed = sexNeed + 3;
            } else if (10 <= energy){
                sexNeed = sexNeed + 2;
            } else {
                sexNeed = sexNeed + 1;
            }


            // sexNeed can not be higher than 100
            if (sexNeed > 100){
                sexNeed = 100;
            }
        }

        animal.setSexNeed(sexNeed);
    }

    /**
     * Checks to see if the animal and the object are compatible for mating
     *
     * @param object object which is going to be a candidate for mating
     * @param animal animal which is searching for a mate
     * @return True = object compatible for mating | False = object not compatible for mating
     */
    private boolean isMate(Object object, Animal animal){

        // Checks that its not it self
        if (object != animal) {
            // Checks if the object is an animal
            if (object instanceof Animal) {
                Animal mate = (Animal) object;

                // Checks to see if the animals are the same species
                if (animal.getSpecies().equals(mate.getSpecies())) {

                    // Checks if the animals are different sexes
                    if (animal.isFemale() != mate.isFemale()) {

                        // Checks if the animal nor the mate are pregnant
                        if (!animal.isPregnant() && !mate.isPregnant()) {

                            // Both animals have to be adults
                            if (animal.getLifeStage().equals("Adult") && mate.getLifeStage().equals("Adult")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks certain criteria depending on species to update the animals pregnant status
     * in some cases only the female will have an updated status
     *
     * @param male male animal used for reproduction
     * @param female female animal used for reproduction
     */
    private void reproduce(Animal male, Animal female) {

        // Requires they have the energy to pay the reproduction cost
        if (male.getEnergy() >= REPRODUCTION_ENERGY_COST && female.getEnergy() >= REPRODUCTION_ENERGY_COST){

            // Different Species reproduce differently
            switch (male.getSpecies()){
                case "Bunny":{

                    // Male Alpha bunnies don't stay with their partner
                   if (male.isAlpha()){
                       male.setEnergy(male.getEnergy() - REPRODUCTION_ENERGY_COST);
                       female.setEnergy(female.getEnergy() - REPRODUCTION_ENERGY_COST);

                       // Energy can not be below 0
                       if (male.getEnergy() < 0){
                           male.setEnergy(0);
                       } else if (female.getEnergy() < 0){
                           female.setEnergy(0);
                       }

                       // Alpha bunnies are not monogamous
                       female.setPregnant(true);
                       female.setMate(male);

                       // Animals had sex
                       female.setSexNeed(0);
                       male.setSexNeed(0);

                   } else{
                       // Bunnies don't wait for their young to grow before getting pregnant again

                       male.setEnergy(male.getEnergy() - REPRODUCTION_ENERGY_COST);
                       female.setEnergy(female.getEnergy() - REPRODUCTION_ENERGY_COST);

                       // Energy can not be below 0
                       if (male.getEnergy() < 0){
                           male.setEnergy(0);
                       } else if (female.getEnergy() < 0){
                           female.setEnergy(0);
                       }

                       male.setPregnant(true);
                       female.setPregnant(true);

                       // Non Alpha Male bunnies will also bond with their mate
                       male.setMate(female);
                       female.setMate(male);

                       // Animals had sex
                       female.setSexNeed(0);
                       male.setSexNeed(0);
                   }

                    break;
                }

                case "Fox":{

                    // Foxes wait until their babies are grown to seek new partners
                    if (male.getBabies() == null && female.getBabies() == null) {

                        // Reproduction uses 10 energy
                        male.setEnergy(male.getEnergy() - REPRODUCTION_ENERGY_COST);
                        female.setEnergy(female.getEnergy() - REPRODUCTION_ENERGY_COST);

                        // Energy can not be below 0
                        if (male.getEnergy() < 0){
                            male.setEnergy(0);
                        } else if (female.getEnergy() < 0){
                            female.setEnergy(0);
                        }

                        // Foxes are monogamous
                        male.setPregnant(true);
                        female.setPregnant(true);
                        male.setMate(female);
                        female.setMate(male);

                        // Animals had sex
                        female.setSexNeed(0);
                        male.setSexNeed(0);
                    }

                    break;
                }
            }
        }
    }

    /**
     * Pregnancy takes certain amount of iteration depending on certain factors
     * gestation period, species, energy level and so forth
     * Males are for the most part ignored for pregnancy
     *
     * @param animal animal which is going through a pregnancy (MALE | FEMALE)
     */
    private void calculatePregnancy(Animal animal) {

        // Position of the birth
        int x = animal.getX();
        int y = animal.getY();

        ArrayList<Animal> babies;
        ArrayList<Animal> parents = new ArrayList<>();
        Animal baby;

        // Initialization of the babies list if the animal already has babies
        if (animal.getBabies() == null){
            babies = new ArrayList<>();
        } else {
            babies = animal.getBabies();
        }

        // Adds one iteration to the pregnancy
        animal.setGestation(animal.getGestation() + 1);

        // If the animal has reached their gestation period
        if (animal.getGestation() >= animal.getGestationMax()) {
            // Only females give birth
            if (animal.isFemale()) {
                // Giving birth costs energy
                if (animal.getEnergy() >= BIRTH_ENERGY_COST) {
                    // literSize is dependant on hunger and different animals have different liter sizes
                    int literSize = generateLiterSize(animal);

                    // Iterates through the amount of babies the animal will have
                    while (literSize > 0) {

                        // Used for Object creation of the right type
                        switch (animal.getSpecies()) {
                            case "Bunny": {
                                literSize--; // Index counter

                                // Gender is random
                                baby = new Bunny(x, y, 0, random.nextBoolean());

                                // Adds it's parents
                                parents.add(animal);
                                parents.add(animal.getMate());
                                baby.setParents(parents);
                                parents.clear();

                                baby.setLifeStage("Baby");

                                // Adds the baby to the list of new babies
                                babies.add(baby);
                                babyBunnies.add((Bunny) baby);

                                // Add the baby to the map in the same spot as the mother
                                MAP[animal.getX()][animal.getY()].add(baby);

                                break;
                            }

                            case "Fox": {
                                literSize--;

                                // Foxes wait until their babies are gone before hailing more babies
                                if (animal.getBabies() == null || animal.getBabies().isEmpty()) {
                                    baby = new Fox(x, y, 0, random.nextBoolean());

                                    parents.add(animal);
                                    parents.add(animal.getMate());
                                    baby.setParents(parents);

                                    baby.setLifeStage("Baby");

                                    babies.add(baby);
                                    babyFoxes.add((Fox) baby);

                                    MAP[animal.getX()][animal.getY()].add(baby);
                                }
                                break;
                            }
                        }
                    }

                    // The cost of giving birth for the mother
                    animal.setEnergy(animal.getEnergy() - BIRTH_ENERGY_COST);

                    // Energy can not be below 0
                    if (animal.getEnergy() < 0){
                        animal.setEnergy(0);
                    }


                    // If the mother has a mate
                    if (animal == animal.getMate().getMate()) {
                        // Adds the new babies to the parents lists
                        animal.setBabies(babies);
                        animal.getMate().setBabies(babies);

                        // The parents are no longer pregnant
                        animal.setPregnant(false);
                        animal.getMate().setPregnant(false);

                        // Reset gestation counter
                        animal.setGestation(0);
                        animal.getMate().setGestation(0);
                    } else {
                        animal.setBabies(babies);
                        animal.setPregnant(false);
                        animal.setGestation(0);
                    }

                    // What happens to the mates after birth depends on species
                    switch (animal.getSpecies()){
                        case "Bunny":{
                            animal.getMate().setMate(null);
                            animal.setMate(null);
                            break;
                        }
                        case "Fox":{

                            // If the pregnancy wasn't successful the fox will no longer be mated
                            if (babies.isEmpty()){
                                animal.getMate().setMate(null);
                                animal.setMate(null);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates a literSize based on mother's hunger the closer it gets to 100 hunger the less kids the mother will have
     *
     * @param mother animal giving birth
     * @return the literSize
     */
    private int generateLiterSize(Animal mother){

        int maxSize = 0;

        // Depends on current hunger level
        if (90 <= mother.getHunger()){
            maxSize = random.nextInt(2);

        } else if (50 <= mother.getHunger() && mother.getHunger() < 90){
            maxSize = random.nextInt(Math.abs(mother.getLiterSize() - 3));

        } else if (25 <= mother.getHunger() && mother.getHunger() < 50){
            maxSize = random.nextInt(Math.abs(mother.getLiterSize() - 2));

        }  else {
            maxSize = mother.getLiterSize();
        }

        if (maxSize < 0){
            maxSize = 0;
        }

        return maxSize;
    }

    /**
     * Moves the animal based on certain priorities:
     *      Mating
     *          if the animal is not pregnant & hunger < sexNeed & there is mate candidate nearby & not resting
     *      Hunting
     *          if the animal hunger > sexNeed & there is food nearby & not resting
     *      Exploring
     *          if it has free energy & not resting & spotted and empty spot
     *
     * The animal will move towards the priority if the conditions were met otherwise it stays where it was
     *
     * Methods used:
     *      isFood()
     *
     * Variables used:
     *      MAP[][]
     *      spottedSurroundings
     *      MOVEMENT_ENERGY_COST
     *      X & Y EMPTY
     *
     * @param animal the animal which will move
     */
    private void movement(Animal animal) {

        boolean moving = false;
        Object spottedObject;

        // The x-y coordinates of the object which is being prioritized
        int x_Origin = animal.getX();
        int y_Origin = animal.getY();
        int x_Priority = x_Origin;
        int y_Priority = y_Origin;
        int x_New = x_Origin;
        int y_New = y_Origin;

        // Index 0 is skipped because every animal spots itself first
        for (int index = 1; index < spottedObjects.size(); index++) {
            spottedObject = spottedObjects.get(index);
            // Priority MATING
            // If the animal is NOT resting & hunger < sexNeed & same animal species
            if (!animal.isResting() && animal.getHunger() >= 25 && isMate(spottedObject, animal)) {

                Animal mate = (Animal) spottedObject;

                // Set Coordinates of mate
                x_Priority = mate.getX();
                y_Priority = mate.getY();

                moving = true;

                break;
            }

            // Priority HUNTING
            // if animal is NOT resting & hunger > sexNeed & spottedObject is food
            else if (!animal.isResting() && animal.getHunger() >= 75 && isFood(spottedObject, animal)){

                if (spottedObject instanceof  Plant){
                    Plant food = (Plant) spottedObject;

                    // Set Coordinates of food
                    x_Priority = food.getX();
                    y_Priority = food.getY();

                    moving = true;
                }
                else if (spottedObject instanceof Animal){
                    Animal food = (Animal) spottedObject;

                    // Set Coordinates of food
                    x_Priority = food.getX();
                    y_Priority = food.getY();

                    moving = true;
                }

                break;
            }

            // Priority EXPLORING
            else if (!animal.isResting() && spottedObject == null && animal.getEnergy() > 0){

                x_Priority = x_Empty;
                y_Priority = y_Empty;

                moving = true;
            }
        }

        // *** BEGIN OF ACTUAL MOVEMENT ***
        if (moving){
            /*
                if (|X_spotted - X_Origin| >= 1)
                    if (X_spotted - X_Origin is positive)
                         X_New = X_Origin + 1;
                    else if (X_Spotted - X_Origin is negative)
                         X_New = X_Origin - 1;
            */
            if (Math.abs(x_Priority - x_Origin) >= 1){
                if (x_Priority - x_Origin >= 0){
                    x_New = x_Origin + 1;
                }
                else if (x_Priority - x_Origin < 0){
                    x_New = x_Origin - 1;
                }
            }

            /*
                if (|Y_spotted - Y_Origin| >= 1)
                    if (Y_spotted - Y_Origin is positive)
                         Y_New = Y_Origin + 1;
                    else if (Y_Spotted - Y_Origin is negative)
                         Y_New = Y_Origin - 1;
            */
            if (Math.abs(y_Priority - y_Origin) >= 1){
                if (y_Priority - y_Origin >= 0){
                    y_New = y_Origin + 1;
                }
                else if (y_Priority - y_Origin < 0){
                    y_New = y_Origin - 1;
                }
            }

            animal.setEnergy(animal.getEnergy() - MOVEMENT_ENERGY_COST);

            if (animal.getEnergy() < 0) {
                animal.setEnergy(0);
            }

            MAP[x_Origin][y_Origin].remove(animal);

            animal.setX(x_New);
            animal.setY(y_New);

            MAP[x_New][y_New].add(animal);
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

        // Checks to see if the that the object is not empty
        if (object != null){
            // Boolean is it self
            if (!(object instanceof Boolean)) {
                // Precaution if it did find it self
                if (object != animal) {

                    // Babies use their parents as their food source
                    if (animal.getLifeStage().equals("Baby")) {
                        if (animal.getParents() != null) {
                            for (Animal parent : animal.getParents()) {

                                if (object == parent) {
                                    return true;
                                }
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
                                        if (animal.getHunger() >= 100 && random.nextBoolean()) {
                                            return true;
                                        } else return false;

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
                                        } else return false;

                                    } else {
                                        return true;
                                    }
                                }

                                break;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * The animal attempts to eat the food and lose hunger from it
     * The loss of hunger depends on whether the food is a Plant or Animal
     * and in what lifeStage the food is in.
     * Animals also give a boost in hunger loss due to how close they are to Animal.ageMax
     * Baby Animals can also feed off their parents
     *
     * @param food Object that is being eaten (Plant | Animal | Parent)
     * @param animal the animal which is eating
     */
    private void attemptToFeed(Object food, Animal animal){

        int hunger = animal.getHunger();
        double hungerGain = 0;

        // Food is a Plant
        if (food instanceof Plant){
            Plant plant = (Plant) food;

            // Plant gives hunger recovery depending on it's life stage
            switch (plant.getLifeStage()){
                case "Seedling":{
                    hungerGain = SEEDLING_HUNGER_GAIN;
                    break;
                }
                case "Sapling":{
                    hungerGain = SAPLING_HUNGER_GAIN;
                    break;
                }
                case "Mature":{
                    hungerGain = MATURE_HUNGER_GAIN;
                    break;
                }
            }

            deadPlants.add(plant);

            hunger = (int) (hunger - hungerGain);

            if (hunger < 0){
                hunger = 0;
            }

            animal.setHunger(hunger);
        }

        // Food is an Animal
        else if (food instanceof Animal){
            Animal prey = (Animal) food;

            // Baby animals get food from their parents
            if (animal.getLifeStage().equals("Baby")){
                for (Animal parent: animal.getParents()){
                    if (parent == prey){
                        animal.setHunger(animal.getHunger()-MOTHER_HUNGER_GAIN);

                        if (animal.getHunger() < 0){
                            animal.setHunger(0);
                        }
                        return;
                    }
                }
            }

            // Animal gives hunger recovery depending on it's life stage * percentage of life lived
            switch (prey.getLifeStage()){
                case "Baby":{
                    hungerGain = BABY_HUNGER_GAIN * (prey.getAge() / prey.getMaxAge());
                    break;
                }
                case "Young Adult":{
                    hungerGain = YA_HUNGER_GAIN * (prey.getAge() / prey.getMaxAge());
                    break;
                }
                case "Adult":{
                    hungerGain = ADULT_HUNGER_GAIN * (prey.getAge() / prey.getMaxAge());
                    break;
                }
            }

            if (!evadeSuccessfully(prey, animal)){
                hunger = (int) (hunger - hungerGain);

                if (hunger < 0){
                    hunger = 0;
                }

                animal.setHunger(hunger);

                deadAnimals.add(prey);
            }
        }
    }

    /**
     * The predator will attempt to feed on the prey but the prey has a chance to have an extra move this iteration
     * The chance of the extra move depends on the age difference between prey and predator
     * The chance is also worsen if the prey has less energy than the predator
     *
     * @param prey the animal which is being hunted
     * @param predator the animal which is hunting
     * @return whether or not the prey got away True | False
     */
    private boolean evadeSuccessfully(Animal prey, Animal predator) {

        int fleeingChance = 0;
        double ageDifference = (prey.getAge() / prey.getMaxAge()) - (predator.getAge() / predator.getMaxAge());

        // The prey has a higher chance of fleeing if it is older than its predator
        // ageDifference range: -inf -> inf where a negative number means that the predator is older
        // Any ageDifference higher than 1 or lower than -1 means that the animal is older than the expected max
        if (1 <= ageDifference){
            fleeingChance = ThreadLocalRandom.current().nextInt(90, 100); // 1 / 10 chance
        } else if (0.5 <= ageDifference){
            fleeingChance = ThreadLocalRandom.current().nextInt(75,100); // 1 / 25 chance
        } else if (0 <= ageDifference){
            fleeingChance = ThreadLocalRandom.current().nextInt(50,100); // 1 / 50 chance
        } else if (-1 > ageDifference){
            fleeingChance = ThreadLocalRandom.current().nextInt(40, 100); // 1 / 60 chance
        } else if (-0.5 > ageDifference){
            fleeingChance = ThreadLocalRandom.current().nextInt(25, 100); // 1 / 85 chance
        } else if (0 > ageDifference){
            fleeingChance = ThreadLocalRandom.current().nextInt(30,100 ); // 1 / 70 chance
        }

        // The prey has a lower chance of fleeing if it has less energy than its predator
        if (prey.getEnergy() < predator.getEnergy()){
            fleeingChance = fleeingChance * random.nextInt(100);
        }

        if (fleeingChance >= 100){
            return false;
        } else {
            predator.setEnergy(predator.getEnergy() - 1);
            prey.setResting(false); // change resting on the prey if evade was successful
            movement(prey); // The prey moves away
            return true;
        }
    }

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
                if (MAP[column][row].isEmpty()) {
                    System.out.print("                ");
                } else {
                    if (grassCount > 0 && bunnyCount > 0 && foxCount > 0){
                        System.out.print(" ~ " + grassCount + " o " + bunnyCount + " x " + foxCount + " ");
                    }
                    else if (grassCount > 0 && bunnyCount > 0){
                        System.out.print("  ~   " + grassCount + "   o  " + bunnyCount + "  ");
                    }
                    else if (grassCount > 0 && foxCount > 0){
                        System.out.print("  ~   " + grassCount + "   x  " + foxCount + "  ");
                    }
                    else if (grassCount > 1){
                        System.out.print("    ~    " + grassCount + "     ");
                    }
                    else if (grassCount == 1){
                        System.out.print("        ~       ");
                    }
                    else if (bunnyCount > 0 && foxCount > 0){
                        System.out.print("  o  " + bunnyCount + "  x  " + foxCount + "  ");
                    }
                    else if (bunnyCount > 1){
                        System.out.print("     o    " + bunnyCount + "      ");
                    }
                    else if (bunnyCount == 1){
                        System.out.print("        o       ");
                    }
                    else if (foxCount > 1){
                        System.out.print("    x    " + foxCount + "     ");
                    }
                    else if (foxCount == 1){
                        System.out.print("        x       ");
                    }
                }

                System.out.print(" | ");
            }
        }
        System.out.println();
        System.out.println();
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
                grass = new Grass(column, row, random.nextInt(25));
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
     *                  movement
     *                  reproduction
     *                  dying of old age
     *                  food search
     *                  mate search
     *                  energy levels
     *                  hunger levels
     *                  evasion
     *                  Life Stages ( Baby | Young Adult | Adult )
     *
     *          Fox:
     *                  age
     *                  sight
     *                  gender
     *                  movement
     *                  reproduction
     *                  food search
     *                  mate search
     *                  dying of old age
     *                  energy levels
     *                  hunger levels
     *                  evasion
     *                  Life Stages ( Baby | Young Adult | Adult )
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

            /* *** USED FOR TESTING ***
            DecimalFormat decimalFormat = new DecimalFormat("##");
            System.out.println("---------");
            System.out.println("Age: " + decimalFormat.format(bunny.getAge() / (24 * 365)));
            System.out.println("Energy:  " + bunny.getEnergy());
            System.out.println("Hunger:  " + bunny.getHunger());
            System.out.println("SexNeed: " + bunny.getSexNeed());
             */

            age(bunny);

            checkSurroundings(bunny);

            calculateHunger(bunny);
            calculateEnergy(bunny);
            calculateSexualNeed(bunny);

            if (bunny.isPregnant()){
                calculatePregnancy(bunny);
            }

            movement(bunny);

            for (Object object: MAP[bunny.getX()][bunny.getY()]){

                // If it moved to a cell with food then it eats
                if (isFood(object, bunny)){
                    attemptToFeed(object, bunny);
                }

                // If it moved to a cell with a mate then it reproduces
                else if (isMate(object, bunny)){
                    if (bunny.isFemale()){
                        reproduce((Animal) object, bunny);
                    }
                    else {
                        reproduce(bunny, (Animal) object);
                    }
                }
            }
        }

        // Iterates through the foxes turns
        for (Fox fox: foxes){
            age(fox);

            checkSurroundings(fox);

            calculateHunger(fox);
            calculateEnergy(fox);
            calculateSexualNeed(fox);

            if (fox.isPregnant()){
                calculatePregnancy(fox);
            }

            movement(fox);

            for (Object object: MAP[fox.getX()][fox.getY()]){

                // If it moved to a cell with food then it eats
                if (isFood(object, fox)){
                    attemptToFeed(object, fox);
                }
                // If it moved to a cell with a mate then it reproduces
                else if (isMate(object, fox)){
                    if (fox.isFemale()){
                        reproduce((Animal) object, fox);
                    }
                    else {
                        reproduce(fox, (Animal) object);
                    }
                }
            }
        }

        // *** Remove all dead things BEFORE updating the map ***

        // Removes deadPlants from their respective ArrayLists
        grass.removeAll(deadPlants);

        // Removes deadAnimals from their respective ArrayLists
        bunnies.removeAll(deadAnimals);
        foxes.removeAll(deadAnimals);

        updateMap();

        // *** ADD all the babies AFTER updating the map ***
        // When babies are born add them to the list
        grass.addAll(babyGrass);
        bunnies.addAll(babyBunnies);
        foxes.addAll(babyFoxes);

        // Clear every List
        deadPlants.clear(); // Clear the List of deadPlants when all the plants have been removed from other Lists
        deadAnimals.clear(); // Clear the list of deadAnimals when all the animals have been removed from other Lists

        babyGrass.clear(); // Clear List of babyPlants when all new entities have been added to other lists
        babyBunnies.clear(); // Clear List of babyAnimals when all new entities have been added to other lists
        babyFoxes.clear(); // Clear List of babyAnimals when all new entities have been added to other lists

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
        simVariables.grass = 30;
        simVariables.bunnies = 5;
        simVariables.foxes = 5;

        // Initialize the DynamicAlgorithm Simulation 22 38
        // TODO input MAP_LENGTH and MAP_HEIGHT from the user
        DynamicAlgorithm simulation = new DynamicAlgorithm(12, 50,
                                                            simVariables.grass,
                                                            simVariables.bunnies,
                                                            simVariables.foxes);

        int TOTAL_ITERATIONS = 8760; // 8760 = 1 year

        simulation.printMAP();

        // Results after the iterations
        System.out.println("Grass Population: " + simVariables.grass);
        System.out.println("Bunny Population: " + simVariables.bunnies);
        System.out.println("Fox Population:   " + simVariables.foxes);

        for (int i = 0; i < TOTAL_ITERATIONS; i++){

            simVariables = simulation.calculate(simVariables);

            //simulation.printMAP();

            // Results after the iterations
            System.out.println("-------------------------------------------------");
            System.out.println("------------------ Hour: " + (i + 1) + " -------------------");
            System.out.println("Grass Population: " + simVariables.grass);
            System.out.println("Bunny Population: " + simVariables.bunnies);
            System.out.println("Fox Population:   " + simVariables.foxes);
            System.out.println("-------------------------------------------------");


        }
    }
}
