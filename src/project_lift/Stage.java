package project_lift;

import java.util.ArrayList;
import java.util.Iterator;

public class Stage{
    private final ArrayList<Request> requests;
    int stage_num;
    public Stage(int num){
        this.stage_num = num;
        this.requests = new ArrayList<>();
    }

    public synchronized ArrayList<Request> get_n_people(Pointers direction, int max_n){
        Iterator<Request> it = requests.iterator();
        ArrayList<Request> people_to_return = new ArrayList<>();

        while (it.hasNext()){
            Request r = it.next();
            if (people_to_return.size() >= max_n) break;
            if (r.button_pressed == direction){
                people_to_return.add(r);
                it.remove();
            }
        }

        return people_to_return;

    }

    public int waiters_num(){
        return this.requests.size();
    }

    public synchronized void add_request(Request r){
        requests.add(r);
    }
}
