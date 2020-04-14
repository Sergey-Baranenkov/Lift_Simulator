package project_lift;

import javafx.beans.property.SimpleIntegerProperty;
import java.util.ArrayList;
import java.util.Iterator;

public class Building {
    public final ArrayList<Lift> lifts_array = new ArrayList<>();
    private final Stage[] stages;
    private final int num_stages;
    public Building(int num_stages, int num_lifts, int lift_capacity){
        //добавляем лифты
        for (int i = 0; i < num_lifts; i++) {
            int initial_stage = (int)(Math.random() * num_stages);
            lifts_array.add(new Lift(i, initial_stage, lift_capacity, this, 1000));
        }

        this.num_stages = num_stages;
        this.stages = new Stage[num_stages];
        for (int i = 0; i < num_stages; i++){
            this.stages[i] = new Stage(i);
        }
    }

    public void launch_lifts(){
        for (Runnable lift : lifts_array){
            new Thread(lift).start();
        }
    }

    public int get_num_stages(){
        return this.num_stages;
    }

    public Stage get_stage(int stage_num) {
        if (stage_num >= this.stages.length || stage_num < 0){
            return null;
        }

        return this.stages[stage_num];
    }
}



