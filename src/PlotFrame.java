import javax.swing.*;
import java.awt.*;
import java.util.List;


class PlotFrame extends JFrame {

    private static final int FRAME_WIDTH = 1440;
    private static final int FRAME_HEIGHT = 900;

    PlotFrame(String title, List<LineChartPanel> panels) {

        int panelWidth = FRAME_WIDTH / 2;
        int panelHeight = FRAME_HEIGHT / 2;

        Dimension panelDimension = new Dimension(panelWidth, panelHeight);

        for (LineChartPanel panel : panels) {
            panel.setPreferredSize(panelDimension);
            this.add(panel);
        }

        GridLayout gridLayout = new GridLayout(2, 2);
        this.setLayout(gridLayout);

        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Dimension actualSize = getContentPane().getSize();

        int extraWidth = FRAME_WIDTH - actualSize.width;
        int extraHeight = FRAME_HEIGHT - actualSize.height;

        this.setSize(FRAME_WIDTH + extraWidth, FRAME_HEIGHT + extraHeight);

        this.setTitle(title);

        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
