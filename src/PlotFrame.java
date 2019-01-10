import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlotFrame extends JFrame {

    PlotFrame(String title, String filePath) throws IOException {
        List<MarketRecord> list = parseCSVFile(filePath);
        Collections.reverse(list);

        ArrayList<String> dateList = list.stream().map(MarketRecord::getDate).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> openList = list.stream().map(MarketRecord::getOpen).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> highList = list.stream().map(MarketRecord::getHigh).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> lowList = list.stream().map(MarketRecord::getLow).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> closeList = list.stream().map(MarketRecord::getClose).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> volumeList = list.stream().map(MarketRecord::getVolume).collect(Collectors.toCollection(ArrayList::new));

        setTitle(title);

        GridLayout gridLayout = new GridLayout(2, 2);
        setLayout(gridLayout);

        LineChartPanel openPanel = new LineChartPanel("Open", dateList, openList);
        LineChartPanel closePanel = new LineChartPanel("Close", dateList, closeList);
        LineChartPanel volumePanel = new LineChartPanel("Volume", dateList, volumeList);
        LineChartPanel highLowPanel = new LineChartPanel("High and Low", dateList, highList, lowList);

        add(openPanel);
        add(closePanel);
        add(volumePanel);
        add(highLowPanel);
    }

    private static List<MarketRecord> parseCSVFile(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(new File(filePath));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<MarketRecord> inputList = bufferedReader.lines().skip(1).map(mapToItem).collect(Collectors.toList());
        bufferedReader.close();
        return inputList;
    }

    private static Function<String, MarketRecord> mapToItem = (line) -> {
        String[] p = line.replace(" ", "").split(",");

        String date = p[0];
        double open = Double.parseDouble(p[1]);
        double high = Double.parseDouble(p[2]);
        double low = Double.parseDouble(p[3]);
        double close = Double.parseDouble(p[4]);
        double volume = Double.parseDouble(p[5]);

        return new MarketRecord(date, open, high, low, close, volume);
    };

    public static void main(String[] args) {
    }
}
