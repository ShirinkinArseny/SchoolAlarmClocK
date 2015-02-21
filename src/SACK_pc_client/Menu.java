package SACK_pc_client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Menu {


    private String[] menuItems;

    public int getSelectedMenuItem() {
        return selectedMenuItem;
    }

    private int selectedMenuItem = 3;
    private int lastSelectedMenuItem = 0;
    private boolean fullySelected = true;

    private Runnable newItemSelected;

    private BufferedImage triangle;

    private ParabolicTimeFunction inMotion;
    private ParabolicTimeFunction outMotion;

    private static final float hideAndAppearTime=0.3f;

    public Menu(String[] items, Runnable newItemSelected) {
        try {
            triangle = ImageIO.read(new File("/home/nameless/IdeaProjects/SoundTest/src/SACK_pc_client/DarkArrow.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        float imageWidth = triangle.getWidth();
        inMotion=new ParabolicTimeFunction(hideAndAppearTime, -imageWidth, 0);
        outMotion=new ParabolicTimeFunction(hideAndAppearTime, 0, -imageWidth);
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
            lastSelectedMenuItem = selectedMenuItem;
            selectedMenuItem = index;
            fullySelected = false;
            inMotion.launch();
            outMotion.launch();
            newItemSelected.run();
        }
    }


    public static final int topMargin=30;
    public static final int itemHeight=50;
    public static final int itemWidth=225;
    public static final int textXDrawOffset =20;
    public static final int textYDrawOffset =35;
    public void draw(Graphics2D g2, int w, int h) {g2.setColor(UICanvas.lightForegroundColor);
        g2.fillRect(0, 0, itemWidth, h);


        g2.setFont(UICanvas.font);
        for (int i = 0; i < menuItems.length; i++) {
            g2.setColor(UICanvas.darkFontColor);
            g2.drawString(menuItems[i], textXDrawOffset, topMargin + i * itemHeight + textYDrawOffset);
        }

        g2.setColor(UICanvas.darkForegroundColor);
        if (fullySelected) {
            g2.drawImage(triangle, 0, topMargin + selectedMenuItem * itemHeight, null);
        } else {
            g2.drawImage(triangle, (int)(outMotion.getSpeedUpValue()), topMargin + lastSelectedMenuItem * itemHeight, null);
            g2.drawImage(triangle, (int)(inMotion.getSpeedDownValue()), topMargin + selectedMenuItem * itemHeight, null);
        }

        g2.setColor(UICanvas.lightFontColor);
        if (!fullySelected) g2.drawString(menuItems[lastSelectedMenuItem], textXDrawOffset +(int)(outMotion.getSpeedUpValue()), topMargin + lastSelectedMenuItem * itemHeight + textYDrawOffset);
        g2.drawString(menuItems[selectedMenuItem], textXDrawOffset +(fullySelected?0:(int)(inMotion.getSpeedDownValue())), topMargin + selectedMenuItem * itemHeight + textYDrawOffset);

    }

    public void update() {
        if (!fullySelected) {

            if (inMotion.isDone() || outMotion.isDone()) {
                fullySelected = true;
            }
        }
    }

}
