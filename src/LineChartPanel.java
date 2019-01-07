import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LineChartPanel<T extends Number> extends JPanel {

    final static int WIDTH = 600;
    final static int HEIGHT = 400;

    private String title;
    private List<String> date;
    private ArrayList[] data;
    private long max;
    private long min;


    LineChartPanel(String title, List<String> date, ArrayList<T> data) {
        double max = 0;
        double min = 0;

        Optional<T> maxOptional = data.stream().max(Comparator.comparing(T::doubleValue));
        Optional<T> minOptional = data.stream().min(Comparator.comparing(T::doubleValue));

        if (maxOptional.isPresent() && minOptional.isPresent()) {
            max = maxOptional.get().doubleValue();
            min = minOptional.get().doubleValue();
        }

        Initialise(title, date, max, min, new ArrayList[]{data});
    }

    LineChartPanel(String title, List<String> date, ArrayList<Double> high, ArrayList<Double> low) {
        double max = high.stream().mapToDouble(v -> v).max().orElse(0);
        double min = low.stream().mapToDouble(v -> v).min().orElse(0);

        Initialise(title, date, max, min, new ArrayList[]{high, low});
    }

    private void Initialise(String title, List<String> date, double max, double min, ArrayList[] data) {
        super.setPreferredSize(new Dimension(700, 450));
        super.setBorder(BorderFactory.createLineBorder(Color.black));

        this.title = title;
        this.date = date;
        this.max = (long) Math.ceil(max) + 1;
        this.min = (long) Math.floor(min) - 1;
        this.data = data;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);


    }
}
