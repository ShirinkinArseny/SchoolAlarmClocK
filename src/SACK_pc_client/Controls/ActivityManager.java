package SACK_pc_client.Controls;

import SACK_pc_client.UICanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ActivityManager implements Activity {

    private Activity currentActivity;

    private TimeFunction newActivityY =new TimeFunction(0.6f, -UICanvas.windowHeight, 0);

    public ActivityManager() {
        currentActivity=new Activity() {
            public void update() {
            }
            public void draw(Graphics2D g2) {
            }
            public void mouseClick(int x, int y) {
            }
        };
    }

    private BufferedImage oldRender;
    private BufferedImage newRender;

    public void setActivity(Activity a) {
        oldRender=new BufferedImage(UICanvas.windowWidth, UICanvas.windowHeight, BufferedImage.TYPE_INT_ARGB);
        newRender=new BufferedImage(UICanvas.windowWidth, UICanvas.windowHeight, BufferedImage.TYPE_INT_ARGB);


        Graphics2D oldG= (Graphics2D) oldRender.getGraphics();
        oldG.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        oldG.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Graphics2D newG= (Graphics2D) newRender.getGraphics();
        newG.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        newG.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        currentActivity.draw(oldG);
        currentActivity=a;
        currentActivity.draw(newG);
        newActivityY.launch();
        oldG.dispose(); newG.dispose();
    }

    @Override
    public void update() {
        if (newActivityY.isDone())
            currentActivity.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (newActivityY.isDone()) {
            currentActivity.draw(g2);
        } else {
            g2.drawImage(oldRender, 0, (int)newActivityY.get4SpeedDownValue()+UICanvas.windowHeight, null);
            g2.drawImage(newRender, 0, (int)newActivityY.get4SpeedDownValue(), null);
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        if (newActivityY.isDone())
            currentActivity.mouseClick(x, y);
    }
}
