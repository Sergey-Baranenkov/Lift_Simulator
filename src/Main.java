import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import project_lift.Building;
import project_lift.ElevatorUIData;
import project_lift.RequestsGenerator;
import java.util.ArrayList;
import java.util.Map;

public class Main extends Application {
    int num_stages;
    int num_lifts;
    int request_generator_speed;
    int num_requests_per_time;
    int lift_capacity;
    ArrayList<Slider> sliders = new ArrayList<>();
    ArrayList<Label> lift_labels = new ArrayList<>();
    ArrayList<Label> stages_labels = new ArrayList<>();
    Building building;
    @Override
    public void init() throws Exception{
        super.init();
        try {
            Map <String,String> parameters = getParameters().getNamed();
            num_stages = Integer.parseInt(parameters.get("ns"));
            num_lifts = Integer.parseInt(parameters.get("nl"));
            request_generator_speed = Integer.parseInt(parameters.get("rgs"));
            num_requests_per_time = Integer.parseInt(parameters.get("nrpt"));
            lift_capacity = Integer.parseInt(parameters.get("lc"));
            if (num_stages <= 0 || num_lifts <= 0 || request_generator_speed <= 0 || num_requests_per_time <= 0 || lift_capacity <= 0){
                System.out.println("Все параметры должны быть строго положительными!");
                System.exit(-1);
            }

            this.building = new Building(num_stages, num_lifts, lift_capacity);

        }catch (Exception e){
            System.out.print("Не удалось запустить программу тк не переданы аргументы\n" +
                    "ns-число этажей\n" +
                    "nl-число лифтов\n" +
                    "lc - вместимость 1 лифта\n" +
                    "rgs - скорость генератора запросов в мс\n" +
                    "nrpt - число сразу сгенерированных заявок\n");

            System.exit(-1);
        }

    }


    private void updateUi(){
        for (int i = 0; i < num_stages; i++){
            String s = "Количество заявок на этаже №" + i + ":  " + this.building.get_stage(i).waiters_num();
            this.stages_labels.get(num_stages - i - 1).textProperty().setValue(s);
        }
        for (int i = 0; i < num_lifts; i++){
           ElevatorUIData data = this.building.lifts_array.get(i).get_references_for_UI();
           this.sliders.get(i).setValue(data.stage_num);
           this.lift_labels.get(i).setText(String.valueOf(data.passenger_num));
        }
    }

    @Override
    public void start(Stage stage) {
        this.building.launch_lifts();
        Thread rg = new Thread(new RequestsGenerator(building, request_generator_speed, num_requests_per_time));
        rg.start();
        HBox root = new HBox();
        Scene scene = new Scene(root);
        VBox stages_requests_info_container = new VBox();
        root.getChildren().add(stages_requests_info_container);
        for (int i = num_stages-1; i >=0; i--){
            String s = "Количество заявок на этаже №" + i + ":  " + 0;
            Label stage_label = new Label(s);
            stages_labels.add(stage_label);
            stage_label.setMinWidth(250);
            stage_label.setFont(new Font(14));
            stages_requests_info_container.getChildren().add(stage_label);
        }

        for (int i = 0; i < num_lifts; i++){
            Slider slider = new Slider();
            this.sliders.add(slider);
            root.getChildren().add(slider);
            slider.setMin(0);
            slider.setMax(this.num_stages - 1);
            slider.setOrientation(Orientation.VERTICAL);
            slider.setShowTickLabels(true);
            slider.setMajorTickUnit(1);
            slider.setBlockIncrement(1);
            slider.setSnapToTicks(true);
            slider.setMinHeight(500);
            slider.applyCss();
            slider.layout();

            Pane thumb = (Pane) slider.lookup(".thumb");
            Label label = new Label();
            this.lift_labels.add(label);
            thumb.getChildren().add(label);
        }
        
        Thread thread = new Thread(() -> {
            try {
                Runnable updater = this::updateUi;
                while (true) {
                    Thread.sleep(1000);
                    Platform.runLater(updater);
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }

        });
        thread.start();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}

