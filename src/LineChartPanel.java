import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class LineChartPanel extends JPanel implements MouseMotionListener {

    private String title;

    private int recordSize;
    private ArrayList<String> records;

    private double max;
    private double min;

    private int mouseX = 0;
    private int mouseY = 0;

    private int[] xPoints;
    private int[] highYPoints;
    private int[] lowYPoints;

    private double[] highScaled;
    private double[] lowScaled;

    private static final int yAxisNumber = 5;
    private static final int circleRadius = 6;

    private static final int TOP_MARGIN = 90;
    private static final int BOTTOM_MARGIN = 50;
    private static final int LEFT_MARGIN = 60;
    private static final int RIGHT_MARGIN = 40;

    private Color borderColor = new Color(222, 222, 222);
    private Color darkGrey = new Color(158, 158, 158);
    private Color lightGrey = new Color(158, 158, 158, 200);
    private Color darkBlue = new Color(57, 119, 175);
    private Color darkOrange = new Color(239, 113, 54);

    LineChartPanel(String title, double[] scaled, double[] range, ArrayList<String> records) {

        super.addMouseMotionListener(this);
        initialise(title, scaled, null, range, records);

    }

    LineChartPanel(String title, double[] highScaled, double[] lowScaled, double[] range, ArrayList<String> records) {

        super.addMouseMotionListener(this);
        initialise(title, highScaled, lowScaled, range, records);

    }

    private void initialise(String title, double[] highScaled, double[] lowScaled, double[] range, ArrayList<String> records) {

        super.setBackground(Color.white);
        super.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        this.title = title;
        this.highScaled = highScaled;
        this.lowScaled = lowScaled;
        this.records = records;
        this.max = range[1];
        this.min = range[0];
        this.recordSize = records.size();

        this.xPoints = new int[recordSize];
        this.highYPoints = new int[recordSize];
        this.lowYPoints = new int[recordSize];

    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON)
        );
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        );

        int chartWidth = getWidth() - LEFT_MARGIN - RIGHT_MARGIN;
        int chartHeight = getHeight() - TOP_MARGIN - BOTTOM_MARGIN;

        drawTitle(g2d);

        int offset = 5;
        int axisStep = chartHeight / (yAxisNumber - 1);
        int axisX1 = LEFT_MARGIN - offset;
        int axisX2 = getWidth() - RIGHT_MARGIN + offset;
        int axisY = getHeight() - BOTTOM_MARGIN;

        drawAxis(g2d, axisStep, axisX1, axisX2, axisY);

        drawYLabels(g2d, axisStep, axisX1, axisY);

        if (recordSize == 0) {
            return;
        }

        double xStep = chartWidth / ((double) recordSize + 1);

        for (int i = 1; i < recordSize + 1; i++) {
            xPoints[i - 1] = (int) (LEFT_MARGIN + i * xStep);
            highYPoints[i - 1] = (int) (highScaled[i - 1] * chartHeight) + TOP_MARGIN;
        }

        drawPolyLine(g2d, xPoints, highYPoints, darkBlue);

        if (lowScaled != null) {
            for (int i = 0; i < recordSize; i++) {
                lowYPoints[i] = (int) (lowScaled[i] * chartHeight) + TOP_MARGIN;
            }
            drawPolyLine(g2d, xPoints, lowYPoints, darkOrange);
        }

        int[] steps = new int[]{0, recordSize / 4, recordSize / 2, recordSize / 4 * 3, recordSize - 1};

        for (int step : steps) {
            int x = xPoints[step];

            String[] labelData = records.get(step).split("USD {2}");
            String label = labelData[labelData.length - 1];

            drawXLabels(g2d, label, x, axisY);
        }

        int index = (int) Math.round((mouseX - LEFT_MARGIN) / xStep) - 1;

        if (index >= 0 && index < recordSize && mouseY > TOP_MARGIN && mouseY < axisY) {

            int x = xPoints[index];
            int y = highYPoints[index];

            drawCircle(g2d, circleRadius, x, y, darkBlue);

            if (lowScaled != null) {
                y = lowYPoints[index];
                drawCircle(g2d, circleRadius, x, y, darkOrange);
            }

            drawRecordRectangle(g2d, records.get(index), x);

        }
    }


    private void drawTitle(Graphics2D g2d) {

        g2d.setColor(Color.black);
        g2d.setFont(new Font("Lucida Console", Font.PLAIN, 16));

        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D titleBounds = fontMetrics.getStringBounds(title, g2d);

        int yFontOffset = fontMetrics.getAscent();
        g2d.drawString(title, (int) (getWidth() - titleBounds.getWidth()) / 2, 20 + yFontOffset);

    }

    private void drawAxis(Graphics2D g2d, int axisStep, int axisX1, int axisX2, int axisY) {

        g2d.setColor(darkGrey);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(axisX1, axisY, axisX2, axisY);

        g2d.setColor(lightGrey);
        g2d.setStroke(new BasicStroke(1));
        for (int i = 1; i < yAxisNumber; i++) {
            g2d.drawLine(axisX1, axisY - axisStep * i, axisX2, axisY - axisStep * i);
        }

    }

    private void drawPolyLine(Graphics2D g2d, int[] xPoints, int[] yPoints, Color color) {

        if (xPoints.length == 1) {

            drawCircle(g2d, circleRadius, xPoints[0], yPoints[0], color);

        } else {

            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(color);
            g2d.drawPolyline(xPoints, yPoints, recordSize);

        }

    }

    private void drawXLabels(Graphics2D g2d, String label, int x, int y) {

        g2d.setColor(darkGrey);
        g2d.setFont(new Font("Lucida Console", Font.PLAIN, 10));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D labelBounds = fontMetrics.getStringBounds(label, g2d);

        int yOffset = 4;
        g2d.drawLine(x, y, x, y + yOffset);
        g2d.drawString(label, x - (int) (labelBounds.getWidth() / 2), y + fontMetrics.getAscent() + yOffset);

    }

    private void drawYLabels(Graphics2D g2d, int yStep, int x, int y) {

        g2d.setColor(darkGrey);
        g2d.setFont(new Font("Lucida Console", Font.BOLD, 10));

        if (min > 10000) {
            g2d.setFont(new Font("Lucida Console", Font.BOLD, 7));
        }

        FontMetrics fontMetrics = g2d.getFontMetrics();

        double range = max - min;
        double rangeStep = range / (yAxisNumber - 1);

        for (int i = 0; i < yAxisNumber; i++) {

            String label = Util.toFormattedNumberString(min + i * rangeStep);
            Rectangle2D labelBounds = fontMetrics.getStringBounds(label, g2d);

            int tx = x - (int) labelBounds.getWidth() - 5;
            int ty = y + fontMetrics.getAscent() / 2 - i * yStep - 1;
            g2d.drawString(label, tx, ty);

        }

    }

    private void drawCircle(Graphics2D g2d, int radius, int x, int y, Color color) {

        g2d.setColor(color);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        g2d.fill(ellipse);

    }

    private void drawRecordRectangle(Graphics2D g2d, String record, int x) {

        g2d.setFont(new Font("Lucida Console", Font.PLAIN, 13));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D recordBounds = fontMetrics.getStringBounds(record, g2d);

        int xOffset = 10;
        int yOffset = 5;

        int recordBoundWidth = (int) recordBounds.getWidth() + xOffset;
        int recordBoundHeight = (int) recordBounds.getHeight() + yOffset;

        int minMargin = 10;
        int leftMargin = x - recordBoundWidth / 2;

        int tx = leftMargin;
        int ty = TOP_MARGIN - recordBoundHeight - minMargin;

        if (leftMargin < minMargin) {
            tx = minMargin;
        }

        int rightMargin = getWidth() - tx - recordBoundWidth;

        if (rightMargin < minMargin) {
            tx = getWidth() - recordBoundWidth - minMargin;
        }

        g2d.setColor(lightGrey);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(tx, ty, recordBoundWidth, recordBoundHeight);

        g2d.setColor(Color.black);
        g2d.drawString(record, tx + xOffset / 2, ty + yOffset / 2 + fontMetrics.getAscent());

    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }
}
