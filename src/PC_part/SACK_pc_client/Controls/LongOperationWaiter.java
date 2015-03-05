package PC_part.SACK_pc_client.Controls;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.DataWrapper;

import java.awt.*;

public class LongOperationWaiter {


    public void processIfConnected(Runnable r) {
        if (DataWrapper.getIsConnected()) {
            processOperation(r);
        } else {
            DataWrapper.processError(Labels.cannotPerformOperationCuzNoConnection);
        }
    }

    public void processOperation(Runnable r) {
        lock();
        new Thread(() -> {
            r.run();
            unlock();
        }).start();
    }

    private final TimeFunction in=new TimeFunction(0.3f, 0, 255);
    private final TimeFunction out=new TimeFunction(0.3f, 255, 0);

    private boolean locked=false;

    private void lock() {
        if (!locked) {
            setUpCircleCenters();
            in.launch();
            locked = true;
        }
    }

    private void unlock() {
        out.launch();
        locked=false;
    }

    public boolean getIsLocked() {
        return locked || !out.isDone();
    }

    private static final Color dark=new Color(0, 0, 0, 128);
    private static final Color semidark=new Color(0, 0, 0, 64);

    private final TimeFunction roll=new TimeFunction(0.8f, 0, 360);

    private static final int diameter=90;
    private static int rollX;
    private static int rollY;

    private static final int diameterBackground=110;
    private static int rollXBackground;
    private static int rollYBackground;

    private void setUpCircleCenters() {
        rollX=(UICanvas.windowWidth-Menu.itemWidth)/2+Menu.itemWidth-diameter/2;
        rollY=UICanvas.windowHeight/2-diameter/2;
        rollXBackground=(UICanvas.windowWidth-Menu.itemWidth)/2+Menu.itemWidth-diameterBackground/2;
        rollYBackground=UICanvas.windowHeight/2-diameterBackground/2;

    }

    public void draw(Graphics2D g2) {

        if (locked) {

            float linearValue=in.getLinearValue();

            if (!in.isDone()) {
                g2.setColor(new Color(0, 0, 0, (int)(linearValue/2)));
            } else {
                g2.setColor(dark);
            }
            g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);

            if (!in.isDone()) {
                g2.setColor(new Color(0, 0, 0, (int)(linearValue/4)));
            } else {
                g2.setColor(semidark);
            }
            g2.fillOval(rollXBackground, rollYBackground, diameterBackground, diameterBackground);

            if (roll.isDone()) roll.launch();

            if (!in.isDone()) {
                g2.setColor(new Color(
                        Design.lightForegroundColor.getRed(),
                        Design.lightForegroundColor.getGreen(),
                        Design.lightForegroundColor.getBlue(), (int)linearValue));
            } else {
                g2.setColor(Design.lightForegroundColor);
            }

            int angle= (int) roll.getLinearValue();
            g2.setStroke(Design.fat);
            g2.drawArc(rollX, rollY, diameter, diameter, angle, 90);
            g2.drawArc(rollX, rollY, diameter, diameter, angle + 180, 90);
        } else

            if (!out.isDone()) {

                float linearValue=out.getLinearValue();

                g2.setColor(new Color(0, 0, 0, (int) (linearValue / 2)));
                g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);
                g2.setColor(new Color(0, 0, 0, (int) (linearValue / 4)));
                g2.fillOval(rollXBackground, rollYBackground, diameterBackground, diameterBackground);
                if (roll.isDone()) roll.launch();
                g2.setColor(new Color(
                        Design.lightForegroundColor.getRed(),
                        Design.lightForegroundColor.getGreen(),
                        Design.lightForegroundColor.getBlue(), (int) linearValue));
                int angle= (int) roll.getLinearValue();
                g2.setStroke(Design.fat);
                g2.drawArc(rollX, rollY, diameter, diameter, angle, 90);
                g2.drawArc(rollX, rollY, diameter, diameter, angle + 180, 90);

            }
    }

}
