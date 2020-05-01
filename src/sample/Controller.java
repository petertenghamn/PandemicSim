package sample;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class Controller {
    // just a class for testing
    @FXML private Text foxPop;
    @FXML private Text rabbitPop;
    @FXML private Text grassPop;

    // method for testing passing of data from MainMenuController
    public void setText(int fox, int rabbit, int grass){
        foxPop.setText(Integer.toString(fox));
        rabbitPop.setText(Integer.toString(rabbit));
        grassPop.setText(Integer.toString(grass));
    }

}
