import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;

class Util {

    private static DecimalFormat integerFormatter = new DecimalFormat("#,###");
    private static DecimalFormat oneDecimalFormatter = new DecimalFormat("#,###.0");
    private static DecimalFormat twoDecimalFormatter = new DecimalFormat("#,###.00");

    static String toFormattedNumberString(double value) {

        if (value % 1 == 0) {
            return integerFormatter.format(value);
        } else if (value % 0.5 == 0) {
            return oneDecimalFormatter.format(value);
        }
        return twoDecimalFormatter.format(value);

    }

    static boolean downloadFile(String filePath, String url) throws IOException {

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

    static ArrayList<String[]> parseCSVFile(String filePath) throws IOException {

        ArrayList<String[]> collection = new ArrayList<>();

        InputStream inputStream = new FileInputStream(new File(filePath));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        bufferedReader.lines().skip(1).forEach((line) -> collection.add(line.split(",")));
        bufferedReader.close();

        return collection;

    }

}
