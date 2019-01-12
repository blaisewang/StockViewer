import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class Main extends Application {

    private static final int SHAKING_CYCLE = 3;
    private static final double SHAKING_DURATION = 0.08;

    private static final LocalDate today = LocalDate.now();
    private SortedMap<String, String> symbolMap = new TreeMap<>();

    private static final String SYMBOL_FILE_PATH = "nasdaq-listed-symbols.csv";
    private static final String SYMBOL_FILE_URL = "https://datahub.io/core/nasdaq-listings/r/nasdaq-listed-symbols.csv";
    private static final String STOCK_URL_TEMPLATE = "https://quotes.wsj.com/%s/historical-prices/download?MOD_VIEW=page&num_rows=%s&startDate=%s&endDate=%s";

    @Override
    public void start(Stage primaryStage) throws IOException {

        initialise();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(4);
        gridPane.setVgap(4);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        Insets textMargin = new Insets(0, 10, 3, 12);
        Insets fieldMargin = new Insets(0, 10, 3, 10);

        Text symbolHintText = new Text("Stock Symbol");
        GridPane.setMargin(symbolHintText, textMargin);
        gridPane.add(symbolHintText, 0, 0);

        AutoCompleteTextField symbolSearchTextFiled = new AutoCompleteTextField(symbolMap);
        symbolSearchTextFiled.setPromptText("e.g. AAPL");

        GridPane.setMargin(symbolSearchTextFiled, fieldMargin);
        gridPane.add(symbolSearchTextFiled, 0, 1);

        Text startDateHintText = new Text("Start Date");
        GridPane.setMargin(startDateHintText, textMargin);
        gridPane.add(startDateHintText, 1, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        LocalDate yesterday = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DatePicker startDatePicker = new DatePicker(yesterday);

        GridPane.setMargin(startDatePicker, fieldMargin);
        gridPane.add(startDatePicker, 1, 1);

        Text endDateHintText = new Text("End Date");
        GridPane.setMargin(endDateHintText, textMargin);
        gridPane.add(endDateHintText, 2, 0);

        DatePicker endDatePicker = new DatePicker(today);

        GridPane.setMargin(endDatePicker, fieldMargin);
        gridPane.add(endDatePicker, 2, 1);

        Label errorHintLabel = new Label("");
        errorHintLabel.setFont(Font.font(11));
        errorHintLabel.setTextFill(Color.RED);
        GridPane.setMargin(errorHintLabel, new Insets(0, 0, 0, 13));
        gridPane.add(errorHintLabel, 0, 2);

        Button retrieveButton = new Button("Retrieve");
        retrieveButton.setOnAction(e -> {
            String symbol = symbolSearchTextFiled.getText().split(" ")[0].toUpperCase();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (symbol.length() == 0) {
                errorHintLabel.setText("EMPTY STOCK SYMBOL");
                shakeStage(primaryStage);
            } else if (!symbolMap.containsKey(symbol)) {
                errorHintLabel.setText("NOT FOUND ON NASDAQ");
                shakeStage(primaryStage);
            } else if (!isLegalDate(startDate, endDate)) {
                errorHintLabel.setText("ILLEGAL DATE SELECTION");
                shakeStage(primaryStage);
            } else {
                errorHintLabel.setText("");
                Thread downloadThread = new Thread(new DownloadTask(symbol, startDate, endDate));
                downloadThread.start();
            }

        });

        GridPane.setHalignment(retrieveButton, HPos.CENTER);
        GridPane.setMargin(retrieveButton, new Insets(5, 0, 10, 0));
        gridPane.add(retrieveButton, 0, 3, 4, 1);

        Scene scene = new Scene(gridPane);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setTitle("Nasdaq Stock Viewer");

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

    }

    private void initialise() throws IOException {

        if (Util.downloadFile(SYMBOL_FILE_PATH, SYMBOL_FILE_URL)) {

            List<List<String>> symbolCollection = Util.parseCSVFile(SYMBOL_FILE_PATH);

            symbolCollection.forEach(line -> {
                String symbol = line.get(0);
                String name = symbol + " - " + line.get(1).replace("\"", "");
                symbolMap.put(symbol, name);
            });

        } else {

            symbolMap.put("FB", "FB - Facebook, Inc.");
            symbolMap.put("AAPL", "AAPL - Apple Inc.");
            symbolMap.put("MSFT", "MSFT - Microsoft Corporation");

        }

    }


    private void shakeStage(Stage stage) {

        Timeline timelineX = new Timeline(
                new KeyFrame(Duration.seconds(SHAKING_DURATION), t -> stage.setX(stage.getX() + 5)),
                new KeyFrame(Duration.seconds(SHAKING_DURATION * 2), t -> stage.setX(stage.getX() - 5))
        );

        timelineX.setCycleCount(SHAKING_CYCLE);
        timelineX.play();

        Timeline timelineY = new Timeline(
                new KeyFrame(Duration.seconds(SHAKING_DURATION), t -> stage.setY(stage.getY() + 5)),
                new KeyFrame(Duration.seconds(SHAKING_DURATION * 2), t -> stage.setY(stage.getY() - 5))
        );

        timelineY.setCycleCount(SHAKING_CYCLE);
        timelineY.play();

    }

    private boolean isLegalDate(LocalDate startDate, LocalDate endDate) {

        if (ChronoUnit.DAYS.between(startDate, endDate) < 0) {
            return false;
        }
        if (ChronoUnit.DAYS.between(startDate, today) < 0) {
            return false;
        }
        return ChronoUnit.DAYS.between(endDate, today) >= 0;

    }

    private class DownloadTask implements Runnable {

        private String symbol;
        private LocalDate startDate;
        private LocalDate endDate;

        DownloadTask(String symbol, LocalDate startDate, LocalDate endDate) {

            this.symbol = symbol;
            this.startDate = startDate;
            this.endDate = endDate;

        }

        public void run() {

            DateTimeFormatter dateFormatterUK = DateTimeFormatter.ofPattern("dd_MM_uuuu");
            DateTimeFormatter dateFormatterUS = DateTimeFormatter.ofPattern("MM/dd/uuuu");

            String startDateUK = startDate.format(dateFormatterUK);
            String endDateUK = endDate.format(dateFormatterUK);

            String url = String.format(STOCK_URL_TEMPLATE,
                    symbol,
                    Long.toString(ChronoUnit.DAYS.between(startDate, endDate)),
                    startDate.format(dateFormatterUS),
                    endDate.format(dateFormatterUS));

            String filePath = symbol + "_" + startDateUK + "_" + endDateUK + ".csv";

            try {
                if (Util.downloadFile(filePath, url)) {
                    Thread plotThread = new Thread(new PlottingTask(filePath));
                    plotThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class PlottingTask implements Runnable {

        private String filePath;

        PlottingTask(String filePath) {
            this.filePath = filePath;
        }

        public void run() {
            try {
                new DataParser(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {

        Application.launch(args);

    }

}
