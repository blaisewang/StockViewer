import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LineChartPanel extends JPanel implements MouseMotionListener {

    final static int WIDTH = 600;
    final static int HEIGHT = 400;

    private String title;
    private List<String> date;
    private ArrayList[] data;
    private double max;
    private double min;

    private int x = 0;
    private int y = 0;

    private int[] xPoints;
    private int[] yPoints;

    private HashMap<String, String> month = new HashMap<>();


    LineChartPanel(String title, List<String> date, ArrayList<Double> data) {
        addMouseMotionListener(this);

        double max = data.stream().mapToDouble(v -> v).max().orElse(0);
        double min = data.stream().mapToDouble(v -> v).min().orElse(0);

        Initialise(title, date, max, min, new ArrayList[]{data});
    }

    LineChartPanel(String title, List<String> date, ArrayList<Double> high, ArrayList<Double> low) {
        addMouseMotionListener(this);

        double max = high.stream().mapToDouble(v -> v).max().orElse(0);
        double min = low.stream().mapToDouble(v -> v).min().orElse(0);

        Initialise(title, date, max, min, new ArrayList[]{high, low});
    }

    private void Initialise(String title, List<String> date, double max, double min, ArrayList[] data) {
        super.setPreferredSize(new Dimension(700, 450));
        super.setBackground(Color.white);
        super.setBorder(BorderFactory.createLineBorder(Color.black));

        this.title = title;
        this.date = date;
        this.max = Math.ceil(max) + 1;
        this.min = Math.floor(min) - 1;
        this.data = data;

        month.put("01", "Jan");
        month.put("02", "Feb");
        month.put("03", "Mar");
        month.put("04", "Apr");
        month.put("05", "May");
        month.put("06", "Jun");
        month.put("07", "Jul");
        month.put("08", "Aug");
        month.put("09", "Sep");
        month.put("10", "Oct");
        month.put("11", "Nov");
        month.put("12", "Dec");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setFont(new Font("Arial Black", Font.PLAIN, 14));
        g2d.drawString(title, 300, 24);

        Color axisColor = new Color(158, 158, 158);
        g2d.setColor(axisColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(50, 400, 650, 400);

        xPoints = new int[data[0].size()];
        yPoints = new int[data[0].size()];

        int step = 600 / data[0].size();
        double range = max - min;

        for (int i = 0; i < xPoints.length; i++) {
            xPoints[i] = 50 + i * step;
            yPoints[i] = 50 + (int) ((max - (double) data[0].get(i)) / range * 350);
        }

        g2d.drawPolyline(xPoints, yPoints, xPoints.length);

        int dateStep = data[0].size() / 5;

        for (int i = dateStep; i < data[0].size(); i += dateStep) {
            int xl = xPoints[i];
            int yl = 420;

            String[] day = date.get(i).split("/");

            String d = day[1] + " " + month.get(day[0]) + " " + day[2];

            FontMetrics fm = g2d.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(d, g2d);

            g.drawLine(xl, 400, xl, 405);
            g.drawString(d, xl - (int) (r.getWidth() / 2), yl);
        }

        int index = (int) Math.round(((double) x - 50) / step);

        if (index >= 0 && index < data[0].size() && y > 50 && y < 400) {
            int xc = 50 + index * step;
            int yc = 50 + (int) ((max - (double) data[0].get(index)) / range * 350);

            g2d.drawOval(xc - 2, yc - 2, 4, 4);

            String[] day = date.get(index).split("/");


            String hint = data[0].get(index) + " USD  " + day[1] + " " + month.get(day[0]) + " " + day[2];

            int rx = xc - 100, ry = 35;

            if (rx < 10) {
                rx = 10;
            }

            if (rx > 490) {
                rx = 490;
            }

            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(Color.black);

            FontMetrics fm = g2d.getFontMetrics();
            Rectangle2D r = fm.getStringBounds(hint, g2d);
            int y = fm.getAscent();
            g2d.drawRect(rx - 5, ry, (int) r.getWidth() + 10, (int) r.getHeight() + 2);
            g.drawString(hint, rx, ry + y);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        repaint();
    }
}
