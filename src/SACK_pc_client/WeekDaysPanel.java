package SACK_pc_client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeekDaysPanel {

    public enum State {selectable, nonSelectable, hidden}

    private State state=State.selectable;
    private State oldState=State.selectable;

    private String[] days=new String[]{"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};

    public void setState(State s) {
        if (s!=state) {
            oldState=state;
            state = s;
        }
    }

    private BufferedImage triangle;

    public WeekDaysPanel() {
        try {
            triangle = ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/DarkDoubleArrow.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void mouseClick(int x, int y) {
    }

    private ParabolicTimeFunction inMotion=new ParabolicTimeFunction(1.0f, -dayWidth*days.length+Menu.itemWidth, Menu.itemWidth);
    private ParabolicTimeFunction outMotion=new ParabolicTimeFunction(1.0f, Menu.itemWidth, -dayWidth*days.length+Menu.itemWidth);

    public static final int textYDrawOffset =35;
    public static final int textXDrawOffset=30;
    public static final int dayWidth=108;
    public void draw(Graphics2D g2, int w, int h) {
        g2.setColor(UICanvas.lightFontColor);
        g2.setFont(UICanvas.font);


        for (int i=0; i<days.length; i++) {
            int x=inMotion.getSpeedDownValue()+dayWidth*i-1;
            g2.drawImage(triangle, x, 30, null);
            g2.drawString(days[i], x+textXDrawOffset, 30+ textYDrawOffset);
        }
    }

    public void update() {
    }


}
