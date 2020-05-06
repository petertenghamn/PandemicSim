package Screen.Controller;

import Algorithms.StaticAlgorithm;
import Data.SimVariables;

public class SimController implements Interface_SimController{

    private StaticAlgorithm staticAlg = new StaticAlgorithm();

    @Override
    public void runProgram() {
        SimVariables dynVar = new SimVariables(100, 100, 100);
        SimVariables staticVar = new SimVariables(100, 100, 100);

        //draw the initial values before iterating
        drawDynamicAlgorithm(dynVar);
        drawStaticAlgorithm(staticVar);
        for (int i = 0; i < 100; i++){
            //calculate and draw dynamic algorithm
            dynVar = iterateDynamicAlgorithm(dynVar);
            drawDynamicAlgorithm(dynVar);
            //calculate and draw static algorithm
            staticVar = iterateStaticAlgorithm(staticVar);
            drawStaticAlgorithm(staticVar);
        }
    }

    @Override
    public SimVariables iterateDynamicAlgorithm(SimVariables input) {
        return null;
    }

    @Override
    public void drawDynamicAlgorithm(SimVariables input) {

    }

    @Override
    public SimVariables iterateStaticAlgorithm(SimVariables input) {
        return staticAlg.calculate(input);
    }

    @Override
    public void drawStaticAlgorithm(SimVariables input) {
        //method to draw the graph for each iteration
    }
}
