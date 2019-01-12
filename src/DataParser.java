import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class DataParser {

    private static int dataSize;
    private static final String[] TITLE_ARRAY = {"OPEN", "CLOSE", "VOLUME", "HIGH & LOW"};

    DataParser(String filePath, String frameTitle, int frameWidth, int frameHeight) throws IOException {

        List<List<String>> recordCollection = Util.parseCSVFile(filePath);

        dataSize = recordCollection.size();

        List<String> dateList = new ArrayList<>();
        List<Double> openList = new ArrayList<>();
        List<Double> highList = new ArrayList<>();
        List<Double> lowList = new ArrayList<>();
        List<Double> closeList = new ArrayList<>();
        List<Double> volumeList = new ArrayList<>();

        for (int i = dataSize - 1; i >= 0; i--) {

            String[] date = recordCollection.get(i).get(0).split("/");
            dateList.add(date[1] + " " + Util.getShortMonth(date[0]) + " " + date[2]);

            openList.add(Double.parseDouble(recordCollection.get(i).get(1)));
            highList.add(Double.parseDouble(recordCollection.get(i).get(2)));
            lowList.add(Double.parseDouble(recordCollection.get(i).get(3)));
            closeList.add(Double.parseDouble(recordCollection.get(i).get(4)));
            volumeList.add(Double.parseDouble(recordCollection.get(i).get(5)));

        }

        List<LineChartPanel> panelList = new ArrayList<>();
        panelList.add(getLineChartPanel(TITLE_ARRAY[0], true, dateList, openList));
        panelList.add(getLineChartPanel(TITLE_ARRAY[1], true, dateList, closeList));
        panelList.add(getLineChartPanel(TITLE_ARRAY[2], false, dateList, volumeList));
        panelList.add(getLineChartPanel(TITLE_ARRAY[3], dateList, highList, lowList));

        PlotFrame plottingFrame = new PlotFrame(frameWidth, frameHeight, panelList);
        plottingFrame.setTitle(frameTitle);
        plottingFrame.setVisible(true);

    }

    private static LineChartPanel getLineChartPanel(String title, boolean isPrice, List<String> date, List<Double> data) {

        DataRange dataRange = getDataRange(data);
        List<Double> scaled = getScaledData(dataRange, data);
        List<String> records = getRecords(isPrice, date, data);

        return new LineChartPanel(title, scaled, dataRange, records);

    }

    private static LineChartPanel getLineChartPanel(String title, List<String> date, List<Double> high, List<Double> low) {

        DataRange dataRange = getDataRange(high, low);
        List<Double> highScaled = getScaledData(dataRange, high);
        List<Double> lowScaled = getScaledData(dataRange, low);
        List<String> records = getRecords(date, high, low);

        return new LineChartPanel(title, highScaled, lowScaled, dataRange, records);

    }

    private static DataRange getDataRange(List<Double> data) {
        return getRange(data, data);
    }

    private static DataRange getDataRange(List<Double> high, List<Double> low) {
        return getRange(high, low);
    }

    private static DataRange getRange(List<Double> high, List<Double> low) {

        double max = high.stream().max(Double::compareTo).orElse(0D);
        double min = low.stream().min(Double::compareTo).orElse(0D);

        double maxRange = Math.ceil(max * 1.1 / 5) * 5;
        double minRange = Math.floor(min * 0.9 / 5) * 5;

        return new DataRange(maxRange, minRange);

    }

    private static List<Double> getScaledData(DataRange dataRange, List<Double> data) {

        return data.stream().map(d -> (dataRange.getMaxRange() - d) / dataRange.getRange()).collect(Collectors.toList());

    }

    private static List<String> getRecords(boolean isPrice, List<String> date, List<Double> data) {

        List<String> records = new ArrayList<>();
        for (int i = 0; i < dataSize; i++) {
            String formattedData = Util.toFormattedNumberString(data.get(i));
            if (isPrice) {
                records.add(formattedData + " USD  " + date.get(i));
            } else {
                records.add(formattedData + " Shares  " + date.get(i));
            }
        }
        return records;

    }

    private static List<String> getRecords(List<String> date, List<Double> high, List<Double> low) {

        List<String> records = new ArrayList<>();
        for (int i = 0; i < dataSize; i++) {
            String highPrice = Util.toFormattedNumberString(high.get(i));
            String lowPrice = Util.toFormattedNumberString(low.get(i));
            records.add(highPrice + " USD  " + lowPrice + " USD  " + date.get(i));
        }
        return records;

    }
}
