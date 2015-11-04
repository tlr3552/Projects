package views;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * An object which represents the simulation table.
 * 
 * @author Drew Heintz
 *
 */
public class SimTableData {
    private StringProperty equity;
    private DoubleProperty current;
    private DoubleProperty simulated;
    
    public SimTableData(String equity, double current, double simulated) {
        setEquity(equity);
        setCurrent(current);
        setSimulated(simulated);
    }

    public String getEquity() {
        return equityProperty().get();
    }

    public void setEquity(String equity) {
        equityProperty().set(equity);
    }
    
    public StringProperty equityProperty() {
        if(equity == null) {
            equity = new SimpleStringProperty(this, "equity");
        }
        return equity;
    }
    
    public double getCurrent() {
        return currentProperty().get();
    }
    
    public void setCurrent(double current) {
        currentProperty().set(current);
    }
    
    public DoubleProperty currentProperty() {
        if(current == null) {
            current = new SimpleDoubleProperty(this, "current");
        }
        return current;
    }
    
    public double getSimulated() {
        return simulatedProperty().get();
    }
    
    public void setSimulated(double simulated) {
        simulatedProperty().set(simulated);
    }
    
    public DoubleProperty simulatedProperty() {
        if(simulated == null) {
            simulated = new SimpleDoubleProperty(this, "simulated");
        }
        return simulated;
    }
}
