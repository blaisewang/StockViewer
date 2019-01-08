import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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

    public static void main(String[] args) throws IOException {

        URL url = new URL("https://quotes.wsj.com/AAPL/historical-prices/download?MOD_VIEW=page&num_rows=300&startDate=12/10/2018&endDate=01/07/2019");
        URLConnection connection = url.openConnection();
        // check to make sure the page exists
        HttpURLConnection htCon = (HttpURLConnection) connection;
        int code = htCon.getResponseCode();
        String message = htCon.getResponseMessage();
        System.out.println(code + " " + message);

        if (code == HttpURLConnection.HTTP_OK) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("APPL.csv");
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

//        Application.launch(args);
    }

}
