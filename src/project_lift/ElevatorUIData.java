package project_lift;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ElevatorUIData {
    public int stage_num;
    public int passenger_num;

    public ElevatorUIData (int stage_num, int passenger_num){
        this.passenger_num = passenger_num;
        this.stage_num = stage_num;
    }
}

