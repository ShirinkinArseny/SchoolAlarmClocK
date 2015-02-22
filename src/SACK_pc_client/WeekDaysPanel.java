package SACK_pc_client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class WeekDaysPanel {

    private int selectedDay=2;
    private Consumer<Integer> onItemSelected;

    public enum State {selectable, nonSelectable, hidden}

    private State state = State.nonSelectable;
    private State oldState = State.selectable;

    private String[] days = new String[]{"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};

    public void setState(State s) {
        if (s != state) {
            oldState = state;
            state = s;

            inMotion.launch();
            outMotion.launch();
        }
    }

    private BufferedImage darkArrow;
    private BufferedImage lightArrow;

    public WeekDaysPanel(Consumer<Integer> onItemSelected) {
        this.onItemSelected = onItemSelected;
        try {
            darkArrow = ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/DarkDoubleArrow.png"));
            lightArrow = ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/LightDoubleArrow.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        selectDay(0);
    }

    public void selectDay(int day) {

        selectionMotion.reconstruct(hideAndAppearTime, Menu.itemWidth+ dayWidth * selectedDay - 1, Menu.itemWidth+ dayWidth * day - 1);
        selectionMotion.launch();
        selectedDay=day;

        onItemSelected.accept(day);

    }

    public void mouseClick(int x, int y) {
        if (state==State.selectable) {
            if (y>=Menu.topMargin+Menu.itemHeight && y<=Menu.topMargin+Menu.itemHeight*2 && x>=Menu.itemWidth && x<=Menu.itemWidth+dayWidth * days.length) {

                int xInd=(x-Menu.itemWidth)/dayWidth;
                selectDay(xInd);

            }
        }
    }

    private static final float hideAndAppearTime=0.4f;

    private ParabolicTimeFunction inMotion = new ParabolicTimeFunction(hideAndAppearTime, -dayWidth * days.length + Menu.itemWidth, Menu.itemWidth);
    private ParabolicTimeFunction outMotion = new ParabolicTimeFunction(hideAndAppearTime, Menu.itemWidth, -dayWidth * days.length + Menu.itemWidth);

    private ParabolicTimeFunction selectionMotion = new ParabolicTimeFunction(hideAndAppearTime, 0, 0);

    private void drawItems(Graphics2D g2, State s, int additionX, int additionY) {
        for (int i = 0; i < days.length; i++) {
            int x = additionX + dayWidth * i - 1;
            g2.drawImage(s==State.selectable?lightArrow:darkArrow, x, additionY, null);
        }

        if (s==State.selectable) {
            if (selectionMotion.isDone()) {
                g2.drawImage(darkArrow, additionX + dayWidth * selectedDay - 1, additionY, null);
            } else {
                g2.drawImage(darkArrow, (int)selectionMotion.getSpeedDownValue(), additionY, null);
            }

        }

        for (int i = 0; i < days.length; i++) {
            int x = additionX + dayWidth * i - 1;
            g2.setColor(s==State.nonSelectable||i==selectedDay?UICanvas.lightFontColor:UICanvas.darkFontColor);
            g2.drawString(days[i], x + textXDrawOffset,  additionY + textYDrawOffset);
        }
    }

    public static final int textYDrawOffset = 35;
    public static final int textXDrawOffset = 45;
    public static final int dayWidth = 108;

    public void draw(Graphics2D g2) {
        g2.setFont(UICanvas.font);

        if (inMotion.isDone() && outMotion.isDone()) {
            if (state == State.selectable || state == State.nonSelectable) {
                drawItems(g2, state, Menu.itemWidth, Menu.topMargin+(state == State.nonSelectable ? 0 : Menu.itemHeight));
            }
        } else {

            if (oldState == State.selectable || oldState == State.nonSelectable) {
                drawItems(g2, oldState, (int) outMotion.getSpeedUpValue(), Menu.topMargin+(oldState == State.nonSelectable ? 0 : Menu.itemHeight));
            }

            if (state == State.selectable || state == State.nonSelectable) {
                drawItems(g2, state, (int) inMotion.getSpeedDownValue(), Menu.topMargin+(state == State.nonSelectable ? 0 : Menu.itemHeight));
            }
        }
    }

    public void update() {
    }


}
