import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Utility class to provide common data method
 *
 * @author Xudong Wang (xwang199@sheffield.ac.uk)
 * @version 1.0 07 January 2019
 */


class Util {

    private static DecimalFormat integerFormatter = new DecimalFormat("#,###");
    private static DecimalFormat DecimalFormatter = new DecimalFormat("#,###.00");

    private static final String[] SHORT_MONTH_ARRAY = new DateFormatSymbols().getShortMonths();

    /**
     * Get short month name by month value string
     * e.g. 01 or 1 => Jan
     *
     * @param month string of month value
     * @return short month name
     */
    static String getShortMonth(String month) {

        return SHORT_MONTH_ARRAY[Integer.parseInt(month) - 1];

    }

    /**
     * Get formatted number string
     * e.g. 123456      =>  123,456
     * e.g. 123456.70   =>  123,456.7
     * e.g. 123456.789  =>  123,456.78
     *
     * @param value to be formatted
     * @return formatted value
     */
    static String toFormattedNumberString(double value) {

        if (value % 1 == 0) {
            // value is a integer
            return integerFormatter.format(value);
        }

        String string = DecimalFormatter.format(value);
        if (string.charAt(string.length() - 1) == '0') {
            // value is like '123.40', remove redundant 0
            string = string.substring(0, string.length() - 1);
        }

        return string;

    }

    /**
     * Get formatted number string based on min value to unify units
     * e.g. 12345678  =>  12.3 M
     *
     * @param value to be formatted
     * @param min   value
     * @return formatted value
     */
    static String toFormattedNumberString(double value, double min) {

        String string;

        if (min < 1e3) {
            // min < 1,000
            string = Util.toFormattedNumberString(value);
        } else if (min >= 1e3 && min < 1e6) {
            // 1,000 <= min < 1,000,000
            string = String.format("%.1f K", value / 1e3);
        } else if (min >= 1e6 && min < 1e9) {
            // 1,000,000 <= min < 1,000,000,000
            string = String.format("%.1f M", value / 1e6);
        } else {
            // min >= 1,000,000,000
            string = String.format("%.1f B", value / 1e9);
        }

        return string;

    }

    /**
     * Retrieve data from given https web address and save it to the given filepath
     *
     * @param filePath   to save the data
     * @param webAddress to retrieve data from
     * @return download result
     * @throws IOException if web address or filepath is invalid
     */
    static boolean downloadFile(String filePath, String webAddress) throws IOException {

        URL url = new URL(webAddress);
        URLConnection urlConnection = url.openConnection();
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;

        if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            // HTTP status code OK

            // file is downloaded directly into buffer
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            return true;
        }

        // URL invalid
        return false;
    }

    /**
     * Split CSV file by comma ','
     *
     * @param filePath CSV filepath
     * @return split CSV file
     * @throws IOException if filepath is invalid
     */
    static List<List<String>> splitCSVFile(String filePath) throws IOException {

        List<List<String>> collection = new ArrayList<>();

        // read file from buffer
        InputStream fileInputStream = new FileInputStream(new File(filePath));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        // skip header and split line with comma
        bufferedReader.lines().skip(1).forEach((l) -> collection.add(Arrays.asList(l.split(","))));
        bufferedReader.close();

        return collection;

    }

}
