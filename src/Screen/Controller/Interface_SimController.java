package Screen.Controller;

import Algorithms.DynamicAlgorithm;
import Algorithms.StaticAlgorithm;
import Data.SimVariables;

public interface Interface_SimController {
    public void runProgram(int grass, int bunnies, int foxes);
    public SimVariables iterateDynamicAlgorithm(SimVariables input);
    public void drawDynamicAlgorithm(SimVariables input);
    public SimVariables iterateStaticAlgorithm(SimVariables input);
    public void drawStaticAlgorithm(SimVariables input);
}
