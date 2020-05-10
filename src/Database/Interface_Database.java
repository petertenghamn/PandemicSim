package Database;

import Data.GraphedData;
import java.util.ArrayList;

public interface Interface_Database {
    public ArrayList<Integer> downloadResultIDs();
    public GraphedData downloadResult();
    public void uploadToDB(GraphedData data);
}
