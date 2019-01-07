import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlotFrame extends JFrame {

    private PlotFrame() throws IOException {
        List<Record> list = parseCSVFile("APPL.csv");

        ArrayList<String> dateList = list.stream().map(Record::getDate).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> openList = list.stream().map(Record::getOpen).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> highList = list.stream().map(Record::getHigh).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> lowList = list.stream().map(Record::getLow).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Double> closeList = list.stream().map(Record::getClose).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Long> volumeList = list.stream().map(Record::getVolume).collect(Collectors.toCollection(ArrayList::new));

        setTitle("A simple JFrame with content");

        GridLayout gridLayout = new GridLayout(2, 2);
        setLayout(gridLayout);

        LineChartPanel<Double> openPanel = new LineChartPanel<>("Open", dateList, openList);
        LineChartPanel<Double> closePanel = new LineChartPanel<>("Close", dateList, closeList);
        LineChartPanel<Long> volumePanel = new LineChartPanel<>("Volume", dateList, volumeList);
        LineChartPanel<Double> highLowPanel = new LineChartPanel<>("High and Low", dateList, highList, lowList);

        add(openPanel);
        add(closePanel);
        add(volumePanel);
        add(highLowPanel);
    }

    private static List<Record> parseCSVFile(String filePath) throws IOException {
        InputStream inputStream = new FileInputStream(new File(filePath));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<Record> inputList = bufferedReader.lines().skip(1).map(mapToItem).collect(Collectors.toList());
        bufferedReader.close();
        return inputList;
    }

    private static Function<String, Record> mapToItem = (line) -> {
        String[] p = line.replace(" ", "").split(",");

        String date = p[0];
        double open = Double.parseDouble(p[1]);
        double high = Double.parseDouble(p[2]);
        double low = Double.parseDouble(p[3]);
        double close = Double.parseDouble(p[4]);
        long volume = Long.parseLong(p[5]);

        return new Record(date, open, high, low, close, volume);
    };

    public static void main(String[] args) throws IOException {
        int preferredWidth = 1400;
        int preferredHeight = 900;

        JFrame frame = new PlotFrame();

        // first set the size
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
    }
}
