package Screen.Controller;

import Algorithms.DynamicAlgorithm;
import Algorithms.StaticAlgorithm;
import Data.SimVariables;

public interface Interface_SimController {
    public void runProgram();
    public SimVariables iterateDynamicAlgorithm(SimVariables input);
    public void drawDynamicAlgorithm();
    public SimVariables iterateStaticAlgorithm(SimVariables input);
    public void drawStaticAlgorithm();
}
