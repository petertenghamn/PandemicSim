package Screen.Controller;

import Data.SimVariables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainMenuControllerTest {

    private int expectedResult;
    private int actualResult;


    @Test
    void setFoxPop() {
        System.out.println("setFoxPop");

        expectedResult = 0;
        actualResult = SimVariables.foxes;
        Assertions.assertEquals(expectedResult,actualResult);
    }

    @Test
    void setRabbitPop() {
        System.out.println("setRabbitPop");

        expectedResult = 0;
        actualResult = SimVariables.bunnies;
        Assertions.assertEquals(expectedResult,actualResult);
    }

    @Test
    void setGrassPop() {
        System.out.println("setGrassPop");

        expectedResult = 0;
        actualResult = SimVariables.grass;
        Assertions.assertEquals(expectedResult,actualResult);
    }
}