import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

public class Main extends Application {

    private final static String SYMBOL_FILE_PATH = "nasdaq-listed-symbols.csv";
    private final static String SYMBOL_FILE_URL = "https://datahub.io/core/nasdaq-listings/r/nasdaq-listed-symbols.csv";

    private final static String STOCK_URL = "https://quotes.wsj.com/#/historical-prices/download?MOD_VIEW=page&num_rows=%&startDate=[&endDate=]";

    @Override
    public void start(Stage primaryStage) throws IOException {

        File symbolFile = new File(SYMBOL_FILE_PATH);
        SortedMap<String, String> symbolMap = new TreeMap<>();

        if (downloadFile(SYMBOL_FILE_URL, SYMBOL_FILE_PATH)) {

            InputStream symbolFileInputStream = new FileInputStream(symbolFile);
            BufferedReader SymbolBufferedReader = new BufferedReader(new InputStreamReader(symbolFileInputStream));

            SymbolBufferedReader.lines().skip(1).forEach((line) -> {

                String[] symbolAndName = line.replace("\"", "").split(",");
                symbolMap.put(symbolAndName[0], symbolAndName[0] + " - " + symbolAndName[1]);

            });
            SymbolBufferedReader.close();

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
        primaryStage.show();

    }

    private boolean downloadFile(String url, String filePath) throws IOException {
        URL symbolURL = new URL(url);
        URLConnection symbolURLConnection = symbolURL.openConnection();
        HttpsURLConnection symbolHttpsURLConnection = (HttpsURLConnection) symbolURLConnection;

        if (symbolHttpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            ReadableByteChannel symbolReadableByteChannel = Channels.newChannel(symbolURL.openStream());
            FileOutputStream symbolFileOutputStream = new FileOutputStream(filePath);
            symbolFileOutputStream.getChannel().transferFrom(symbolReadableByteChannel, 0, Long.MAX_VALUE);

            return true;
        }
        return false;
    }


    private String getMonthAbbreviation(String month) {

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        return dateFormatSymbols.getMonths()[Integer.parseInt(month) - 1].substring(0, 3).toUpperCase();

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
            String[] startDateArray = startDate.toString().split("-");
            String[] endDateArray = endDate.toString().split("-");

            String startDay = startDateArray[2];
            String startMonth = startDateArray[1];
            String startYear = startDateArray[0];

            String endDay = endDateArray[2];
            String endMonth = endDateArray[1];
            String endYear = endDateArray[0];

            String startDateUS = startMonth + "/" + startDay + "/" + startYear;
            String endDateUS = endMonth + "/" + endDay + "/" + endYear;

            String startDateUK = startDay + getMonthAbbreviation(startMonth) + startYear.substring(2, 4);
            String endDateUK = endDay + getMonthAbbreviation(endMonth) + endYear.substring(2, 4);

            Long maxRowNumber = ChronoUnit.DAYS.between(startDate, endDate);

            String title = symbol + "  " + startDateUK + "  -  " + endDateUK;

            String url = STOCK_URL
                    .replace("#", symbol)
                    .replace("%", Long.toString(maxRowNumber))
                    .replace("[", startDateUS)
                    .replace("]", endDateUS);

            String filePath = symbol + "_" + startDateUK + "_" + endDateUK + ".csv";

            System.out.println(url);
            System.out.println(filePath);

            try {
                if (downloadFile(url, filePath)) {
                    Thread downloadThread = new Thread(new PlottingTask(title, filePath));
                    downloadThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class PlottingTask implements Runnable {

        private String title;
        private String filePath;

        PlottingTask(String title, String filePath) {
            this.title = title;
            this.filePath = filePath;
        }

        public void run() {
            int preferredWidth = 1400;
            int preferredHeight = 900;

            try {
                JFrame frame = new PlotFrame(title, filePath);

                frame.setSize(preferredWidth, preferredHeight);

                Dimension actualSize = frame.getContentPane().getSize();

                int extraWidth = preferredWidth - actualSize.width;
                int extraHeight = preferredHeight - actualSize.height;

                frame.setSize(preferredWidth + extraWidth, preferredHeight + extraHeight);

                frame.pack();
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);

    }

}
