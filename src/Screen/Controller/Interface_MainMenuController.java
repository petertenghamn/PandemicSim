package Screen.Controller;

import javafx.event.ActionEvent;

import java.io.IOException;

public interface Interface_MainMenuController {
    public void runSim(ActionEvent event) throws IOException;
    public void getResultList(ActionEvent event) throws IOException;
    public void viewPrevResult(ActionEvent event) throws IOException;
    public void setFoxPop();
    public void setBunnyPop();
    public void setGrassPop();
}
