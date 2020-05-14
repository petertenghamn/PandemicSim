package Algorithms;

import Data.SimVariables;

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

    //testing calculations
    public static void main(String[] args) {
        // Initialize the SimVariables
        SimVariables simVariables = new SimVariables();
        simVariables.grass = 1000;
        simVariables.bunnies = 100;
        simVariables.foxes = 10;

        // Initialize the StaticAlgorithm
        StaticAlgorithm algorithm = new StaticAlgorithm();

        int TOTAL_ITERATIONS = 10;
        System.out.println("Grass Population: " + simVariables.grass + " | " +
                "Bunny Population: " + simVariables.bunnies + " | " +
                "Fox Population: " + simVariables.foxes);

        for (int i = 1; i < TOTAL_ITERATIONS; i++){
            simVariables = algorithm.calculate(simVariables);

            // Results after the iterations
            System.out.println("Grass Population: " + simVariables.grass + " | " +
                    "Bunny Population: " + simVariables.bunnies + " | " +
                    "Fox Population: " + simVariables.foxes);
        }
    }

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
        SimVariables output = new SimVariables(input.grass, input.bunnies, input.foxes);

        // calculate pop changes due to one pop eating the other
        double grassEaten = calculateGrassConsumed(input);
        double bunniesHunted = calculateBunnyHunted(input);

        // calculate the new populations based on the input variables and the hunting outcomes
        output.grass = calculateGrassPopulation(input, grassEaten);
        output.bunnies = calculateBunnyPopulation(input, grassEaten, bunniesHunted);
        output.foxes = calculateFoxPopulation(input, bunniesHunted);

        return output;
    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Grass Population being consumed  ***
       ---------------------------------------------------------------------------------------------------
     */

    private double calculateGrassConsumed(SimVariables input){
        // the bunnies currently eat more if grass is abundant
        return (0.75 * (input.bunnies * input.grass));
    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Bunny Population being hunted  ***
       ---------------------------------------------------------------------------------------------------
     */

    private double calculateBunnyHunted(SimVariables input){
        // the foxes currently hunt more if there is an abundance of bunnies
        return (0.3 * (input.bunnies * input.foxes));
    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Grass Population  ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     *  Estimated new = 50  / iteration
     */
    private int calculateGrassPopulation(SimVariables input, double grassEaten){
        double popChange = (((input.grass * 0.8) * 0.9) * 0.6); // Calculated growth of grass
        popChange = (popChange * (input.grass / grassEaten)); // TEMP (might be wrong) this effects the pop negatively if the bunny pop is larger then the grass
        return (int)(input.grass + popChange);
    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Bunny Population  ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     *  Estimated new = 3.81 * reproduction rate - death rate  / iteration
     */
    private int calculateBunnyPopulation(SimVariables input, double grassEaten, double bunniesEaten){
        double popChange = ((input.bunnies * (0.2 / 30)) - (input.bunnies * (0.25 / 365))); // Bunny pop growth subtracted from their death rate (Daily)
        // TEMP (might be wrong) Grass available effects growth as well as foxes eating the bunnies
        popChange = (popChange * (input.bunnies/grassEaten)) - (popChange * (input.bunnies/bunniesEaten));
        return (int)(input.bunnies + popChange);
    }

    /* ---------------------------------------------------------------------------------------------------
        *** The methods in this section are related to the Fox Population  ***
       ---------------------------------------------------------------------------------------------------
     */

    /**
     *  Estimated new = (2.22 -> 13.61) * reproduction rate - death rate / iteration
     */
    private int calculateFoxPopulation(SimVariables input, double bunniesEaten){
        double popChange = ((input.foxes * (0.4 / 365)) - (input.foxes * (0.25 / 365))); // Fox pop growth subtracted from their death rate (Daily) (TEMP, values not based on statistics)
        popChange = popChange * (input.foxes / bunniesEaten); // TEMP this effects the pop negatively if not enough bunnies are available to eat for the fox pop
        return (int)(input.foxes + popChange);
    }
}
