package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Resources.Images;
import PC_part.SACK_pc_client.Ring;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.Controls.WeekDaysPanel;

import java.awt.*;
import java.util.ArrayList;

public class Chart implements Activity {

    public Chart() {
    }

    private static final int textXOffset = 40;
    private static final int textYOffset = 35;
    private static final int markXOffset = WeekDaysPanel.dayWidth / 2 + 15;

    public void draw(Graphics2D g2) {
        g2.setColor(Design.lightBackgroundColor);
        g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);

        int timeMarkXPos = UICanvas.windowWidth - Images.backArrow.getWidth();

        g2.setFont(Design.font);
        g2.setStroke(new BasicStroke(1));
        for (int i = 9; i <= 19; i += 2) {
            int y = (i - 7) * PC_part.SACK_pc_client.Controls.Menu.itemHeight;
            g2.drawImage(Images.backArrow, timeMarkXPos, y, null);

            g2.setColor(Design.lightForegroundColor);
            g2.drawLine(PC_part.SACK_pc_client.Controls.Menu.itemWidth - 1, y + Images.backArrow.getHeight() / 2, timeMarkXPos, y + Images.backArrow.getHeight() / 2);

            g2.setColor(Design.darkFontColor);
            g2.drawString(String.valueOf(i), timeMarkXPos + textXOffset, y + textYOffset);
        }

        for (int i = 0; i < 7; i++) {

            ArrayList<Ring> rings = DataWrapper.getRings(i);

            if (Design.drawShadows)
                for (Ring r : rings) {

                    if (r.getHours() >= 9 && r.getHours() <= 19) {
                        int y = (int) ((r.getHours() - 7) * PC_part.SACK_pc_client.Controls.Menu.itemHeight +
                                (Images.backArrow.getHeight() - Images.markFilled.getHeight()) / 2) - Design.shadowRadius;
                        g2.drawImage(Images.markShadow,
                                PC_part.SACK_pc_client.Controls.Menu.itemWidth + i * WeekDaysPanel.dayWidth + markXOffset -
                                        Images.markFilled.getWidth() / 2 - Design.shadowRadius,
                                y, null);
                    }
                }

            for (Ring r : rings) {
                if (r.getHours() >= 9 && r.getHours() <= 19) {
                    int y = (int) ((r.getHours() - 7) * PC_part.SACK_pc_client.Controls.Menu.itemHeight + (Images.backArrow.getHeight() - Images.markFilled.getHeight()) / 2);
                    g2.drawImage(Images.markFilled, PC_part.SACK_pc_client.Controls.Menu.itemWidth + i * WeekDaysPanel.dayWidth + markXOffset - Images.markFilled.getWidth() / 2, y, null);
                }
            }

        }


    }

    @Override
    public void mouseClick(int x, int y) {

    }

}
