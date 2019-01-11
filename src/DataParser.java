import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;

class DataParser {

    private static int dataSize;

    DataParser(String filePath, String frameTitle, int frameWidth, int frameHeight) throws IOException {

        ArrayList<String[]> recordCollection = Util.parseCSVFile(filePath);

        dataSize = recordCollection.size();
        Collections.reverse(recordCollection);

        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Double> openList = new ArrayList<>();
        ArrayList<Double> highList = new ArrayList<>();
        ArrayList<Double> lowList = new ArrayList<>();
        ArrayList<Double> closeList = new ArrayList<>();
        ArrayList<Double> volumeList = new ArrayList<>();

        for (int i = 0; i < dataSize; i++) {

            String[] date = recordCollection.get(i)[0].split("/");
            dateList.add(date[1] + " " + getMonthAbbreviation(date[0]) + " " + date[2]);

            openList.add(Double.parseDouble(recordCollection.get(i)[1]));
            highList.add(Double.parseDouble(recordCollection.get(i)[2]));
            lowList.add(Double.parseDouble(recordCollection.get(i)[3]));
            closeList.add(Double.parseDouble(recordCollection.get(i)[4]));
            volumeList.add(Double.parseDouble(recordCollection.get(i)[5]));
        }

        double[] openRange = getRangeList(openList);
        double[] openScaled = getScaledData(openRange, openList);
        ArrayList<String> openRecords = getRecords(dateList, openList);
        LineChartPanel openPanel = new LineChartPanel("Open", openScaled, openRange, openRecords);

        double[] closeRange = getRangeList(closeList);
        double[] closeScaled = getScaledData(closeRange, closeList);
        ArrayList<String> closeRecords = getRecords(dateList, closeList);
        LineChartPanel closePanel = new LineChartPanel("Close", closeScaled, closeRange, closeRecords);

        double[] volumeRange = getRangeList(volumeList);
        double[] volumeScaled = getScaledData(volumeRange, volumeList);
        ArrayList<String> volumeRecords = getRecords(dateList, volumeList);
        LineChartPanel volumePanel = new LineChartPanel("Volume", volumeScaled, volumeRange, volumeRecords);

        double[] highLowRange = getRangeList(highList, lowList);
        double[] highScaled = getScaledData(highLowRange, highList);
        double[] lowScaled = getScaledData(highLowRange, lowList);
        ArrayList<String> highLowRecords = getRecords(dateList, highList, lowList);
        LineChartPanel highLowPanel = new LineChartPanel("High and Low", highScaled, lowScaled, highLowRange, highLowRecords);

        PlotFrame plottingFrame = new PlotFrame(frameWidth, frameHeight, openPanel, closePanel, volumePanel, highLowPanel);
        plottingFrame.setTitle(frameTitle);
        plottingFrame.setVisible(true);
    }

    private static String getMonthAbbreviation(String month) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        return dateFormatSymbols.getMonths()[Integer.parseInt(month) - 1].substring(0, 3).toUpperCase();
    }

    private static double[] getRangeList(ArrayList<Double> data) {
        return getRange(data, data);
    }

    private static double[] getRangeList(ArrayList<Double> high, ArrayList<Double> low) {
        return getRange(high, low);
    }

    private static double[] getRange(ArrayList<Double> high, ArrayList<Double> low) {

        double max = high.stream().mapToDouble(v -> v).max().orElse(0);
        double min = low.stream().mapToDouble(v -> v).min().orElse(0);

        double maxRange = Math.ceil(max * 1.1 / 5) * 5;
        double minRange = Math.floor(min * 0.9 / 5) * 5;

        return new double[]{minRange, maxRange};

    }

    private static double[] getScaledData(double[] range, ArrayList<Double> data) {

        double yRange = range[1] - range[0];
        double[] yCoordinates = new double[dataSize];

        for (int i = 0; i < dataSize; i++) {
            yCoordinates[i] = ((range[1] - data.get(i)) / yRange);
        }

        return yCoordinates;

    }

    private static ArrayList<String> getRecords(ArrayList<String> date, ArrayList<Double> data) {

        ArrayList<String> records = new ArrayList<>();
        for (int i = 0; i < dataSize; i++) {
            String price = Util.toFormattedNumberString(data.get(i));
            records.add(price + " USD  " + date.get(i));
        }
        return records;

    }

    private static ArrayList<String> getRecords(ArrayList<String> date, ArrayList<Double> high, ArrayList<Double> low) {

        ArrayList<String> records = new ArrayList<>();
        for (int i = 0; i < dataSize; i++) {
            String highPrice = Util.toFormattedNumberString(high.get(i));
            String lowPrice = Util.toFormattedNumberString(low.get(i));
            records.add(highPrice + " USD  " + lowPrice + " USD  " + date.get(i));
        }
        return records;

    }
}
