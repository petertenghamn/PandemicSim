package Database;

import Data.GraphedData;
import Data.SimVariables;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class DatabaseTest {

    @Test
    void uploadToDB() {
        Database db = new Database();

        // generate random data to use
        GraphedData data = new GraphedData();
        ArrayList<SimVariables> variables = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            variables.add(new SimVariables(i, (i * 2), (i / 5), true));
            variables.add(new SimVariables(i, (i * 2), (i / 5), false));
        }
        data.setVariables(variables);

        // call DB methods
        System.out.println("Uploading to the database");
        db.uploadToDB(data);
    }

    @Test
    void downloadResultIDs() {
        Database db = new Database();

        System.out.println("downloading IDs from the database");
        ArrayList<Integer> ids = db.downloadResultIDs();

        System.out.println("Found IDs");
        System.out.print(ids.toString());
    }

    @Test
    void downloadResult() {
        Database db = new Database();

        System.out.println("downloading data from the database");
        GraphedData data = db.downloadResult();
        ArrayList<SimVariables> variables = data.getVariables();

        boolean first = true;
        for (int i = 0; i < variables.size(); i++) {
            if (first) {
                System.out.println("Iteration data");
                System.out.print("[" + i + "] Grass: " + variables.get(i).grass + ", Bunnies: " + variables.get(i).bunnies + ", Foxes: " + variables.get(i).foxes);
                first = false;
            } else {
                System.out.print(" | [" + i + "] Grass: " + variables.get(i).grass + ", Bunnies: " + variables.get(i).bunnies + ", Foxes: " + variables.get(i).foxes);
            }
        }
    }


}