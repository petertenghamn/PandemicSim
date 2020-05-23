package Screen.Controller;

import Data.GraphedData;
import Data.SimVariables;
import Database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StatsController implements Interface_StatsController, Initializable {


    @FXML private LineChart<?, ?> lineChart;
    @FXML private LineChart<?, ?> lineChart2;

    @FXML private PieChart pieChart;
    @FXML private PieChart pieChart2;

    private Database database;

    private ArrayList<SimVariables> simVariablesStatic;
    private ArrayList<SimVariables> simVariablesDynamic;



    // calls these two methods on start
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // downloads the result and puts it into array lists
        database = new Database();
        GraphedData data = database.downloadResult();

        simVariablesStatic = data.getStaticVariables();
        simVariablesDynamic = data.getDynamicVariables();

        drawGraphStatic();
        drawPieChartStatic();

        drawGraphDynamic();
        drawPieChartDynamic();
    }

    @Override
    public void downloadResult(int resultID) {

    }

    @Override
    public void drawGraphStatic() {
        // draws a chart based on the inputs from the DB (Only test values for now)

        // Creating the lines
        XYChart.Series foxes = new XYChart.Series();
        XYChart.Series bunnies = new XYChart.Series();
        XYChart.Series plants = new XYChart.Series();

        // name for the lines
        foxes.setName("Foxes: " + simVariablesStatic.get(simVariablesStatic.size()-1).foxes);
        bunnies.setName("Bunnies: " + simVariablesStatic.get(simVariablesStatic.size()-1).bunnies);
        plants.setName("Plants: " + simVariablesStatic.get(simVariablesStatic.size()-1).grass);

        // adds data to each line from the simVariablesStatic
        for (int i = 0; i < simVariablesStatic.size(); i++){

            String index = Integer.toString(i);

            foxes.getData().add(new XYChart.Data<>(index,simVariablesStatic.get(i).foxes));
            bunnies.getData().add(new XYChart.Data<>(index,simVariablesStatic.get(i).bunnies));
            plants.getData().add(new XYChart.Data<>(index,simVariablesStatic.get(i).grass));
        }

        // adds the lines to the line chart
        lineChart.setCreateSymbols(false);
        lineChart.getData().addAll(foxes, bunnies, plants);

    }

    private void drawPieChartStatic() {
        //method that draws the pie chart

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Foxes", simVariablesStatic.get(simVariablesStatic.size()-1).foxes),
                new PieChart.Data("Bunnies", simVariablesStatic.get(simVariablesStatic.size()-1).bunnies),
                new PieChart.Data("Plants", simVariablesStatic.get(simVariablesStatic.size()-1).grass));

        pieChart.setData(pieChartData);
    }

    @Override
    public void drawGraphDynamic() {
        // draws a chart based on the inputs from the DB (Only test values for now)

        // Creating the lines
        XYChart.Series foxes = new XYChart.Series();
        XYChart.Series bunnies = new XYChart.Series();
        XYChart.Series plants = new XYChart.Series();

        // name for the lines
        foxes.setName("Foxes: " + simVariablesDynamic.get(simVariablesDynamic.size()-1).foxes);
        bunnies.setName("Bunnies: " + simVariablesDynamic.get(simVariablesDynamic.size()-1).bunnies);
        plants.setName("Plants: " + simVariablesDynamic.get(simVariablesDynamic.size()-1).grass);

        // adds data to each line from the simVariablesStatic
        for (int i = 0; i < simVariablesDynamic.size(); i++){

            String index = Integer.toString(i);

            foxes.getData().add(new XYChart.Data<>(index,simVariablesDynamic.get(i).foxes));
            bunnies.getData().add(new XYChart.Data<>(index,simVariablesDynamic.get(i).bunnies));
            plants.getData().add(new XYChart.Data<>(index,simVariablesDynamic.get(i).grass));
        }

        // adds the lines to the line chart
        lineChart2.setCreateSymbols(false);
        lineChart2.getData().addAll(foxes, bunnies, plants);
    }

    private void drawPieChartDynamic() {

        //method that draws the pie chart

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Foxes", simVariablesDynamic.get(simVariablesDynamic.size()-1).foxes),
                new PieChart.Data("Bunnies", simVariablesDynamic.get(simVariablesDynamic.size()-1).bunnies),
                new PieChart.Data("Plants", simVariablesDynamic.get(simVariablesDynamic.size()-1).grass));

        pieChart2.setData(pieChartData);

    }

    // method that switches back to main menu
    public void backBtnAction(ActionEvent event) throws IOException {
        File path = new File("src/Resources/MainMenuView.fxml");

        MainMenuController mainMenuController = new MainMenuController();
        mainMenuController.switchScene(event,path);
    }

}
