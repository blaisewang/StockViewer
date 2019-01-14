import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Plotting JFrame
 */
class PlotFrame extends JFrame {

    private static final int FRAME_WIDTH = 1440;
    private static final int FRAME_HEIGHT = 900;

    PlotFrame(String title, List<LineChartPanel> panels) {

        // the width of panel is half width of the window
        // so does the height of panel
        int panelWidth = FRAME_WIDTH / 2;
        int panelHeight = FRAME_HEIGHT / 2;

        Dimension panelDimension = new Dimension(panelWidth, panelHeight);

        for (LineChartPanel panel : panels) {
            // set panel width and height
            panel.setPreferredSize(panelDimension);
            this.add(panel);
        }

        // four panels are arranged in a 2 * 2 layout via GridLayout
        GridLayout gridLayout = new GridLayout(2, 2);
        this.setLayout(gridLayout);

        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Dimension actualSize = getContentPane().getSize();

        // get size of title bar and border
        int extraWidth = FRAME_WIDTH - actualSize.width;
        int extraHeight = FRAME_HEIGHT - actualSize.height;

        // set the window size with extra size
        // to make sure the window's real display area is FRAME_WIDTH * FRAME_HEIGHT
        this.setSize(FRAME_WIDTH + extraWidth, FRAME_HEIGHT + extraHeight);

        this.setTitle(title);

        // set the window to be sized to fit the preferred size
        this.pack();
        // set the window is not resizable
        this.setResizable(false);
        // centre the window
        this.setLocationRelativeTo(null);
        // set the window is disposed when a close operation is performed
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
