package Controller;

import Screen.Controller.MainMenuController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainMenuControllerTest {

    private int expectedResult;
    private int actualResult;

    @Test
    void setFoxPop() {
        System.out.println("setFoxPop");
        MainMenuController controller = new MainMenuController();

        expectedResult = 10;
        controller.setFoxPop(10);
        actualResult = controller.getSimVariables().foxes;
        Assertions.assertEquals(expectedResult,actualResult);
    }

    @Test
    void setRabbitPop() {
        System.out.println("setBunnyPop");
        MainMenuController controller = new MainMenuController();

        expectedResult = 10;
        controller.setBunnyPop(10);
        actualResult = controller.getSimVariables().bunnies;
        Assertions.assertEquals(expectedResult,actualResult);
    }

    @Test
    void setGrassPop() {
        System.out.println("setGrassPop");
        MainMenuController controller = new MainMenuController();

        expectedResult = 10;
        controller.setGrassPop(10);
        actualResult = controller.getSimVariables().grass;
        Assertions.assertEquals(expectedResult,actualResult);
    }
}