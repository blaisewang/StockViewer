import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

public class Main extends Application {

    private final static int FRAME_WIDTH = 1440;
    private final static int FRAME_HEIGHT = 900;

    private final static String SYMBOL_FILE_PATH = "nasdaq-listed-symbols.csv";
    private final static String SYMBOL_FILE_URL = "https://datahub.io/core/nasdaq-listings/r/nasdaq-listed-symbols.csv";
    private final static String STOCK_URL = "https://quotes.wsj.com/#/historical-prices/download?MOD_VIEW=page&num_rows=%&startDate=[&endDate=]";

    @Override
    public void start(Stage primaryStage) throws IOException {

        SortedMap<String, String> symbolMap = new TreeMap<>();

        if (Util.downloadFile(SYMBOL_FILE_PATH, SYMBOL_FILE_URL)) {

            ArrayList<String[]> symbolCollection = Util.parseCSVFile(SYMBOL_FILE_PATH);

            symbolCollection.forEach(symbolAndName -> {
                String symbol = symbolAndName[0];
                String name = symbol + " - " + symbolAndName[1].replace("\"", "");
                symbolMap.put(symbol, name);
            });

        } else {

            symbolMap.put("FB", "FB - Facebook, Inc.");
            symbolMap.put("AAPL", "AAPL - Apple Inc.");
            symbolMap.put("MSFT", "MSFT - Microsoft Corporation");

        }

        primaryStage.setTitle("Nasdaq Stock Viewer");

        AutoCompleteTextField symbolSearchTextFiled = new AutoCompleteTextField(symbolMap);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        LocalDate yesterday = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        DatePicker startDatePicker = new DatePicker(yesterday);
        DatePicker endDatePicker = new DatePicker(LocalDate.now());

        Button viewButton = new Button("View");
        viewButton.setOnAction(e -> {
            String symbolAndName = symbolSearchTextFiled.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            Thread downloadThread = new Thread(new DownloadTask(symbolAndName, startDate, endDate));
            downloadThread.start();
        });

        HBox hbox = new HBox(symbolSearchTextFiled, startDatePicker, endDatePicker, viewButton);

        Scene scene = new Scene(hbox, 600, 400);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

    }

    private class DownloadTask implements Runnable {

        private String symbol;
        private LocalDate startDate;
        private LocalDate endDate;

        DownloadTask(String symbolAndName, LocalDate startDate, LocalDate endDate) {
            this.symbol = symbolAndName.split(" ")[0];
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public void run() {

            DateTimeFormatter formatterUK = DateTimeFormatter.ofPattern("dd_MM_uuuu");
            DateTimeFormatter formatterUS = DateTimeFormatter.ofPattern("MM/dd/uuuu");

            String startDateUK = startDate.format(formatterUK);
            String endDateUK = endDate.format(formatterUK);

            String url = STOCK_URL
                    .replace("#", symbol)
                    .replace("%", Long.toString(ChronoUnit.DAYS.between(startDate, endDate)))
                    .replace("[", startDate.format(formatterUS))
                    .replace("]", endDate.format(formatterUS));

            String filePath = symbol + "_" + startDateUK + "_" + endDateUK + ".csv";
            String frameTitle = (symbol + " " + startDateUK + " to " + endDateUK).replace('_', '/');

            try {
                if (Util.downloadFile(filePath, url)) {
                    Thread downloadThread = new Thread(new PlottingTask(filePath, frameTitle));
                    downloadThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class PlottingTask implements Runnable {

        private String filePath;
        private String frameTitle;

        PlottingTask(String filePath, String frameTitle) {
            this.filePath = filePath;
            this.frameTitle = frameTitle;
        }

        public void run() {
            try {
                new DataParser(filePath, frameTitle, FRAME_WIDTH, FRAME_HEIGHT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        Application.launch(args);

    }

}
