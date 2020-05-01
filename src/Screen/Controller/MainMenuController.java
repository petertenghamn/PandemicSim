package Screen.Controller;

import Data.SimVariables;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainMenuController implements Interface_MainMenuController {
    @FXML private TextField foxPop;
    @FXML private TextField rabbitPop;
    @FXML private TextField grassPop;

    @Override
    public void runSim(javafx.event.ActionEvent event) throws IOException{
        //switch scene to start sim and passes through the input numbers (sample.fxml for testing)
        setFoxPop();
        setRabbitPop();
        setGrassPop();

        File path = new File("src/Resources/sample.fxml");
        URL url = path.toURL();

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        // Controller class for testing
        Controller controller = loader.getController();
        controller.setText(SimVariables.foxes,SimVariables.bunnies, SimVariables.grass);

        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void getResultList(javafx.event.ActionEvent event) throws IOException{
        //switch to resultList scene (sample.fxml for testing)
        File path = new File("src/Resources/sample.fxml");
        switchScene(event, path);
    }

    @Override
    public void viewPrevResult(javafx.event.ActionEvent event) throws IOException{
        //switch to prevResult scene (sample.fxml for testing)
        File path = new File("src/Resources/sample.fxml");
        switchScene(event, path);
    }

    @Override
    public void setFoxPop() {
        SimVariables.foxes = Integer.parseInt(foxPop.getText());
    }

    @Override
    public void setRabbitPop() {
        SimVariables.bunnies = Integer.parseInt(rabbitPop.getText());
    }

    @Override
    public void setGrassPop() {
        SimVariables.grass = Integer.parseInt(grassPop.getText());
    }

    // method for switching between scenes
    private void switchScene(javafx.event.ActionEvent event, File path) throws IOException{
        URL url = path.toURL();
        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }
}
