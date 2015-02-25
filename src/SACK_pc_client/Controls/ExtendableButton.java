package SACK_pc_client.Controls;

import SACK_pc_client.Resources.Images;
import SACK_pc_client.UICanvas;

import java.awt.*;

public class ExtendableButton {

    /*
    Fuck the MVC
     */


    private int x;
    private int x2;
    private int y, y2;
    private int w;
    private int h;

    private String text;

    private static final float checkTime=0.3f;
    private Runnable onclick;

    public ExtendableButton(Runnable onclick, String text, int x, int y, int w, int h) {
        this.onclick = onclick;
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        x2=x+w;
        y2=y+h;
        this.text=text;
    }

    public boolean getContains(int x, int y) {
        return x>=this.x && x<=this.x2 && y>=this.y && y<=this.y2;
    }

    public void click(int x, int y) {
        if (getContains(x, y)) {
            onclick.run();
        }
    }

    private static final int textXDrawOffset=30;
    private static final int textYDrawOffset=35;
    public void draw(Graphics2D g2) {
        g2.drawImage(Images.bigBackArrow, x, y, null);
        g2.setColor(UICanvas.darkFontColor);
        g2.setFont(UICanvas.font);
        g2.drawString(text, x+textXDrawOffset, y+textYDrawOffset);

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }


}
