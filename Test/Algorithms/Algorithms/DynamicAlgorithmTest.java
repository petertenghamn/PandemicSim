package Algorithms;

import Data.SimVariables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicAlgorithmTest {

    // Setup Variables
    int TOTAL_ITERATIONS = 6;
    int MAP_LENGTH = 3;
    int MAP_HEIGHT = 5;
    int INPUT_GRASS = 1;
    int INPUT_BUNNY = 0;
    int INPUT_FOXES = 0;

    // Test Variables
    int expectedGrassPopulation;
    int resultedGrassPopulation;

    @Test
    void calculate() {

        // Grass is expected to take up the entire area when other populations are 0
        if (INPUT_BUNNY + INPUT_FOXES == 0){
            expectedGrassPopulation = (MAP_LENGTH * MAP_HEIGHT);
        }

        // Initialize the SimVariables
        SimVariables simVariables = new SimVariables();
        simVariables.grass = INPUT_GRASS;
        simVariables.bunnies = INPUT_BUNNY;
        simVariables.foxes = INPUT_FOXES;

        // This is for visual testing
        System.out.println("*** Initial Populations ***");
        System.out.println("Plant Population: " + simVariables.grass);
        System.out.println("Bunny Population: " + simVariables.bunnies);
        System.out.println("Fox Population:   " + simVariables.foxes);

        // Initialize the DynamicAlgorithm Simulation with hard coded inputs
        DynamicAlgorithm simulation = new DynamicAlgorithm(MAP_LENGTH, MAP_HEIGHT,
                simVariables.grass,
                simVariables.bunnies,
                simVariables.foxes);

        for (int i = 0; i < TOTAL_ITERATIONS; i++){
            simulation.printMAP();
            simulation.calculate(simVariables);
        }
        simulation.printMAP();

        // Results after the iterations
        System.out.println("Plant Population: " + simVariables.grass);
        System.out.println("Bunny Population: " + simVariables.bunnies);
        System.out.println("Fox Population:   " + simVariables.foxes);

        resultedGrassPopulation = simVariables.grass;
        assertEquals(expectedGrassPopulation, resultedGrassPopulation);
    }
}