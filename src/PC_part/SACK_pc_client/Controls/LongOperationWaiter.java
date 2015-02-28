package PC_part.SACK_pc_client.Controls;

import java.awt.*;

public class LongOperationWaiter {

    private TimeFunction in=new TimeFunction(0.3f, 0, 255);
    private TimeFunction out=new TimeFunction(0.3f, 255, 0);

    private boolean locked=false;

    public void lock() {
        if (!locked) {
            in.launch();
            locked = true;
        }
    }

    public void unlock() {
        out.launch();
        locked=false;
    }

    public boolean getIsLocked() {
        return locked || !out.isDone();
    }

    private static final Color dark=new Color(0, 0, 0, 128);
    private static final Color semidark=new Color(0, 0, 0, 64);

    private TimeFunction roll=new TimeFunction(0.8f, 0, 360);

    private static final int diameter=90;
    private static final int rollX=(UICanvas.windowWidth-Menu.itemWidth)/2+Menu.itemWidth-diameter/2;
    private static final int rollY=UICanvas.windowHeight/2-diameter/2;

    private static final int diameterBackground=110;
    private static final int rollXBackground=(UICanvas.windowWidth-Menu.itemWidth)/2+Menu.itemWidth-diameterBackground/2;
    private static final int rollYBackground=UICanvas.windowHeight/2-diameterBackground/2;

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
                        UICanvas.lightForegroundColor.getRed(),
                        UICanvas.lightForegroundColor.getGreen(),
                        UICanvas.lightForegroundColor.getBlue(), (int)linearValue));
            } else {
                g2.setColor(UICanvas.lightForegroundColor);
            }

            int angle= (int) roll.getLinearValue();
            g2.setStroke(UICanvas.fat);
            g2.drawArc(rollX, rollY, diameter, diameter, angle, 90);
            g2.drawArc(rollX, rollY, diameter, diameter, angle + 180, 90);
            g2.setStroke(UICanvas.thin);
        } else

            if (!out.isDone()) {

                float linearValue=out.getLinearValue();

                g2.setColor(new Color(0, 0, 0, (int)(linearValue/2)));
                g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);
                g2.setColor(new Color(0, 0, 0, (int)(linearValue/4)));
                g2.fillOval(rollXBackground, rollYBackground, diameterBackground, diameterBackground);
                roll.launch();
                g2.setColor(new Color(
                        UICanvas.lightForegroundColor.getRed(),
                        UICanvas.lightForegroundColor.getGreen(),
                        UICanvas.lightForegroundColor.getBlue(), (int)linearValue));
                int angle= (int) roll.getLinearValue();
                g2.setStroke(UICanvas.fat);
                g2.drawArc(rollX, rollY, diameter, diameter, angle, 90);
                g2.drawArc(rollX, rollY, diameter, diameter, angle + 180, 90);
                g2.setStroke(UICanvas.thin);

            }
    }

}
