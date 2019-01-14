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


/**
 * MarketGUI.java
 * initial interface of Nasdaq Stock Viewer.
 * Nasdaq Stock Viewer displays historical Nasdaq Stock Market data based on user selection.
 * The code should retrieve the list of companies on Nasdaq Stock Market and their ticker symbols,
 * the ticker symbols are searched via the autocomplete text field.
 * Once the valid start date and end date are given,
 * the program should retrieve the historical data that is designated.
 * The retrieved data is displayed on a new window with four line chart graphs.
 * The line chart graph traces the mouse position to display appropriate details.
 * <p>
 * MarketGUI class
 * main class is the main class of the program.
 * It retrieves Nasdaq company list and their ticker symbols, draws main interface of the program.
 *
 * @author Xudong Wang (xwang199@sheffield.ac.uk)
 * @version 1.0 12 January 2019
 */

public class MarketGUI extends Application {

    // number of window shakes and the duration of each shaking
    // when invalid data were given
    private static final int SHAKING_CYCLE = 3;
    private static final double SHAKING_DURATION = 0.08;

    // variable to represent the date of today
    private static final LocalDate today = LocalDate.now();

    // prefix tree to store ticker symbols and company names
    // key: ticker symbol value: ticker symbol - company name
    private static SortedMap<String, String> symbolMap = new TreeMap<>();

    // constant URL variables
    private static final String SYMBOL_FILE_PATH = "nasdaq-listed-symbols.csv";
    private static final String SYMBOL_FILE_URL = "https://datahub.io/core/nasdaq-listings/r/nasdaq-listed-symbols.csv";
    private static final String STOCK_URL_TEMPLATE = "https://quotes.wsj.com/%s/historical-prices/download?MOD_VIEW=page&num_rows=%s&startDate=%s&endDate=%s";

    /**
     * Start of JavaFX application
     *
     * @param primaryStage stage of the main window
     * @throws IOException if initialisation failed
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        initialise();

        // components on the main window are arranged in a 4 * 3 layout via GridPane
        GridPane gridPane = new GridPane();
        gridPane.setVgap(4);
        gridPane.setHgap(3);

        // margins to window edges
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        // margins of text and components to cell in the GridPane
        Insets textMargin = new Insets(0, 10, 3, 12);
        Insets fieldMargin = new Insets(0, 10, 3, 10);

        // text of ticker symbol
        Text symbolHintText = new Text("Ticker Symbol");
        GridPane.setMargin(symbolHintText, textMargin);
        gridPane.add(symbolHintText, 0, 0);

        // autocomplete text field with prompt text displayed when losing focus
        AutoCompleteTextField symbolSearchTextFiled = new AutoCompleteTextField(symbolMap);
        symbolSearchTextFiled.setPromptText("e.g. AAPL");
        GridPane.setMargin(symbolSearchTextFiled, fieldMargin);
        gridPane.add(symbolSearchTextFiled, 0, 1);

        // text of start date
        Text startDateHintText = new Text("Start Date");
        GridPane.setMargin(startDateHintText, textMargin);
        gridPane.add(startDateHintText, 1, 0);

        // default value of start date DatePicker is yesterday
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        LocalDate yesterday = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // start date DatePicker
        DatePicker startDatePicker = new DatePicker(yesterday);
        GridPane.setMargin(startDatePicker, fieldMargin);
        gridPane.add(startDatePicker, 1, 1);

        // text of end date
        Text endDateHintText = new Text("End Date");
        GridPane.setMargin(endDateHintText, textMargin);
        gridPane.add(endDateHintText, 2, 0);

        // default value of end date DatePicker is today
        DatePicker endDatePicker = new DatePicker(today);
        GridPane.setMargin(endDatePicker, fieldMargin);
        gridPane.add(endDatePicker, 2, 1);

        // error message label with default red font color
        Label errorMessageLabel = new Label("");
        errorMessageLabel.setFont(Font.font(11));
        errorMessageLabel.setTextFill(Color.RED);
        GridPane.setMargin(errorMessageLabel, new Insets(0, 0, 0, 14));
        gridPane.add(errorMessageLabel, 0, 2);

        // retrieve button, linked with previous components
        Button retrieveButton = new Button("Retrieve");
        retrieveButton.setOnAction(e -> {

            // the result of autocomplete text field is like 'TICKER_SYMBOL - company_name'
            // to get ticker symbol, the regex ' ' is applied
            // customise input like 'aapl' is converted to 'AAPL'
            String symbol = symbolSearchTextFiled.getText().split(" ")[0].toUpperCase();

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (symbol.length() == 0) {
                // empty ticker symbol
                errorMessageLabel.setText("EMPTY TICKER SYMBOL");
                shakeStage(primaryStage);
            } else if (!symbolMap.containsKey(symbol)) {
                // ticker symbol parsed is not on Nasdaq ticker symbol list
                errorMessageLabel.setText("NOT FOUND ON NASDAQ");
                shakeStage(primaryStage);
            } else if (!isValidDate(startDate, endDate)) {
                // invalid start date and/or end date
                errorMessageLabel.setText("INVALID DATE SELECTION");
                shakeStage(primaryStage);
            } else {
                // clear error message
                errorMessageLabel.setText("");
                // start new thread to retrieve stock market data
                Thread downloadThread = new Thread(new DataDownloadTask(symbol, startDate, endDate));
                downloadThread.start();
            }

        });

        // set the retrieve button in the centre horizontally
        GridPane.setHalignment(retrieveButton, HPos.CENTER);
        GridPane.setMargin(retrieveButton, new Insets(5, 0, 10, 0));
        // set span of the retrieve button is 3 columns
        gridPane.add(retrieveButton, 0, 3, 3, 1);

        // add GridPane to the scene
        Scene scene = new Scene(gridPane);

        // set scene to the stage
        primaryStage.setScene(scene);

        // set the window is not resizable.
        primaryStage.setResizable(false);

        primaryStage.setTitle("Nasdaq Stock Viewer");

        // set close event
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

    }

    /**
     * Ticker symbol list download function
     * the main thread is blocked until this function is finished
     *
     * @throws IOException if download or parsing failed
     */
    private void initialise() throws IOException {

        if (Util.downloadFile(SYMBOL_FILE_PATH, SYMBOL_FILE_URL)) {
            // file download succeeded, parse csv file to get a list of string list
            List<List<String>> symbolCollection = Util.splitCSVFile(SYMBOL_FILE_PATH);

            symbolCollection.forEach(line -> {
                // get ticker symbol from first column
                String symbol = line.get(0);
                // remove redundant double quotes in the second column
                String name = symbol + " - " + line.get(1).replace("\"", "");
                // put symbol and stitched string into the prefix tree
                symbolMap.put(symbol, name);
            });

        } else {
            // file download failed, put some samples into the prefix tree
            symbolMap.put("FB", "FB - Facebook, Inc.");
            symbolMap.put("AAPL", "AAPL - Apple Inc.");
            symbolMap.put("MSFT", "MSFT - Microsoft Corporation");

        }

    }

    /**
     * Window shaking animation function
     *
     * @param stage main stage of the window
     */
    private void shakeStage(Stage stage) {

        // animation in X-axis, lasts 2 * SHAKING_DURATION
        Timeline timelineX = new Timeline(
                // move the window right 5 pixels in first SHAKING_DURATION time
                new KeyFrame(Duration.seconds(SHAKING_DURATION), t -> stage.setX(stage.getX() + 5)),
                // move the window left 5 pixels in second SHAKING_DURATION time
                new KeyFrame(Duration.seconds(SHAKING_DURATION * 2), t -> stage.setX(stage.getX() - 5))
        );

        timelineX.setCycleCount(SHAKING_CYCLE);
        timelineX.play();

        // animation in Y-axis, lasts 2 * SHAKING_DURATION
        Timeline timelineY = new Timeline(
                // move window up 5 pixels in first SHAKING_DURATION time
                new KeyFrame(Duration.seconds(SHAKING_DURATION), t -> stage.setY(stage.getY() + 5)),
                // move window down 5 pixels in second SHAKING_DURATION time
                new KeyFrame(Duration.seconds(SHAKING_DURATION * 2), t -> stage.setY(stage.getY() - 5))
        );

        timelineY.setCycleCount(SHAKING_CYCLE);
        timelineY.play();

    }

    /**
     * Check the validation of start date and end date
     *
     * @param startDate start date selected
     * @param endDate   end date selected
     * @return boolean true for valid, false for invalid
     */
    private boolean isValidDate(LocalDate startDate, LocalDate endDate) {

        if (ChronoUnit.DAYS.between(startDate, endDate) < 0) {
            // end date is ahead of the start date
            return false;
        }
        if (ChronoUnit.DAYS.between(startDate, today) < 0) {
            // start date is in the future
            return false;
        }
        // false for end date is in the future
        // true for both dates are valid
        return ChronoUnit.DAYS.between(endDate, today) >= 0;

    }

    /**
     * Data download task thread
     */
    private class DataDownloadTask implements Runnable {

        private String symbol;
        private LocalDate startDate;
        private LocalDate endDate;

        /**
         * Class constructor
         *
         * @param symbol    ticker symbol
         * @param startDate start date
         * @param endDate   end date
         */
        DataDownloadTask(String symbol, LocalDate startDate, LocalDate endDate) {

            this.symbol = symbol;
            this.startDate = startDate;
            this.endDate = endDate;

        }

        /**
         * Main task to run
         */
        public void run() {

            // format date to '30_12_2018'
            DateTimeFormatter dateFormatterUK = DateTimeFormatter.ofPattern("dd_MM_uuuu");
            // format date to '12/30/2018'
            DateTimeFormatter dateFormatterUS = DateTimeFormatter.ofPattern("MM/dd/uuuu");

            String startDateUK = startDate.format(dateFormatterUK);
            String endDateUK = endDate.format(dateFormatterUK);

            // format the Wall Street Journal quoting URL
            String url = String.format(STOCK_URL_TEMPLATE,
                    symbol,
                    Long.toString(ChronoUnit.DAYS.between(startDate, endDate)),
                    startDate.format(dateFormatterUS),
                    endDate.format(dateFormatterUS));

            // format data filename
            String filePath = symbol + "_" + startDateUK + "_" + endDateUK + ".csv";

            try {
                if (Util.downloadFile(filePath, url)) {
                    // data download succeeded
                    // start new thread to plot the data
                    Thread plotThread = new Thread(new PlottingTask(filePath));
                    plotThread.start();
                }
            } catch (IOException e) {
                // data download failed
                e.printStackTrace();
            }
        }
    }

    /**
     * Plotting task thread
     */
    private class PlottingTask implements Runnable {

        private String filePath;

        /**
         * Class constructor
         *
         * @param filePath file path to parse data
         */
        PlottingTask(String filePath) {
            this.filePath = filePath;
        }

        /**
         * Main task to run
         */
        public void run() {
            try {
                // parse CSV file downloaded
                DataParser.parseData(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Main function of the program
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // launch JavaFX application
        Application.launch(args);

    }

}
