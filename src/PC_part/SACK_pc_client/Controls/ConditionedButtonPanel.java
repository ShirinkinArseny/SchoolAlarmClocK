package PC_part.SACK_pc_client.Controls;

import PC_part.Common.Logger;
import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Resources.Images;
import sun.security.krb5.internal.crypto.Des;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConditionedButtonPanel {

    private Runnable[] actions;
    private String[] names;
    private int itemsNumber;
    private Supplier<Boolean> condition;

    private TimeFunction inMotion;
    private TimeFunction outMotion;
    private static final float hideAndAppearTime=0.7f;

    public ConditionedButtonPanel(Runnable[] actions, String[] names, Supplier<Boolean> condition) {
        this.condition = condition;
        if (actions.length==names.length) {
            itemsNumber=actions.length;
        } else {
            Logger.logError("ConditionedButtonPanel", "Not same names and actions number!");
        }
        this.actions = actions;
        this.names = names;

        inMotion = new TimeFunction(hideAndAppearTime, itemsNumber*Images.bigBackDoubleArrow.getWidth(), 0);
        outMotion = new TimeFunction(hideAndAppearTime, 0, itemsNumber*Images.bigBackDoubleArrow.getWidth());
    }

    public void mouseClick(int x, int y) {
        if (inMotion.isDone() && lastConditionValue)

            if (y>=UICanvas.windowHeight-80 &&
                    y<=UICanvas.windowHeight-80+Images.bigBackDoubleArrow.getHeight() &&
                    x>=UICanvas.windowWidth-itemWidth*itemsNumber && x<=UICanvas.windowWidth) {

                int xInd=itemsNumber-(x-UICanvas.windowWidth+itemWidth*itemsNumber)/itemWidth-1;

                actions[xInd].run();

            }
    }



    private static final int textYDrawOffset = 35;
    private static final int textXDrawOffset = 35;
    private static final int itemWidth = 263;

    private void drawItems(Graphics2D g2, int dx) {

        if (Design.drawShadows)
        for (int i = 0; i < itemsNumber; i++) {
            int x = UICanvas.windowWidth-itemWidth*(i+1)+dx;
            g2.drawImage(Images.bigBackDoubleArrowShadowed,
                    x- Design.shadowRadius,
                    UICanvas.windowHeight-80- Design.shadowRadius,
                    null);
        }

        g2.setColor(Design.darkFontColor);
        g2.setFont(Design.font);

        for (int i = 0; i < itemsNumber; i++) {
            int x = UICanvas.windowWidth-itemWidth*(i+1)+dx;
            g2.drawImage(Images.bigBackDoubleArrow,
                    x,
                    UICanvas.windowHeight - 80, null);
            g2.drawString(names[i], x+textXDrawOffset, UICanvas.windowHeight - 80+textYDrawOffset);
        }

    }

    private boolean lastConditionValue=false;
    public void draw(Graphics2D g2) {
        boolean newConditionValue=condition.get();

        if (newConditionValue) {

            if (inMotion.isDone()) {
                if (!lastConditionValue) {
                    inMotion.launch();
                } else
                    drawItems(g2, 0);
            } else {
                drawItems(g2, (int) inMotion.get2SpeedDownValue());
            }

        } else {

            if (outMotion.isDone()) {
                if (lastConditionValue) {
                    outMotion.launch();
                    drawItems(g2, (int) outMotion.get2SpeedUpValue());
                }
            } else {
                drawItems(g2, (int) outMotion.get2SpeedUpValue());
            }
        }

        lastConditionValue=newConditionValue;

    }

}
