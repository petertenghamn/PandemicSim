package Algorithms;

import Data.SimVariables;

public class StaticAlgorithm implements Interface_StaticAlgorithm {

    @Override
    public SimVariables calculate(SimVariables input) {
        int predator = input.foxes;
        int prey = input.bunnies;
        int food = input.plants;

        // using the variables
        // let predators kill a fixed % estimate
        // let predators grow a fixed % estimate
        // let predators die naturally by a fixed % estimate

        // using the variables do the same for prey

        // grass will regrow by a % as well

        return new SimVariables(predator, prey, food);
    }
}
