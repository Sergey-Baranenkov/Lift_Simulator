package project_lift;
import java.util.ArrayList;
import java.util.Iterator;

public class Lift implements Runnable {
    private int stage_num; // этаж, на котором лифт находится в данный момент
    private final int capacity; // вместимость лифта
    private final int branch_id; //id пролета, на котором установлен лифт
    private final int speed_ms;
    private Pointers state; // состояние, куда едет лифт (↑, ↓, либо 0 если стоит)
    private final ArrayList<Request> passengers = new ArrayList<>();


    Building assigned_building;


    public Lift(int branch_id, int initial_stage, int capacity, Building assigned_building, int speed_ms){
        this.speed_ms = speed_ms;
        this.assigned_building = assigned_building;
        this.branch_id = branch_id;
        this.stage_num = initial_stage;
        this.state = Pointers.STOP;
        this.capacity = capacity;
    }


    public ElevatorUIData get_references_for_UI(){
        return new ElevatorUIData(
                this.stage_num,
                this.passengers.size()
        );
    }

    // если лифт пуст, то на каждом этаже, независимо куда он вызван, будет производится пересчет маршрута к ближайшему этажу.
    private int get_closest_stage_num_with_requests(){
        {
            Stage s1;
            Stage s2;
            int shift;
            while (true) {
                //проверим есть ли люди на текущем этаже
                s1 = assigned_building.get_stage(this.stage_num); // 0 ... n-1
                if (s1 != null && s1.waiters_num() != 0) {
                    return s1.stage_num;
                }
                //иначе смотрим по бокам все дальше и дальше, пока не пройдем все этажи -> возврат к верхнему циклу
                shift = 1;
                do{
                    s1 = assigned_building.get_stage(this.stage_num + shift);
                    s2 = assigned_building.get_stage(this.stage_num - shift);
                    if (s1!=null && s1.waiters_num()!=0){
                        return s1.stage_num;
                    }else if (s2!=null && s2.waiters_num() != 0){
                        return s2.stage_num;
                    }
                    shift++;
                }while (s1!=null || s2!=null);
            }
        }
    }

    private void move(){
        try {
            Thread.sleep(this.speed_ms);
            if (this.state == Pointers.UP){
                this.stage_num++;
            }else if (this.state == Pointers.DOWN){
                this.stage_num--;
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(){
        System.out.printf("Лифт №%s запустился\n", this.branch_id);
        while (true){
            if (this.state == Pointers.STOP){ // Если лифт стоит, то

                System.out.printf("Лифт №%d на этаже: %d число пассажиров: %d state: %s \n",
                        this.branch_id, this.stage_num, this.passengers.size(), this.state.name());

                int stage_to_go = get_closest_stage_num_with_requests();
                //едем к ближайшему этажу (каждый этаж пересчитываем)
                while (stage_to_go != this.stage_num){
                    if (stage_to_go > this.stage_num){
                        this.state = Pointers.UP;
                    }else{
                        this.state = Pointers.DOWN;
                    }
                    move();

                    stage_to_go = get_closest_stage_num_with_requests();
                }
                this.state = Pointers.STOP;

                boolean get_up_passengers_first = Math.random() < 0.5;
                if (get_up_passengers_first){
                    passengers.addAll(assigned_building.get_stage(this.stage_num).get_n_people(Pointers.UP, this.capacity));
                }else {
                    passengers.addAll(assigned_building.get_stage(this.stage_num).get_n_people(Pointers.DOWN, this.capacity));
                }

                if (this.passengers.size() != 0){
                    this.state = this.passengers.get(0).button_pressed;
                }

            }else { //иначе лифт куда-то едет
                while (this.passengers.size() != 0){
                    System.out.printf("Лифт №%d на этаже: %d число пассажиров: %d state: %s \n",
                            this.branch_id, this.stage_num, this.passengers.size(), this.state.name());

                    Iterator<Request> it = passengers.iterator();
                    Request r;
                    while (it.hasNext()){
                        r = it.next();
                        if (r.stage_to == this.stage_num){
                            it.remove();
                        }
                    }
                    //если после выгруза пассажиров, остались те, кто еще едет в первоначальном направлении - открываем двери на этаже и двигаемся на этаж выше/ниже,
                    // иначе переходим в состояние Stop
                    if (this.passengers.size() != 0) {
                        this.passengers.addAll(assigned_building.get_stage(this.stage_num).get_n_people(this.state, this.capacity - this.passengers.size()));
                        move();
                    }

                }
                this.state = Pointers.STOP;
            }
        }

    }
}