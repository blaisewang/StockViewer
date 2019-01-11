import javax.swing.*;
import java.awt.*;

class PlotFrame extends JFrame {

    PlotFrame(int frameWidth,
              int frameHeight,
              LineChartPanel openPanel,
              LineChartPanel closePanel,
              LineChartPanel volumePanel,
              LineChartPanel highLowPanel) {

        int panelWidth = frameWidth / 2;
        int panelHeight = frameHeight / 2;

        GridLayout gridLayout = new GridLayout(2, 2);
        setLayout(gridLayout);

        openPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        closePanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        volumePanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        highLowPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        add(openPanel);
        add(closePanel);
        add(volumePanel);
        add(highLowPanel);

        setSize(frameWidth, frameHeight);
        Dimension actualSize = getContentPane().getSize();

        int extraWidth = frameWidth - actualSize.width;
        int extraHeight = frameHeight - actualSize.height;

        setSize(frameWidth + extraWidth, frameHeight + extraHeight);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

}
