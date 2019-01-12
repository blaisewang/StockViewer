import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class DataParser {

    private static int dataSize;
    private static final String FRAME_TITLE_TEMPLATE = "%s  %s/%s/%s  to  %s/%s/%s";
    private static final String[] CAPTION_ARRAY = {"OPEN", "CLOSE", "VOLUME", "HIGH & LOW"};

    DataParser(String filePath) throws IOException {

        List<List<String>> collections = Util.parseCSVFile(filePath);

        dataSize = collections.size();

        List<String> dateList = new ArrayList<>();
        List<Double> openList = new ArrayList<>();
        List<Double> highList = new ArrayList<>();
        List<Double> lowList = new ArrayList<>();
        List<Double> closeList = new ArrayList<>();
        List<Double> volumeList = new ArrayList<>();

        for (int i = dataSize - 1; i >= 0; i--) {

            String[] date = collections.get(i).get(0).split("/");
            dateList.add(date[1] + " " + Util.getShortMonth(date[0]) + " " + date[2]);

            openList.add(getData(1, collections.get(i)));
            highList.add(getData(2, collections.get(i)));
            lowList.add(getData(3, collections.get(i)));
            closeList.add(getData(4, collections.get(i)));
            volumeList.add(getData(5, collections.get(i)));

        }

        List<LineChartPanel> panelList = new ArrayList<>();
        panelList.add(getLineChartPanel(CAPTION_ARRAY[0], true, dateList, openList));
        panelList.add(getLineChartPanel(CAPTION_ARRAY[1], true, dateList, closeList));
        panelList.add(getLineChartPanel(CAPTION_ARRAY[2], false, dateList, volumeList));
        panelList.add(getLineChartPanel(CAPTION_ARRAY[3], dateList, highList, lowList));

        PlotFrame plottingFrame = new PlotFrame(getFrameTitle(filePath), panelList);
        plottingFrame.setVisible(true);

    }

    private double getData(int index, List<String> stringList) {

        try {
            return Double.parseDouble(stringList.get(index));
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }

    }

    private String getFrameTitle(String filePath) {

        String[] pathArray = filePath.split("/");
        pathArray = pathArray[pathArray.length - 1].split(".csv")[0].split("_");
        return String.format(
                FRAME_TITLE_TEMPLATE,
                pathArray[0],
                pathArray[1],
                pathArray[2],
                pathArray[3],
                pathArray[4],
                pathArray[5],
                pathArray[6]
        );

    }

    private static LineChartPanel getLineChartPanel(String caption,
                                                    boolean isPrice,
                                                    List<String> date,
                                                    List<Double> data) {

        Range range = getRange(data);
        List<Double> scaled = getScaledData(range, data);
        List<String> records = getRecords(isPrice, date, data);

        return new LineChartPanel(caption, scaled, range, records);

    }

    private static LineChartPanel getLineChartPanel(String caption,
                                                    List<String> date,
                                                    List<Double> high,
                                                    List<Double> low) {

        Range range = getRange(high, low);
        List<Double> highScaled = getScaledData(range, high);
        List<Double> lowScaled = getScaledData(range, low);
        List<String> records = getRecords(date, high, low);

        return new LineChartPanel(caption, highScaled, lowScaled, range, records);

    }

    private static Range getRange(List<Double> data) {
        return getDataRange(data, data);
    }

    private static Range getRange(List<Double> high, List<Double> low) {
        return getDataRange(high, low);
    }

    private static Range getDataRange(List<Double> high, List<Double> low) {

        double max = high.stream().max(Double::compareTo).orElse(0D);
        double min = low.stream().min(Double::compareTo).orElse(0D);

        double maxRange = Math.ceil(max * 1.1 / 5) * 5;
        double minRange = Math.floor(min * 0.9 / 5) * 5;

        return new Range(maxRange, minRange);

    }

    private static List<Double> getScaledData(Range range, List<Double> data) {

        return data.stream().map(d -> (range.getMax() - d) / range.getRange()).collect(Collectors.toList());

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
