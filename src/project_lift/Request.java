package project_lift;

public class Request {
    int stage_to = 0;
    Pointers button_pressed;
    public Request(int stage, Pointers button_pressed){
        this.stage_to = stage;
        this.button_pressed = button_pressed;
    }
}
