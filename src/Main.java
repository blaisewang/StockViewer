import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.SortedMap;
import java.util.TreeMap;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws IOException {
        SortedMap<String, String> stock = new TreeMap<>();

        URL url = new URL("https://pkgstore.datahub.io/core/nasdaq-listings/nasdaq-listed-symbols_csv/data/595a1f263719c09a8a0b4a64f17112c6/nasdaq-listed-symbols_csv.csv");
        URLConnection connection = url.openConnection();
        // check to make sure the page exists
        HttpURLConnection htCon = (HttpURLConnection) connection;
        int code = htCon.getResponseCode();

        if (code == HttpURLConnection.HTTP_OK) {
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream("nasdaq-listed-symbols_csv.csv");
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

        InputStream inputStream = new FileInputStream(new File("nasdaq-listed-symbols_csv.csv"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        bufferedReader.lines().skip(1).forEach((line) -> {
            String[] p = line.replace("\"", "").split(",");
            stock.put(p[0], p[0] + " - " + p[1]);
        });
        bufferedReader.close();

        AutoCompleteTextField search = new AutoCompleteTextField(stock);

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        HBox hbox = new HBox(search, startDatePicker, endDatePicker);

        primaryStage.setTitle("Nasdaq Stock Viewer");

        Scene scene = new Scene(hbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }


}
