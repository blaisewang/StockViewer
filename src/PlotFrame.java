import javax.swing.*;
import java.awt.*;
import java.util.List;

class PlotFrame extends JFrame {

    PlotFrame(int frameWidth, int frameHeight, List<LineChartPanel> panels) {

        int panelWidth = frameWidth / 2;
        int panelHeight = frameHeight / 2;

        Dimension panelDimension = new Dimension(panelWidth, panelHeight);

        for (LineChartPanel panel : panels) {
            panel.setPreferredSize(panelDimension);
            this.add(panel);
        }

        GridLayout gridLayout = new GridLayout(2, 2);
        this.setLayout(gridLayout);

        this.setSize(frameWidth, frameHeight);
        Dimension actualSize = getContentPane().getSize();

        int extraWidth = frameWidth - actualSize.width;
        int extraHeight = frameHeight - actualSize.height;

        this.setSize(frameWidth + extraWidth, frameHeight + extraHeight);

        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
