package Data;

import java.util.ArrayList;

public class GraphedData {
    //list for stored values on each iteration (can be used to graph the result). An average case version of this will be stored in the DB as well.
    private ArrayList<SimVariables> staticVariables;
    private ArrayList<SimVariables> dynamicVariables;

    public ArrayList<SimVariables> getStaticVariables() {
        return staticVariables;
    }
    public ArrayList<SimVariables> getDynamicVariables() {
        return dynamicVariables;
    }
    public ArrayList<SimVariables> getVariables(){
        ArrayList<SimVariables> output = staticVariables;
        output.addAll(dynamicVariables);
        return output;
    }

    public void setStaticVariables(ArrayList<SimVariables> variables) {
        this.staticVariables = variables;
    }
    public void setDynamicVariables(ArrayList<SimVariables> variables) {
        this.dynamicVariables = variables;
    }
    public void setVariables(ArrayList<SimVariables> variables){
        dynamicVariables = new ArrayList<>();
        staticVariables = new ArrayList<>();
        for (int i = 0; i < variables.size(); i++){
            if (variables.get(i).dynamicAlgorithm){
                dynamicVariables.add(variables.get(i));
            }
            else {
                staticVariables.add(variables.get(i));
            }
        }
    }
}
