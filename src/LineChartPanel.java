import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * LineChartPanel class
 * draw line chart by given data.
 * Code traces the mouse motion to display detail information.
 *
 * @author Xudong Wang (xwang199@sheffield.ac.uk)
 * @version 1.0 12 January 2019
 */
class LineChartPanel extends JPanel implements MouseMotionListener {

    // member variables
    private String caption;

    private int dataSize;
    private List<String> data;

    private Range range;

    private int mouseX = 0;
    private int mouseY = 0;

    private int[] xPoints;
    private int[] highYPoints;
    private int[] lowYPoints;

    private List<Double> highScaled;
    private List<Double> lowScaled;

    // constant variables
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

    /**
     * Class constructor for one scaled data given
     * suitable for single data line chart
     *
     * @param caption      of the line chart
     * @param scaled       data given
     * @param range        line chart y-axis range
     * @param detailedData detailed data
     */
    LineChartPanel(String caption, List<Double> scaled, Range range, List<String> detailedData) {

        // bind mouse motion listener
        super.addMouseMotionListener(this);
        initialise(caption, scaled, null, range, detailedData);

    }

    /**
     * Class constructor for two scaled data given
     * suitable for high-low line chart
     *
     * @param caption      of the line chart
     * @param highScaled   data given
     * @param lowScaled    data given
     * @param range        line chart y-axis range
     * @param detailedData detailed data
     */
    LineChartPanel(String caption,
                   List<Double> highScaled,
                   List<Double> lowScaled,
                   Range range,
                   List<String> detailedData) {

        // bind mouse motion listener
        super.addMouseMotionListener(this);
        initialise(caption, highScaled, lowScaled, range, detailedData);

    }

    /**
     * Variables bindings, common method
     *
     * @param caption      of the line chart
     * @param highScaled   data given
     * @param lowScaled    data given
     * @param range        line chart y-axis range
     * @param detailedData detailed data to display when the pointer on the designated position
     * @see LineChartPanel#LineChartPanel(String, List, Range, List)
     * @see LineChartPanel#LineChartPanel(String, List, List, Range, List)
     */
    private void initialise(String caption,
                            List<Double> highScaled,
                            List<Double> lowScaled,
                            Range range,
                            List<String> detailedData) {

        // set white background
        super.setBackground(Color.white);
        // set light grey border color
        super.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        this.caption = caption;
        this.highScaled = highScaled;
        this.lowScaled = lowScaled;
        this.range = range;
        this.data = detailedData;
        this.dataSize = detailedData.size();

        // initialise arrays
        this.xPoints = new int[dataSize];
        this.highYPoints = new int[dataSize];
        this.lowYPoints = new int[dataSize];

    }

    /**
     * Delegate's paint method
     *
     * @param g Graphics object
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // calculate line chart width and height
        int lineChartWidth = getWidth() - LEFT_MARGIN - RIGHT_MARGIN;
        int lineChartHeight = getHeight() - TOP_MARGIN - BOTTOM_MARGIN;

        // some render settings
        initialise(g2d);

        // draw caption
        drawCaption(g2d);

        // pixel interval of each step to draw x Axis
        int xAxisInterval = lineChartHeight / (AXIS_NUMBER - 1);
        int axisX1 = LEFT_MARGIN - AXIS_OFFSET;
        int axisX2 = getWidth() - RIGHT_MARGIN + AXIS_OFFSET;
        int axisY = getHeight() - BOTTOM_MARGIN;

        // draw axis
        drawXAxis(g2d, xAxisInterval, axisX1, axisX2, axisY);

        // draw labels on y-axis
        drawYAxisLabels(g2d, xAxisInterval, axisX1, axisY);

        // return for zero data
        if (dataSize == 0) {
            return;
        }

        // data point interval pixels
        double xDataStep = lineChartWidth / ((double) dataSize + 1);

        for (int i = 1; i < dataSize + 1; i++) {
            // calculate x coordinates based on data point interval
            xPoints[i - 1] = (int) (LEFT_MARGIN + i * xDataStep);
            // calculate y coordinates based on scaled data
            highYPoints[i - 1] = (int) (highScaled.get(i - 1) * lineChartHeight) + TOP_MARGIN;
        }

        // draw poly line based on x, y coordinates
        drawPolyLine(g2d, xPoints, highYPoints, DARK_BLUE);

        if (lowScaled != null) {
            // two scaled data given draw another ploy line

            for (int i = 0; i < dataSize; i++) {
                // calculate y coordinates based on scaled data
                lowYPoints[i] = (int) (lowScaled.get(i) * lineChartHeight) + TOP_MARGIN;
            }
            // draw poly line based on x, y coordinates
            drawPolyLine(g2d, xPoints, lowYPoints, DARK_ORANGE);
            // draw legend to distinguish two lines
            drawLegend(g2d);
        }

        // indices of 5 dates to be displayed on x-axis
        int[] steps = new int[]{0, dataSize / 4, dataSize / 2, (int) (dataSize * 0.75), dataSize - 1};

        for (int step : steps) {
            int x = xPoints[step];

            // get date from detailed data via regex ' {2}'
            String[] labelData = data.get(step).split(" {2}");
            String label = labelData[labelData.length - 1];

            // draw labels on x-axis
            drawXAxisLabels(g2d, label, x, axisY);
        }

        // calculate the nearest data point based on current mouse position
        int index = (int) Math.round((mouseX - LEFT_MARGIN) / xDataStep) - 1;

        if (index >= 0 && index < dataSize && mouseY > TOP_MARGIN && mouseY < axisY) {
            // valid data index

            int x = xPoints[index];
            int y = highYPoints[index];

            // draw small circle to indicate current data point
            drawCircle(g2d, x, y, DARK_BLUE);

            if (lowScaled != null) {
                // draw another circle to indicate current data point
                y = lowYPoints[index];
                drawCircle(g2d, x, y, DARK_ORANGE);
            }

            // draw detailed information on top of line chart graph
            drawDetailedDataRectangle(g2d, data.get(index), x);

        }
    }

    /**
     * Rendering setting to make line chart smooth
     *
     * @param g2d Graphics2D object
     */
    private void initialise(Graphics2D g2d) {

        // antialiasing
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        // alpha interpolation
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));

        // color rendering
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY));

        // fractional text metrics
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON));

        // image interpolation
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC));

        // Rendering
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));

        // stroke normalization control
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE));

        // text antialiasing
        g2d.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

    }

    /**
     * Draw caption of line chart
     *
     * @param g2d Graphics2d object
     */
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

    /**
     * Draw X-axis
     *
     * @param g2d      Graphics2d object
     * @param interval pixel interval of each step to draw x Axis
     * @param axisX1   x1 of line
     * @param axisX2   x2 of line
     * @param axisY    y of line
     */
    private void drawXAxis(Graphics2D g2d, int interval, int axisX1, int axisX2, int axisY) {

        g2d.setColor(DARK_GREY);
        g2d.setStroke(new BasicStroke(2));
        // draw the bottom x-axis
        g2d.drawLine(axisX1, axisY, axisX2, axisY);

        g2d.setColor(LIGHT_GREY);
        g2d.setStroke(new BasicStroke(1));
        for (int i = 1; i < AXIS_NUMBER; i++) {
            // draw the other x-axis by subtracting initial y position from the pixel interval
            int y = axisY - interval * i;
            g2d.drawLine(axisX1, y, axisX2, y);
        }

    }

    /**
     * Draw poly line by given x, y coordinates
     *
     * @param g2d     Graphics2d object
     * @param xPoints x coordinates
     * @param yPoints y coordinates
     * @param color   to draw
     */
    private void drawPolyLine(Graphics2D g2d, int[] xPoints, int[] yPoints, Color color) {

        if (xPoints.length == 1) {
            // draw a small data point instead for the single data
            drawCircle(g2d, xPoints[0], yPoints[0], color);

        } else {
            // draw poly line with given coordinates
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(color);
            g2d.drawPolyline(xPoints, yPoints, dataSize);

        }

    }

    /**
     * Draw legend for high-low line chart
     *
     * @param g2d Graphics2D g2d
     */
    private void drawLegend(Graphics2D g2d) {

        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D legendBounds = fontMetrics.getStringBounds("High", g2d);

        // border width and height
        int rectWidth = (int) legendBounds.getWidth() + CIRCLE_RADIUS * 7;
        int rectHeight = CIRCLE_RADIUS * 7;

        // top-left position of the border
        int rectX = getWidth() - RIGHT_MARGIN - rectWidth;
        int rectY = CAPTION_OFFSET / 2 - 3;

        g2d.setColor(LIGHT_GREY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(rectX, rectY, rectWidth, rectHeight);

        // space between circles
        int step = 3 * CIRCLE_RADIUS;
        // top circle position
        int circleX = (int) (rectX + CIRCLE_RADIUS * 2.5);
        int circleY = rectY + CIRCLE_RADIUS * 2;

        drawCircle(g2d, circleX, circleY, DARK_BLUE);
        drawCircle(g2d, circleX, circleY + step, DARK_ORANGE);

        // top text position
        int textX = circleX + CIRCLE_RADIUS * 3;
        int textY = circleY + fontMetrics.getAscent() - (int) (1.5 * CIRCLE_RADIUS);

        g2d.setColor(Color.black);
        g2d.drawString("High", textX, textY);
        g2d.drawString("Low", textX, textY + step + 1);

    }

    /**
     * Draw date label on x-axis
     *
     * @param g2d   Graphics2D g2d object
     * @param label to draw
     * @param x     coordinate of data point
     * @param y     coordinate of data point
     */
    private void drawXAxisLabels(Graphics2D g2d, String label, int x, int y) {

        g2d.setColor(DARK_GREY);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 10));

        // label bound to centre the label
        FontMetrics fontMetrics = g2d.getFontMetrics();
        Rectangle2D labelBounds = fontMetrics.getStringBounds(label, g2d);

        // small line from x-axis to label
        int yOffset = 5;
        g2d.drawLine(x, y, x, y + yOffset);

        // calculate label position by data position plus label width and font ascent
        x -= (int) (labelBounds.getWidth() / 2);
        y += fontMetrics.getAscent() + yOffset;
        g2d.drawString(label, x, y);

    }

    /**
     * Draw labels on y-axis
     *
     * @param g2d      Graphics2D g2d object
     * @param interval pixel interval of each label
     * @param x        coordinate of x-axis
     * @param y        coordinate of x-axis
     */
    private void drawYAxisLabels(Graphics2D g2d, int interval, int x, int y) {

        g2d.setColor(DARK_GREY);
        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 10));

        FontMetrics fontMetrics = g2d.getFontMetrics();

        // the interval of each label
        double rangeStep = range.getRange() / (AXIS_NUMBER - 1);

        for (int i = 0; i < AXIS_NUMBER; i++) {

            double value = range.getMin() + i * rangeStep;
            // format label
            String label = Util.toFormattedNumberString(value, range.getMin());
            // label bound to centre the label
            Rectangle2D labelBounds = fontMetrics.getStringBounds(label, g2d);

            // calculate label position by x-axis position, axis offset, and font ascent
            int tx = x - (int) labelBounds.getWidth() - AXIS_OFFSET;
            int ty = y + fontMetrics.getAscent() / 2 - i * interval - 1;
            g2d.drawString(label, tx, ty);

        }

    }

    /**
     * Draw small circle to indicate data point
     *
     * @param g2d   Graphics2D g2d object
     * @param x     coordinate of data point
     * @param y     coordinate of data point
     * @param color to draw
     */
    private void drawCircle(Graphics2D g2d, int x, int y, Color color) {

        // x, y coordinate need to be subtracting radius to centre the circle
        x -= CIRCLE_RADIUS;
        y -= CIRCLE_RADIUS;
        int diameter = CIRCLE_RADIUS * 2;

        g2d.setColor(color);
        Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, diameter, diameter);
        // fill the circle
        g2d.fill(ellipse);

    }

    /**
     * Draw detailed information
     *
     * @param g2d          Graphics2D g2d object
     * @param detailedData information to draw
     * @param x            coordinate of data point
     */
    private void drawDetailedDataRectangle(Graphics2D g2d, String detailedData, int x) {

        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, 13));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        // information bound to centre the information
        Rectangle2D detailedDataBounds = fontMetrics.getStringBounds(detailedData, g2d);

        // margins of information to its border
        int xOffset = 10;
        int yOffset = 5;

        // border width and height
        int detailedDataBoundWidth = (int) detailedDataBounds.getWidth() + xOffset;
        int detailedDataBoundHeight = (int) detailedDataBounds.getHeight() + yOffset;

        // minimum margin to edge of the panel
        int minMargin = 10;

        // margin to left edge
        int leftMargin = x - detailedDataBoundWidth / 2;

        int tx = leftMargin;

        // y coordinate
        int ty = TOP_MARGIN - detailedDataBoundHeight - minMargin;

        if (leftMargin < minMargin) {
            // fix position when minimum margin reached
            tx = minMargin;
        }

        // margin to right edge
        int rightMargin = getWidth() - tx - detailedDataBoundWidth;

        if (rightMargin < minMargin) {
            // fix position when minimum margin reached
            tx = getWidth() - detailedDataBoundWidth - minMargin;
        }

        // draw border
        g2d.setColor(LIGHT_GREY);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(tx, ty, detailedDataBoundWidth, detailedDataBoundHeight);

        // draw text
        g2d.setColor(Color.black);
        tx += xOffset / 2;
        ty += +yOffset / 2 + fontMetrics.getAscent();
        g2d.drawString(detailedData, tx, ty);

    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Method for mouse moved
     *
     * @param e MouseEvent object
     */
    @Override
    public void mouseMoved(MouseEvent e) {

        // get x, y coordinate
        mouseX = e.getX();
        mouseY = e.getY();

        // repaint UI
        repaint();

    }

}
