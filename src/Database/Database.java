package Database;

import Data.GraphedData;
import Data.SimVariables;

import java.sql.*;
import java.util.ArrayList;

public class Database implements Interface_Database {

    private Connection con;

    private void connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // here ecosimdb is database name, root is username and password
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ecosimdb?autoReconnect=true&useSSL=false", "root", "root");
        }
        catch (Exception e){
            System.out.println("Error:" + e);
        }
    }

    @Override
    public ArrayList<Integer> downloadResultIDs() {
        ArrayList<Integer> result = new ArrayList<>();
        try {
            // connect to the database
            connect();
            System.out.println("--- Execute Download Result ID Query ---");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from records");
            while(rs.next()) {
                Integer rID = rs.getInt(1);
                result.add(rID);
            }
            System.out.println("");
            return result;
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    @Override
    public GraphedData downloadResult() {
        GraphedData result = new GraphedData();
        ArrayList<SimVariables> variables = new ArrayList<>();
        try {
            // connect to the database
            connect();
            System.out.println("--- Execute Download Result Query ---");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from iteration where records_id = 1");
            while (rs.next()) {
                variables.add(new SimVariables(rs.getInt(4), rs.getInt(3), rs.getInt(2), rs.getBoolean(6)));
            }
            result.setVariables(variables);
            return result;
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    @Override
    public void uploadToDB(GraphedData data) {
        try {
            // connect to the database
            connect();
            // check if records with id = 1 exists, if it does, remove existing before placing new
            ArrayList<Integer> currentIDs = downloadResultIDs();
            if (currentIDs.contains(1)){
                System.out.println("--- Delete Existing record at 1 in order to replace with new data ---");
                Statement stmt = con.createStatement();
                stmt.execute("delete from records where id = 1");
            }

            System.out.println("--- Insert record with id of 1 ---");
            Statement stmt = con.createStatement();
            stmt.execute("insert into records values (1, null)");

            System.out.println("--- Insert iteration data assigned to the inserted record ---");
            for (SimVariables variable : data.getVariables()){
                stmt = con.createStatement();
                stmt.execute("insert into iteration (fox_pop, bunny_pop, grass_pop, records_id, isDynamic) " +
                        "values (" + variable.foxes + ", " + variable.bunnies + ", " + variable.grass + ", (select id from records where id = 1), " + (variable.dynamicAlgorithm ? "1" : "0") + ")");
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
