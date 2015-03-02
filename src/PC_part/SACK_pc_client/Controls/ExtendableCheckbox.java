package PC_part.SACK_pc_client.Controls;

import PC_part.SACK_pc_client.Configurable.Design;

import java.awt.*;

public class ExtendableCheckbox<T> {

    private T data;

    private int x;
    private int x2;
    private int y, y2;
    private int w;
    private int h;

    private String text;

    private boolean selected=false;

    private static final float checkTime=0.3f;

    public ExtendableCheckbox(T data, int x, int y, int w, int h) {
        this.data=data;
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
        x2=x+w;
        y2=y+h;
        in=new TimeFunction(checkTime, 0, w);
        out=new TimeFunction(checkTime, w, 0);

        text=data.toString();
    }

    public boolean getContains(int x, int y) {
        return x>=this.x && x<=this.x2 && y>=this.y && y<=this.y2;
    }

    private TimeFunction in;
    private TimeFunction out;

    public void click(int x, int y) {
        if (getContains(x, y)) {
            selected = !selected;
            if (selected)
                in.launch();
            else {
                out.launch();
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Design.lightForegroundColor);
        if (selected) {
            if (in.isDone()) {
                g2.fillRect(x, y, w, h);
            } else
                g2.fillRect(x, y, (int) in.get2SpeedDownValue(), h);
        } else {
            if (!out.isDone()) {
                g2.fillRect(x, y, (int) out.get2SpeedUpValue(), h);
            }
        }
        g2.setColor(Design.darkFontColor);
        g2.setFont(Design.fontSmall);
        g2.drawString(text, x+5, y+20);

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

    public T getValue() {
        return data;
    }

    public boolean getSelected() {
        return selected;
    }

    public void unselect() {
        selected=false;
        out.launch();
    }
}
