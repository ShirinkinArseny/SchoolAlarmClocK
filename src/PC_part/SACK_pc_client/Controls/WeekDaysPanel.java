package PC_part.SACK_pc_client.Controls;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Resources.Images;

import java.awt.*;
import java.util.function.Consumer;

public class WeekDaysPanel {

    private int selectedDay=2;
    private final Consumer<Integer> onItemSelected;

    public int getLastSelectedDay() {
        return selectedDay;
    }

    public enum State {selectable, nonSelectable, hidden}

    private State state = State.nonSelectable;
    private State oldState = State.selectable;

    private final String[] days = new String[]{
            Labels.monday,
            Labels.tuesday,
            Labels.wednesday,
            Labels.thursday,
            Labels.friday,
            Labels.saturday,
            Labels.sunday};

    public void setState(State s) {
        if (s != state) {
            oldState = state;
            state = s;

            inMotion.launch();
            outMotion.launch();
        }
    }

    public WeekDaysPanel(Consumer<Integer> onItemSelected) {
        this.onItemSelected = onItemSelected;
        selectDay(0);
    }

    private void selectDay(int day) {

        if (day!=selectedDay) {

            selectionMotion.reconstruct(hideAndAppearTime, PC_part.SACK_pc_client.Controls.Menu.itemWidth + dayWidth * selectedDay - 1, PC_part.SACK_pc_client.Controls.Menu.itemWidth + dayWidth * day - 1);
            selectionMotion.launch();
            selectedDay = day;

            onItemSelected.accept(day);
        }

    }

    public void mouseClick(int x, int y) {
        if (state==State.selectable) {
            if (y>= PC_part.SACK_pc_client.Controls.Menu.topMargin+ PC_part.SACK_pc_client.Controls.Menu.itemHeight && y<= PC_part.SACK_pc_client.Controls.Menu.topMargin+ PC_part.SACK_pc_client.Controls.Menu.itemHeight*2 && x>= PC_part.SACK_pc_client.Controls.Menu.itemWidth && x<= PC_part.SACK_pc_client.Controls.Menu.itemWidth+dayWidth * days.length) {

                int xInd=(x- PC_part.SACK_pc_client.Controls.Menu.itemWidth)/dayWidth;

                selectDay(xInd);

            }
        }
    }

    private static final float hideAndAppearTime=0.4f;

    private final TimeFunction inMotion = new TimeFunction(hideAndAppearTime, -dayWidth * days.length + PC_part.SACK_pc_client.Controls.Menu.itemWidth, PC_part.SACK_pc_client.Controls.Menu.itemWidth);
    private final TimeFunction outMotion = new TimeFunction(hideAndAppearTime, PC_part.SACK_pc_client.Controls.Menu.itemWidth, -dayWidth * days.length + PC_part.SACK_pc_client.Controls.Menu.itemWidth);

    private final TimeFunction selectionMotion = new TimeFunction(hideAndAppearTime, 0, 0);

    private void drawItems(Graphics2D g2, State s, int additionX, int additionY) {


        if (Design.drawShadows)
        for (int i = 0; i < days.length; i++) {
            int x = additionX + dayWidth * i - 2-Design.shadowRadius;
            g2.drawImage(Images.doubleArrowShadowed, x, additionY-Design.shadowRadius, null);
        }

        for (int i = 0; i < days.length; i++) {
            int x = additionX + dayWidth * i - 1;
            g2.drawImage(Images.lightDoubleArrow, x, additionY, null);
        }

        if (s==State.selectable) {
            if (selectionMotion.isDone()) {

                if (Design.drawShadows)
                g2.drawImage(Images.doubleArrowShadowed,
                        additionX + dayWidth * selectedDay - 1-Design.shadowRadius,
                        additionY-Design.shadowRadius, null);


                g2.drawImage(Images.darkDoubleArrow, additionX + dayWidth * selectedDay - 1, additionY, null);

            } else {

                if (Design.drawShadows)
                g2.drawImage(Images.doubleArrowShadowed,
                        (int)selectionMotion.get2SpeedDownValue()-Design.shadowRadius,
                        additionY-Design.shadowRadius, null);

                g2.drawImage(Images.darkDoubleArrow, (int)selectionMotion.get2SpeedDownValue(), additionY, null);
            }

        }

        for (int i = 0; i < days.length; i++) {
            int x = additionX + dayWidth * i - 1;
            g2.setColor(i==selectedDay&&s== State.selectable? Design.lightFontColor: Design.darkFontColor);
            g2.drawString(days[i], x + textXDrawOffset,  additionY + textYDrawOffset);
        }
    }

    private static final int textYDrawOffset = 35;
    private static final int textXDrawOffset = 45;
    public static final int dayWidth = 108;

    public void draw(Graphics2D g2) {
        g2.setFont(Design.font);

        if (inMotion.isDone() && outMotion.isDone()) {
            if (state == State.selectable || state == State.nonSelectable) {
                drawItems(g2, state, PC_part.SACK_pc_client.Controls.Menu.itemWidth, PC_part.SACK_pc_client.Controls.Menu.topMargin+(state == State.nonSelectable ? 0 : PC_part.SACK_pc_client.Controls.Menu.itemHeight));
            }
        } else {

            if (oldState == State.selectable || oldState == State.nonSelectable) {
                drawItems(g2, oldState, (int) outMotion.get2SpeedUpValue(), PC_part.SACK_pc_client.Controls.Menu.topMargin+(oldState == State.nonSelectable ? 0 : PC_part.SACK_pc_client.Controls.Menu.itemHeight));
            }

            if (state == State.selectable || state == State.nonSelectable) {
                drawItems(g2, state, (int) inMotion.get2SpeedDownValue(), PC_part.SACK_pc_client.Controls.Menu.topMargin+(state == State.nonSelectable ? 0 : PC_part.SACK_pc_client.Controls.Menu.itemHeight));
            }
        }
    }

}
