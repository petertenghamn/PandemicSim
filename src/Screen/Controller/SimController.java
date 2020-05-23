package Screen.Controller;

import Algorithms.DynamicAlgorithm;
import Algorithms.StaticAlgorithm;
import Data.SimVariables;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.io.IOException;

public class SimController implements Interface_SimController {

    @FXML private LineChart<?, ?> lineChartDynamicAlg;
    @FXML private LineChart<?, ?> lineChartStaticAlg;

    // lines for the two graphs
    private XYChart.Series foxesDynamic = new XYChart.Series();
    private XYChart.Series bunniesDynamic = new XYChart.Series();
    private XYChart.Series plantsDynamic = new XYChart.Series();

    private XYChart.Series foxesStatic = new XYChart.Series();
    private XYChart.Series bunniesStatic = new XYChart.Series();
    private XYChart.Series plantsStatic = new XYChart.Series();

    // counter used for sync the graphs
    private int counter = 0;

    private StaticAlgorithm staticAlg;
    private DynamicAlgorithm dynamicAlg;

    @Override
    public void runProgram(int grass, int bunnies, int foxes) {

        SimVariables dynVar = new SimVariables(grass,bunnies,foxes, true);
        SimVariables staticVar = new SimVariables(grass,bunnies,foxes, false);

        staticAlg = new StaticAlgorithm();
        dynamicAlg = new DynamicAlgorithm(20,20,grass,bunnies,foxes);

        // removes dots on the lines
        lineChartDynamicAlg.setCreateSymbols(false);
        lineChartStaticAlg.setCreateSymbols(false);

        //draw the initial values before iterating
        drawDynamicAlgorithm(dynVar);
        drawStaticAlgorithm(staticVar);
        for (int i = 0; i < 25; i++){
            lineChartDynamicAlg.getData().clear();
            lineChartStaticAlg.getData().clear();
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

        int TOTAL_ITERATIONS = 1 * 24 * 7 * 4; // 8760 = 1 year

        for (int years = 0; years <= TOTAL_ITERATIONS; years++){
            input = dynamicAlg.calculate(input);
        }

        // scale plant results
        input.grass = input.grass / 10;

        return input;
    }

    @Override
    public void drawDynamicAlgorithm(SimVariables input) {

        // name for the lines and unit count
        foxesDynamic.setName("Foxes: " + input.foxes);
        bunniesDynamic.setName("Bunnies: " + input.bunnies);
        plantsDynamic.setName("Plants: " + input.grass);

        String index = Integer.toString(counter);

        // create data points
        foxesDynamic.getData().add(new XYChart.Data<>(index,input.foxes));
        bunniesDynamic.getData().add(new XYChart.Data<>(index,input.bunnies));
        plantsDynamic.getData().add(new XYChart.Data<>(index,input.grass));
        counter++;

        // adds the lines to the line chart
        lineChartDynamicAlg.getData().addAll(foxesDynamic, bunniesDynamic, plantsDynamic);
    }

    @Override
    public SimVariables iterateStaticAlgorithm(SimVariables input) {

        return staticAlg.calculate(input);
    }

    @Override
    public void drawStaticAlgorithm(SimVariables input) {
        // name for the lines
        foxesStatic.setName("Foxes: " + input.foxes);
        bunniesStatic.setName("Bunnies: " + input.bunnies);
        plantsStatic.setName("Plants: " + input.grass);

        // adds data to each line
        String index = Integer.toString(counter-1);
        foxesStatic.getData().add(new XYChart.Data<>(index,input.foxes));
        bunniesStatic.getData().add(new XYChart.Data<>(index,input.bunnies));
        plantsStatic.getData().add(new XYChart.Data<>(index,input.grass));

        // adds the lines to the line chart
        lineChartStaticAlg.getData().addAll(foxesStatic, bunniesStatic, plantsStatic);
    }

    // method that switches back to main menu
    public void backBtnAction(ActionEvent event) throws IOException {
        File path = new File("src/Resources/MainMenuView.fxml");

        MainMenuController mainMenuController = new MainMenuController();
        mainMenuController.switchScene(event,path);
    }

}
