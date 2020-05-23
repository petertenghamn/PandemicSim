package Screen.Controller;

import Data.SimVariables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainMenuController implements Interface_MainMenuController {
    @FXML private TextField foxPop;
    @FXML private TextField rabbitPop;
    @FXML private TextField grassPop;

    @FXML private Text fieldTesterFox;
    @FXML private Text fieldTesterRabbit;
    @FXML private Text fieldTesterGrass;

    // Class Variables
    private SimVariables simVariables;

    //getter and setter for testing
    public SimVariables getSimVariables() {
        return simVariables;
    }
    public void setSimVariables(SimVariables simVariables) {
        this.simVariables = simVariables;
    }

    public MainMenuController(){
        simVariables = new SimVariables(0,0,0, true);
    }

    @Override
    public void runSim(javafx.event.ActionEvent event) throws IOException{
        //switch scene to start sim and passes through the input numbers (sample.fxml for testing)
        // switches only if input fields are numbers and not empty
        fieldTesterFox.setText("");
        fieldTesterRabbit.setText("");
        fieldTesterGrass.setText("");

        if (isOnlyNumbers() && !isEmpty()) {

            setFoxPop();
            setBunnyPop();
            setGrassPop();

            File path = new File("src/Resources/SimView.fxml");
            URL url = path.toURL();

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            SimController simController = loader.getController();
            simController.runProgram(simVariables.grass,simVariables.bunnies,simVariables.foxes);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void getResultList(javafx.event.ActionEvent event) throws IOException{
        //switch to resultList scene (sample.fxml for testing)
        File path = new File("src/Resources/StatsView.fxml");
        switchScene(event, path);
    }

    @Override
    public void viewPrevResult(javafx.event.ActionEvent event) throws IOException{
        //switch to prevResult scene (sample.fxml for testing)
        File path = new File("src/Resources/sample.fxml");
        switchScene(event, path);
    }

    @Override
    public void setGrassPop() {
        simVariables.grass = Integer.parseInt(grassPop.getText());
    }
    public void setGrassPop(int pop){
        simVariables.grass = pop;
    }

    @Override
    public void setBunnyPop() {
        simVariables.bunnies = Integer.parseInt(rabbitPop.getText());
    }
    public void setBunnyPop(int pop){
        simVariables.bunnies = pop;
    }

    @Override
    public void setFoxPop() {
        simVariables.foxes = Integer.parseInt(foxPop.getText());
    }
    public void setFoxPop(int pop){
        simVariables.foxes = pop;
    }

    // method for switching between scenes
    public void switchScene(javafx.event.ActionEvent event, File path) throws IOException{
        URL url = path.toURL();
        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

    // checks if input fields are empty
    // displays red starts if not correct
    private boolean isEmpty() {

        if (foxPop.getText().isEmpty() || rabbitPop.getText().isEmpty() || grassPop.getText().isEmpty()){

            if (foxPop.getText().isEmpty()){
                fieldTesterFox.setText("***");
            }

            if (rabbitPop.getText().isEmpty()){
                fieldTesterRabbit.setText("***");
            }
            if (grassPop.getText().isEmpty()){
                fieldTesterGrass.setText("***");
            }
            return true;
        } else {
            return false;
        }
    }

    // checks if input fields only numbers
    // displays red starts if not correct
    private boolean isOnlyNumbers() {
        if (foxPop.getText().matches("[0-9]+") && rabbitPop.getText().matches("[0-9]+") &&
                grassPop.getText().matches("[0-9]+")){
            return true;
        }

        if (!foxPop.getText().matches("[0-9]+")){
            fieldTesterFox.setText("***");
        }
        if (!rabbitPop.getText().matches("[0-9]+")){
            fieldTesterRabbit.setText("***");
        }
        if (!grassPop.getText().matches("[0-9]+")){
            fieldTesterGrass.setText("***");
        }

        return false;
    }
}
