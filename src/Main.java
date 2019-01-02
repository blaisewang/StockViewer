import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Button Experiment 1");

        DatePicker startDatePicker = new DatePicker();

        DatePicker endDatePicker = new DatePicker();


        HBox hbox = new HBox(startDatePicker, endDatePicker);

        Scene scene = new Scene(hbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
