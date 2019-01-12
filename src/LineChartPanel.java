import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;


public class LineChartPanel extends JPanel implements MouseMotionListener {

    private String caption;

    private int recordSize;
    private List<String> records;

    private Range range;

    private int mouseX = 0;
    private int mouseY = 0;

    private int[] xPoints;
    private int[] highYPoints;
    private int[] lowYPoints;

    private List<Double> highScaled;
    private List<Double> lowScaled;

    private static final int AXIS_NUMBER = 5;
    private static final int AXIS_OFFSET = 5;
    private static final int CIRCLE_RADIUS = 6;
    private static final int CAPTION_OFFSET = 20;

    private static final int TOP_MARGIN = 90;
    private static final int LEFT_MARGIN = 65;
    private static final int RIGHT_MARGIN = 35;
    private static final int BOTTOM_MARGIN = 50;

    private static final String FONT_NAME = "Lucida Console";

    private static final Color BORDER_COLOR = new Color(222, 222, 222);
    private static final Color DARK_GREY = new Color(158, 158, 158);
    private static final Color LIGHT_GREY = new Color(158, 158, 158, 200);
    private static final Color DARK_BLUE = new Color(57, 119, 175);
    private static final Color DARK_ORANGE = new Color(239, 113, 54);

    LineChartPanel(String caption, List<Double> scaled, Range range, List<String> records) {

        super.addMouseMotionListener(this);
        initialise(caption, scaled, null, range, records);

    }

    LineChartPanel(String caption,
                   List<Double> highScaled,
                   List<Double> lowScaled,
                   Range range,
                   List<String> records) {

        super.addMouseMotionListener(this);
        initialise(caption, highScaled, lowScaled, range, records);

    }

    private void initialise(String caption,
                            List<Double> highScaled,
                            List<Double> lowScaled,
                            Range range,
                            List<String> records) {

        super.setBackground(Color.white);
        super.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        this.caption = caption;
        this.highScaled = highScaled;
        this.lowScaled = lowScaled;
        this.range = range;
        this.records = records;
        this.recordSize = records.size();

        this.xPoints = new int[recordSize];
        this.highYPoints = new int[recordSize];
        this.lowYPoints = new int[recordSize];

    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int lineChartWidth = getWidth() - LEFT_MARGIN - RIGHT_MARGIN;
        int lineChartHeight = getHeight() - TOP_MARGIN - BOTTOM_MARGIN;

        initialise(g2d);

        drawCaption(g2d);

        int axisStep = lineChartHeight / (AXIS_NUMBER - 1);
        int axisX1 = LEFT_MARGIN - AXIS_OFFSET;
        int axisX2 = getWidth() - RIGHT_MARGIN + AXIS_OFFSET;
        int axisY = getHeight() - BOTTOM_MARGIN;

        drawAxis(g2d, axisStep, axisX1, axisX2, axisY);

        drawYAxisLabels(g2d, axisStep, axisX1, axisY);

        if (recordSize == 0) {
            return;
        }

        double xStep = lineChartWidth / ((double) recordSize + 1);

        for (int i = 1; i < recordSize + 1; i++) {
            xPoints[i - 1] = (int) (LEFT_MARGIN + i * xStep);
            highYPoints[i - 1] = (int) (highScaled.get(i - 1) * lineChartHeight) + TOP_MARGIN;
        }

        drawPolyLine(g2d, xPoints, highYPoints, DARK_BLUE);

        if (lowScaled != null) {

            for (int i = 0; i < recordSize; i++) {
                lowYPoints[i] = (int) (lowScaled.get(i) * lineChartHeight) + TOP_MARGIN;
            }
            drawPolyLine(g2d, xPoints, lowYPoints, DARK_ORANGE);
            drawLegend(g2d);
        }

        int[] steps = new int[]{0, recordSize / 4, recordSize / 2, (int) (recordSize * 0.75), recordSize - 1};

        for (int step : steps) {
            int x = xPoints[step];

            String[] labelData = records.get(step).split(" {2}");
            String label = labelData[labelData.length - 1];

            drawXAxisLabels(g2d, label, x, axisY);
        }

        int index = (int) Math.round((mouseX - LEFT_MARGIN) / xStep) - 1;

        if (index >= 0 && index < recordSize && mouseY > TOP_MARGIN && mouseY < axisY) {

            int x = xPoints[index];
            int y = highYPoints[index];

            drawCircle(g2d, x, y, DARK_BLUE);

            if (lowScaled != null) {
                y = lowYPoints[index];
                drawCircle(g2d, x, y, DARK_ORANGE);
            }

            drawRecordRectangle(g2d, records.get(index), x);

        }
    }

    private void initialise(Graphics2D g2d) {

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY));

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON));

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC));

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE));

        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

    }

    private void drawCaption(Graphics2D g2d) {

        g2d.setColor(Color.black);
        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 16));

        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D captionBounds = fontMetrics.getStringBounds(caption, g2d);

        int yFontOffset = fontMetrics.getAscent();
        int x = (int) (getWidth() - captionBounds.getWidth()) / 2;
        int y = yFontOffset + CAPTION_OFFSET;
        g2d.drawString(caption, x, y);

    }

    private void drawAxis(Graphics2D g2d, int axisStep, int axisX1, int axisX2, int axisY) {

        g2d.setColor(DARK_GREY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(axisX1, axisY, axisX2, axisY);

        g2d.setColor(LIGHT_GREY);
        g2d.setStroke(new BasicStroke(1));
        for (int i = 1; i < AXIS_NUMBER; i++) {
            int y = axisY - axisStep * i;
            g2d.drawLine(axisX1, y, axisX2, y);
        }

    }

    private void drawPolyLine(Graphics2D g2d, int[] xPoints, int[] yPoints, Color color) {

        if (xPoints.length == 1) {

            drawCircle(g2d, xPoints[0], yPoints[0], color);

        } else {

            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(color);
            g2d.drawPolyline(xPoints, yPoints, recordSize);

        }

    }

    private void drawLegend(Graphics2D g2d) {

        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D legendBounds = fontMetrics.getStringBounds("High", g2d);

        int rectWidth = (int) legendBounds.getWidth() + CIRCLE_RADIUS * 7;
        int rectHeight = CIRCLE_RADIUS * 7;

        int rectX = getWidth() - RIGHT_MARGIN - rectWidth;
        int rectY = CAPTION_OFFSET / 2 - 3;

        g2d.setColor(LIGHT_GREY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(rectX, rectY, rectWidth, rectHeight);

        int step = 3 * CIRCLE_RADIUS;
        int circleX = (int) (rectX + CIRCLE_RADIUS * 2.5);
        int circleY = rectY + CIRCLE_RADIUS * 2;

        drawCircle(g2d, circleX, circleY, DARK_BLUE);
        drawCircle(g2d, circleX, circleY + step, DARK_ORANGE);

        int textX = circleX + CIRCLE_RADIUS * 3;
        int textY = circleY + fontMetrics.getAscent() - (int) (1.5 * CIRCLE_RADIUS);

        g2d.setColor(Color.black);
        g2d.drawString("High", textX, textY);
        g2d.drawString("Low", textX, textY + step + 1);

    }

    private void drawXAxisLabels(Graphics2D g2d, String label, int x, int y) {

        g2d.setColor(DARK_GREY);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 10));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D labelBounds = fontMetrics.getStringBounds(label, g2d);

        int yOffset = 5;
        g2d.drawLine(x, y, x, y + yOffset);

        x -= (int) (labelBounds.getWidth() / 2);
        y += fontMetrics.getAscent() + yOffset;
        g2d.drawString(label, x, y);

    }

    private void drawYAxisLabels(Graphics2D g2d, int yStep, int x, int y) {

        g2d.setColor(DARK_GREY);
        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 10));

        FontMetrics fontMetrics = g2d.getFontMetrics();

        double rangeStep = range.getRange() / (AXIS_NUMBER - 1);

        for (int i = 0; i < AXIS_NUMBER; i++) {

            double value = range.getMin() + i * rangeStep;
            String label = Util.toFormattedNumberString(value, range.getMin());
            Rectangle2D labelBounds = fontMetrics.getStringBounds(label, g2d);

            int tx = x - (int) labelBounds.getWidth() - AXIS_OFFSET;
            int ty = y + fontMetrics.getAscent() / 2 - i * yStep - 1;
            g2d.drawString(label, tx, ty);

        }

    }

    private void drawCircle(Graphics2D g2d, int x, int y, Color color) {

        x -= CIRCLE_RADIUS;
        y -= CIRCLE_RADIUS;
        int diameter = CIRCLE_RADIUS * 2;

        g2d.setColor(color);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, diameter, diameter);
        g2d.fill(ellipse);

    }

    private void drawRecordRectangle(Graphics2D g2d, String record, int x) {

        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
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

        g2d.setColor(LIGHT_GREY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(tx, ty, recordBoundWidth, recordBoundHeight);

        g2d.setColor(Color.black);
        tx += xOffset / 2;
        ty += +yOffset / 2 + fontMetrics.getAscent();
        g2d.drawString(record, tx, ty);

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
