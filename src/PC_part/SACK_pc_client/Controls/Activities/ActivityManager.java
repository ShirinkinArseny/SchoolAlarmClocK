package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Controls.TimeFunction;
import PC_part.SACK_pc_client.Controls.UICanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ActivityManager implements Activity {

    private Activity currentActivity;

    private TimeFunction newActivityY =new TimeFunction(0.6f, -UICanvas.windowHeight, 0);

    public ActivityManager() {
        currentActivity=new Activity() {
            public void draw(Graphics2D g2) {
            }
            public void mouseClick(int x, int y) {
            }
        };
        oldRender=new BufferedImage(UICanvas.windowWidth, UICanvas.windowHeight, BufferedImage.TYPE_3BYTE_BGR);
        newRender=new BufferedImage(UICanvas.windowWidth, UICanvas.windowHeight, BufferedImage.TYPE_3BYTE_BGR);

        oldG= (Graphics2D) oldRender.getGraphics();
        oldG.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        oldG.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        newG= (Graphics2D) newRender.getGraphics();
        newG.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        newG.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private BufferedImage oldRender;
    private BufferedImage newRender;
    private Graphics2D oldG;
    private Graphics2D newG;

    public void setActivity(Activity a) {
        currentActivity.draw(oldG);
        currentActivity=a;
        newActivityY.launch();
        //-Dsun.java2d.opengl=True
    }

    @Override
    public void draw(Graphics2D g2) {
        if (newActivityY.isDone()) {
            currentActivity.draw(g2);
        } else {
            currentActivity.draw(newG);
            g2.drawImage(oldRender, 0, (int)newActivityY.get4SpeedDownValue()+UICanvas.windowHeight, null);
            g2.drawImage(newRender, 0, (int)newActivityY.get4SpeedDownValue(), null);
        }
    }

    @Override
    public void mouseClick(int x, int y) {
        if (newActivityY.isDone() && !UICanvas.longOperationWaiter.getIsLocked())
            currentActivity.mouseClick(x, y);
    }
}
