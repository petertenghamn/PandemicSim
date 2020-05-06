package Screen.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class StatsController implements Interface_StatsController, Initializable {


    @FXML private LineChart<?, ?> lineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private PieChart pieChart;

    // Test values for the line chart and pie chart
    private int[] testValues1 = new int[20];
    private int[] testValues2 = new int[20];
    private int[] testValues3 = new int[20];

    //TODO maybe adding a counter for number som simulations done (all time)

    // calls these two methods on start
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawGraph();
        drawPieChart();
    }


    @Override
    public void downloadResult(int resultID) {

    }

    @Override
    public void drawGraph() {
        // draws a chart based on the inputs from the DB (Only test values for now)

        // fills testValues arrays with random numbers
        for (int i = 0; i < 20; i++){
            testValues1[i] = ThreadLocalRandom.current().nextInt(0,80);
            testValues2[i] = ThreadLocalRandom.current().nextInt(20,100);
            testValues3[i] = ThreadLocalRandom.current().nextInt(15,70);
        }

        // Creating the lines
        XYChart.Series foxes = new XYChart.Series();
        XYChart.Series bunnies = new XYChart.Series();
        XYChart.Series plants = new XYChart.Series();

        // name for the lines
        foxes.setName("Foxes: " + testValues1[19]); // 19 simulates the last number. need to be changed later
        bunnies.setName("Bunnies: " + testValues2[19]);
        plants.setName("Plants: " + testValues3[19]);

        // adds data to each line (20 times) from the test values
        for (int i = 0; i < 20; i++){

            String index = Integer.toString(i);

            foxes.getData().add(new XYChart.Data<>(index,testValues1[i]));
            bunnies.getData().add(new XYChart.Data<>(index,testValues2[i]));
            plants.getData().add(new XYChart.Data<>(index,testValues3[i]));
        }

        // adds the lines to the line chart
        lineChart.getData().addAll(foxes, bunnies, plants);

    }

    private void drawPieChart() {
        //method that draws the pie chart

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Foxes", testValues1[19]), // 19 = the last value in each array
                new PieChart.Data("Bunnies", testValues2[19]),
                new PieChart.Data("Plants", testValues3[19]));


        pieChart.setData(pieChartData);
    }

    // method that switches back to main menu
    public void backBtnAction(ActionEvent event) throws IOException {
        File path = new File("src/Resources/MainMenuView.fxml");

        MainMenuController mainMenuController = new MainMenuController();
        mainMenuController.switchScene(event,path);
    }

}
