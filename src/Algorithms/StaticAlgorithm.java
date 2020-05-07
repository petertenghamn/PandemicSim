package Algorithms;

import Data.SimVariables;

import java.util.Random;

/**
 * Calculates the populations for grass, bunnies, and foxes depending on static probabilities
 * this class does NOT use a map nor tracks entity Objects as probabilities depend on population not individuals
 *
 *  For Grass Probabilities see first link below p 201 - 203
 *  Grass grows in Norther Ireland 50.3 kg DM/ha/day (Dry Mass / harvest / day)
 *      80 % of grass makes seeds
 *      90 % of seeds are germinated
 *      60 % of seeds make it into adult stage
 *
 * For Rabbit Probabilities see Second and Third link below
 *      liter size 3-4 average
 *      40 % make it to adulthood
 *      70 % of females get pregnant
 *      25 % of bunnies are expected to die in a year
 *      20 % population growth in a month
 *
 * For Fox Probabilities see Fifth link below
 *      Eat up to 3 bunnies a day but require only 1 if grass population is good
 *      liters can be up to 13 with avg 4-6 depending on bunny population and fox mortality rate
 *      20 % make it to adulthood
 *
 *
 * @see "https://bit.ly/3bcs5wu"
 * @see "https://www.wikiwand.com/en/European_hare#/Behaviour_and_life_history"
 * @see "https://mathinsight.org/controlling_rabbit_population"
 * @see "https://www.wikiwand.com/en/Red_fox#/Diet,_hunting_and_feeding_behaviour"
 */
public class StaticAlgorithm implements Interface_StaticAlgorithm {

    /**
     * *** This is the only method that communicates outside of the class ***
     *  Using the SimVariables goes through the calculation for the different populations.
     *  Then returns the updated SimVariables
     *
     *  Order of calculation is grass - fox - bunny
     *  since foxes hunt in the morning
     *
     * @param input SimVariables representing populations (Grass | Bunny | Fox)
     * @return updated SimVariables with new calculated populations
     */
    @Override
    public SimVariables calculate(SimVariables input) {
        population_grass = input.grass;
        population_bunny = input.bunnies;
        population_fox = input.foxes;

        // Order of calculation is grass - fox - bunny | since foxes hunt in the morning
        calculateGrassPopulation();
        calculateFoxPopulation();
        calculateBunnyPopulation();

        return input;
    }

    // Variables
    private int population_grass;
    private int population_bunny;
    private int population_fox;

    // Widgets
    private Random random = new Random(); // Random Generator used in random placement in initialization

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Grass Population  ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     *  Estimated new = 50  / iteration
     */
    private void calculateGrassPopulation(){

    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Bunny Population  ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     *  Estimated new = 3.81 * reproduction rate - death rate  / iteration
     */
    private void calculateBunnyPopulation(){

    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Fox Population  ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     *  Estimated new = (2.22 -> 13.61) * reproduction rate - death rate / iteration
     */
    private void calculateFoxPopulation(){

    }
}
