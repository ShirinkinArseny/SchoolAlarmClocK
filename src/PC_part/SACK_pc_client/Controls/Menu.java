package PC_part.SACK_pc_client.Controls;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Controls.Activities.Activity;
import PC_part.SACK_pc_client.Resources.Images;

import java.awt.*;
import java.util.function.Consumer;

public class Menu implements Activity {


    private String[] menuItems;

    public int getSelectedMenuItem() {
        return selectedMenuItem;
    }

    private int selectedMenuItem = 0;
    private int lastSelectedMenuItem = 0;
    private boolean fullySelected = true;

    private Consumer<Integer> newItemSelected;

    private TimeFunction inMotion;
    private TimeFunction outMotion;

    private static final float hideAndAppearTime=0.3f;

    public Menu(String[] items, Consumer<Integer> newItemSelected) {
        float imageWidth = Images.darkArrow.getWidth();
        inMotion=new TimeFunction(hideAndAppearTime, -imageWidth, 0);
        outMotion=new TimeFunction(hideAndAppearTime, 0, -imageWidth);
        menuItems=items;
        this.newItemSelected=newItemSelected;
    }



    public void mouseClick(int x, int y) {
        if (x<=itemWidth) {
            int yNum=(y-topMargin)/itemHeight;
            if (yNum>=0 && yNum<menuItems.length) {
                selectMenuItem(yNum);
            }
        }
    }

    public void selectMenuItem(int index) {
        if (index!=selectedMenuItem) {
            reselectMenuItem(index);
        }
    }

    public void reselectMenuItem(int index) {
        lastSelectedMenuItem = selectedMenuItem;
        selectedMenuItem = index;
        fullySelected = false;
        inMotion.launch();
        outMotion.launch();
        newItemSelected.accept(index);
    }


    public static final int topMargin=30;
    public static final int itemHeight=50;
    public static final int itemWidth=225;
    public static final int textXDrawOffset =20;
    public static final int textYDrawOffset =35;
    public void draw(Graphics2D g2) {g2.setColor(Design.lightForegroundColor);
        g2.fillRect(0, 0, itemWidth, UICanvas.windowHeight);


        g2.setFont(Design.font);
        for (int i = 0; i < menuItems.length; i++) {
            g2.setColor(Design.darkFontColor);
            g2.drawString(menuItems[i], textXDrawOffset, topMargin + i * itemHeight + textYDrawOffset);
        }

        g2.setColor(Design.darkForegroundColor);
        if (fullySelected) {
            g2.drawImage(Images.darkArrow, 0, topMargin + selectedMenuItem * itemHeight, null);
        } else {
            g2.drawImage(Images.darkArrow, (int)(outMotion.get2SpeedUpValue()), topMargin + lastSelectedMenuItem * itemHeight, null);
            g2.drawImage(Images.darkArrow, (int)(inMotion.get2SpeedDownValue()), topMargin + selectedMenuItem * itemHeight, null);
        }

        g2.setColor(Design.lightFontColor);
        if (!fullySelected) g2.drawString(menuItems[lastSelectedMenuItem], textXDrawOffset +(int)(outMotion.get2SpeedUpValue()), topMargin + lastSelectedMenuItem * itemHeight + textYDrawOffset);
        g2.drawString(menuItems[selectedMenuItem], textXDrawOffset +(fullySelected?0:(int)(inMotion.get2SpeedDownValue())), topMargin + selectedMenuItem * itemHeight + textYDrawOffset);

    }

    public void update() {
        if (!fullySelected) {

            if (inMotion.isDone() || outMotion.isDone()) {
                fullySelected = true;
            }
        }
    }
}
