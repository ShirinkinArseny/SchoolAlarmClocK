package SACK_pc_client.Controls;

import SACK_pc_client.DataWrapper;
import SACK_pc_client.Resources.Images;
import SACK_pc_client.Ring;
import SACK_pc_client.UICanvas;
import SACK_pc_client.WeekDaysPanel;

import java.awt.*;
import java.util.ArrayList;

public class Chart implements Activity{

    public Chart() {
    }

    public void update() {

    }

    private static final int textXOffset=40;
    private static final int textYOffset=35;
    private static final int markXOffset= WeekDaysPanel.dayWidth/2+15;

    public void draw(Graphics2D g2) {
        g2.setColor(UICanvas.lightBackgroundColor);
        g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);

        int timeMarkXPos=UICanvas.windowWidth- Images.backArrow.getWidth();

        g2.setFont(UICanvas.font);
        g2.setStroke(new BasicStroke(1));
        for (int i=9; i<=19; i+=2) {
            int y=(i - 7) * SACK_pc_client.Controls.Menu.itemHeight;
            g2.drawImage(Images.backArrow, timeMarkXPos, y, null);

            g2.setColor(UICanvas.lightForegroundColor);
            g2.drawLine(SACK_pc_client.Controls.Menu.itemWidth-1, y+Images.backArrow.getHeight()/2, timeMarkXPos, y+Images.backArrow.getHeight()/2);

            g2.setColor(UICanvas.darkFontColor);
            g2.drawString(String.valueOf(i), timeMarkXPos + textXOffset, y + textYOffset);
        }

        for (int i=0; i<7; i++) {

            ArrayList<Ring> rigns= DataWrapper.getRings(i);

            for (Ring r: rigns) {

                if (r.getHours()>=9 && r.getHours()<=19) {
                    int y = (int) ((r.getHours() - 7) * SACK_pc_client.Controls.Menu.itemHeight + (Images.backArrow.getHeight() - Images.markBoards.getHeight()) / 2);
                    g2.drawImage(Images.markBoards, SACK_pc_client.Controls.Menu.itemWidth + i * WeekDaysPanel.dayWidth + markXOffset - Images.markBoards.getWidth() / 2, y, null);
                }
            }

            for (Ring r: rigns) {
                if (r.getHours()>=9 && r.getHours()<=19) {
                int y= (int) ((r.getHours() - 7) * SACK_pc_client.Controls.Menu.itemHeight+(Images.backArrow.getHeight()-Images.markFilled.getHeight())/2);
                g2.drawImage(Images.markFilled, SACK_pc_client.Controls.Menu.itemWidth+i*WeekDaysPanel.dayWidth+markXOffset-Images.markFilled.getWidth()/2, y, null);
                }
            }

        }


    }

    @Override
    public void mouseClick(int x, int y) {

    }

}
