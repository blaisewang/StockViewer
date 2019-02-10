import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Utility class to parse data
 *
 * @author Xudong Wang (xwang199@sheffield.ac.uk)
 * @version 1.0 11 January 2019
 */


class DataParser {

    private static int dataSize;

    // frame title format template
    private static final String FRAME_TITLE_TEMPLATE = "%s  %s/%s/%s  to  %s/%s/%s";

    private static final String[] CAPTION_ARRAY = {"OPEN", "CLOSE", "VOLUME", "HIGH & LOW"};

    /**
     * Entrance to parse data
     *
     * @param filePath of CSV File to parse
     * @throws IOException if parsing failed
     */
    static void parseData(String filePath) throws IOException {

        // get split CSV file
        List<List<String>> collections = Util.splitCSVFile(filePath);

        dataSize = collections.size();

        List<String> dateList = new ArrayList<>();
        List<Double> openList = new ArrayList<>();
        List<Double> highList = new ArrayList<>();
        List<Double> lowList = new ArrayList<>();
        List<Double> closeList = new ArrayList<>();
        List<Double> volumeList = new ArrayList<>();

        // retrieved data is descending order
        // to display data in ascending order the loop is descending
        for (int i = dataSize - 1; i >= 0; i--) {

            // the format date retrieved is like '12/30/2018'
            String[] date = collections.get(i).get(0).split("/");
            dateList.add(date[1] + " " + Util.getShortMonth(date[0]) + " " + date[2]);

            openList.add(getData(1, collections.get(i)));
            highList.add(getData(2, collections.get(i)));
            lowList.add(getData(3, collections.get(i)));
            closeList.add(getData(4, collections.get(i)));
            volumeList.add(getData(5, collections.get(i)));

        }

        // construct panel list
        List<LineChartPanel> panelList = new ArrayList<>();
        panelList.add(getLineChartPanel(CAPTION_ARRAY[0], true, dateList, openList));
        panelList.add(getLineChartPanel(CAPTION_ARRAY[1], true, dateList, closeList));
        panelList.add(getLineChartPanel(CAPTION_ARRAY[2], false, dateList, volumeList));
        panelList.add(getLineChartPanel(CAPTION_ARRAY[3], dateList, highList, lowList));

        // Start a JFrame to display four line chart panels
        String frameTitle = getFrameTitle(filePath);
        PlotFrame plottingFrame = new PlotFrame(frameTitle, panelList);
        plottingFrame.setVisible(true);

    }

    /**
     * Get data from the string list by index given
     * Exception is applied for missing value
     *
     * @param index      of data
     * @param stringList split string
     * @return double value of data
     */
    private static double getData(int index, List<String> stringList) {

        try {
            return Double.parseDouble(stringList.get(index));
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }

    }

    /**
     * Get frame tile by filepath of the CSV file
     *
     * @param filePath of the CSV file
     * @return JFrame title
     */
    private static String getFrameTitle(String filePath) {

        // split prefix of path and file name
        String[] pathArray = filePath.split("/");

        // split suffix of filename and file extension
        pathArray = pathArray[pathArray.length - 1].split(".csv")[0].split("_");

        // return formatted title string
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

    /**
     * Get LineChartPanel object
     *
     * @param caption of line chart
     * @param isPrice data is price or not
     * @param date    the list date retrieved
     * @param data    data retrieved
     * @return LineChartPanel object
     */
    private static LineChartPanel getLineChartPanel(
            String caption,
            boolean isPrice,
            List<String> date,
            List<Double> data) {

        // get range of data
        Range range = getRange(data);
        // get scaled data by range
        List<Double> scaled = getScaledData(range, data);
        // get detailed information by date and data retrieved
        List<String> details = getDetails(isPrice, date, data);

        return new LineChartPanel(caption, scaled, range, details);

    }


    /**
     * Get LineChartPanel object
     *
     * @param caption of line chart
     * @param date    the list date retrieved
     * @param high    high data
     * @param low     low data
     * @return LineChartPanel object
     */
    private static LineChartPanel getLineChartPanel(
            String caption,
            List<String> date,
            List<Double> high,
            List<Double> low) {

        // get range of high and low data
        Range range = getRange(high, low);
        // get scaled data by range
        List<Double> highScaled = getScaledData(range, high);
        List<Double> lowScaled = getScaledData(range, low);
        // get detailed information by date, high, and low data.
        List<String> details = getDetails(date, high, low);

        return new LineChartPanel(caption, highScaled, lowScaled, range, details);

    }

    /**
     * Get range of data
     *
     * @param data given
     * @return Range object
     */
    private static Range getRange(List<Double> data) {
        return getDataRange(data, data);
    }

    /**
     * Get range of high and low
     *
     * @param high data
     * @param low  data
     * @return Range object
     */
    private static Range getRange(List<Double> high, List<Double> low) {
        return getDataRange(high, low);
    }

    /**
     * Get data range by high data and low data
     *
     * @param high data
     * @param low  data
     * @return Range object
     */
    private static Range getDataRange(List<Double> high, List<Double> low) {

        // get max and min of high and low separately
        double max = high.stream().max(Double::compareTo).orElse(0D);
        double min = low.stream().min(Double::compareTo).orElse(0D);

        // 10% space for max and min for graph beautifying
        // round range to nearest multiple of 5 to avoid infinite decimal in y-axis labels
        double maxRange = Math.ceil(max * 1.1 / 5) * 5;
        double minRange = Math.floor(min * 0.9 / 5) * 5;

        return new Range(maxRange, minRange);

    }

    /**
     * Get scaled data by range
     * This is also called normalisation in other subject
     *
     * @param range given
     * @param data  given
     * @return scaled data
     */
    private static List<Double> getScaledData(Range range, List<Double> data) {

        // d_new = (max - d_old) / (max - min)
        return data.stream().map(d -> (range.getMax() - d) / range.getRange()).collect(Collectors.toList());

    }

    /**
     * Get detailed information for each data
     *
     * @param isPrice boolean value to decide the unit of data
     * @param date    of each data
     * @param data    given
     * @return list of detailed information
     */
    private static List<String> getDetails(boolean isPrice, List<String> date, List<Double> data) {

        List<String> details = new ArrayList<>();

        for (int i = 0; i < dataSize; i++) {
            // format data
            String formattedData = Util.toFormattedNumberString(data.get(i));
            if (isPrice) {
                // add 'USD' unit for price
                details.add(formattedData + " USD  " + date.get(i));
            } else {
                // add 'Shares' unit for volume
                details.add(formattedData + " Shares  " + date.get(i));
            }
        }

        return details;

    }

    /**
     * Get detailed information for each data
     *
     * @param date of each data
     * @param high data
     * @param low  data
     * @return list of detailed information
     */
    private static List<String> getDetails(List<String> date, List<Double> high, List<Double> low) {

        List<String> details = new ArrayList<>();

        for (int i = 0; i < dataSize; i++) {
            // format high and low data
            String highPrice = Util.toFormattedNumberString(high.get(i));
            String lowPrice = Util.toFormattedNumberString(low.get(i));
            // stitch text
            details.add(highPrice + " USD  " + lowPrice + " USD  " + date.get(i));
        }

        return details;

    }
}
