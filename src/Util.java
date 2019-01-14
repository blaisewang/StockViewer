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


class Util {

    private static DecimalFormat integerFormatter = new DecimalFormat("#,###");
    private static DecimalFormat DecimalFormatter = new DecimalFormat("#,###.00");

    private static final String[] SHORT_MONTH_ARRAY = new DateFormatSymbols().getShortMonths();

    static String getShortMonth(String month) {

        return SHORT_MONTH_ARRAY[Integer.parseInt(month) - 1];

    }

    static String toFormattedNumberString(double value) {

        if (value % 1 == 0) {
            return integerFormatter.format(value);
        }

        String string = DecimalFormatter.format(value);
        if (string.charAt(string.length() - 1) == '0') {
            string = string.substring(0, string.length() - 1);
        }

        return string;

    }

    static String toFormattedNumberString(double value, double min) {

        String label;

        if (min < 1e3) {
            label = Util.toFormattedNumberString(value);
        } else if (min >= 1e3 && min < 1e6) {
            label = String.format("%.1f K", value / 1e3);
        } else if (min >= 1e6 && min < 1e9){
            label = String.format("%.1f M", value / 1e6);
        } else {
            label = String.format("%.1f B", value / 1e9);
        }

        return label;

    }

    static boolean downloadFile(String filePath, String webAddress) throws IOException {

        URL url = new URL(webAddress);
        URLConnection urlConnection = url.openConnection();
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;

        if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            return true;
        }

        return false;
    }

    static List<List<String>> splitCSVFile(String filePath) throws IOException {

        List<List<String>> collection = new ArrayList<>();

        InputStream fileInputStream = new FileInputStream(new File(filePath));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

        bufferedReader.lines().skip(1).forEach((l) -> collection.add(Arrays.asList(l.split(","))));
        bufferedReader.close();

        return collection;

    }

}
