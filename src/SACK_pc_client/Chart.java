package SACK_pc_client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Chart {

    private BufferedImage backArrow;
    private BufferedImage markBoards;
    private BufferedImage markFilled;
    public Chart() {
        try {
            backArrow = ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/BackArrow.png"));
            markBoards = ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/MarkBoarders.png"));
            markFilled = ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/MarkFilled.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void update() {

    }

    private static final int timeMarkXPos=Menu.itemWidth+7*WeekDaysPanel.dayWidth;
    private static final int textXOffset=40;
    private static final int textYOffset=35;
    private static final int markXOffset=WeekDaysPanel.dayWidth/2+15;

    public void draw(Graphics2D g2) {

        g2.setFont(UICanvas.font);
        g2.setStroke(new BasicStroke(1));
        for (int i=9; i<=19; i+=2) {
            int y=(i - 7) * Menu.itemHeight;
            g2.drawImage(backArrow, timeMarkXPos, y, null);

            g2.setColor(UICanvas.lightForegroundColor);
            g2.drawLine(Menu.itemWidth-1, y+backArrow.getHeight()/2, timeMarkXPos, y+backArrow.getHeight()/2);

            g2.setColor(UICanvas.darkFontColor);
            g2.drawString(String.valueOf(i), timeMarkXPos + textXOffset, y + textYOffset);
        }

        for (int i=0; i<7; i++) {

            ArrayList<Ring> rigns=DataWrapper.getRings(i);

            for (Ring r: rigns) {

                if (r.getHours()>=9 && r.getHours()<=19) {
                    int y = (int) ((r.getHours() - 7) * Menu.itemHeight + (backArrow.getHeight() - markBoards.getHeight()) / 2);
                    g2.drawImage(markBoards, Menu.itemWidth + i * WeekDaysPanel.dayWidth + markXOffset - markBoards.getWidth() / 2, y, null);
                }
            }

            for (Ring r: rigns) {
                if (r.getHours()>=9 && r.getHours()<=19) {
                int y= (int) ((r.getHours() - 7) * Menu.itemHeight+(backArrow.getHeight()-markFilled.getHeight())/2);
                g2.drawImage(markFilled, Menu.itemWidth+i*WeekDaysPanel.dayWidth+markXOffset-markFilled.getWidth()/2, y, null);
                }
            }

        }


    }

}
