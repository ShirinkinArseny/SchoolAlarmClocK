package SACK_pc_client.Controls;

import SACK_pc_client.DataWrapper;
import SACK_pc_client.Dialogs.AddRingDialogue;
import SACK_pc_client.Resources.Images;
import SACK_pc_client.Ring;
import SACK_pc_client.UICanvas;

import java.awt.*;
import java.util.ArrayList;

public class TimeTable implements Activity {

    private ArrayList<ExtendableCheckbox<Ring>> ringsRects;
    private ArrayList<ExtendableButton> actions;

    private static final ArrayList<Ring> copiedItems=new ArrayList<>();

    private void reloadTimes(int weekday) {
        ringsRects.clear();
        ArrayList<Ring> rings= DataWrapper.getRings(weekday);
        for (int i=0; i<rings.size(); i++) {
            ringsRects.add(new ExtendableCheckbox<>(rings.get(i), (i / 14) * 110 + SACK_pc_client.Controls.Menu.itemWidth + 25,
                    i % 14 * 30 + SACK_pc_client.Controls.Menu.topMargin + 2 * SACK_pc_client.Controls.Menu.itemHeight + 10,
                    105, 25));
        }
    }

    public TimeTable(int weekDay) {

        ringsRects=new ArrayList<>();

        reloadTimes(weekDay);

        int w= Images.bigBackArrow.getWidth();
        int h=Images.bigBackArrow.getHeight();

        int timeMarkXPos=1100-Images.bigBackArrow.getWidth();//todo: window width



        actions=new ArrayList<>();

        actions.add(new ExtendableButton(() -> {

            new AddRingDialogue(integer -> {
                Ring r=new Ring(integer);
                DataWrapper.addRing(weekDay, r);
                reloadTimes(weekDay);
            });

            System.out.println("ADD ONE MOAR");
        }, "ДОБАВИТЬ ЕЩЁ ОДИН", timeMarkXPos, SACK_pc_client.Controls.Menu.topMargin+(2+actions.size())*(SACK_pc_client.Controls.Menu.itemHeight+20), w, h));

        actions.add(new ExtendableButton(() -> {

            ArrayList<Ring> nonSelected=new ArrayList<>();
            for (ExtendableCheckbox<Ring> r: ringsRects)
                if (!r.getSelected())
                    nonSelected.add(r.getValue());
            DataWrapper.setRings(weekDay, nonSelected);
            reloadTimes(weekDay);

            System.out.println("REM SELECTED");
        }, "УДАЛИТЬ ВЫДЕЛЕННЫЕ", timeMarkXPos, SACK_pc_client.Controls.Menu.topMargin+(2+actions.size())*(SACK_pc_client.Controls.Menu.itemHeight+20), w, h));

        actions.add(new ExtendableButton(() -> {

            copiedItems.clear();
            for (ExtendableCheckbox<Ring> r: ringsRects)
                if (r.getSelected())
                    copiedItems.add(r.getValue());

            System.out.println("COPY SELECTED");
        }, "КОПИРОВАТЬ ВЫДЕЛЕННЫЕ", timeMarkXPos, SACK_pc_client.Controls.Menu.topMargin+(2+actions.size())*(SACK_pc_client.Controls.Menu.itemHeight+20), w, h));

        actions.add(new ExtendableButton(() -> {

            DataWrapper.addRings(weekDay, copiedItems);
            reloadTimes(weekDay);

            System.out.println("INSERT");
        }, "ВСТАВИТЬ", timeMarkXPos, SACK_pc_client.Controls.Menu.topMargin+(2+actions.size())*(SACK_pc_client.Controls.Menu.itemHeight+20), w, h));

        actions.add(new ExtendableButton(() -> {

            ringsRects.stream().filter(ExtendableCheckbox::getSelected).forEach(ExtendableCheckbox::unselect);

            System.out.println("DROP SELECT");
        }, "СБРОСИТЬ ВЫДЕЛЕНИЕ", timeMarkXPos, SACK_pc_client.Controls.Menu.topMargin+(2+actions.size())*(SACK_pc_client.Controls.Menu.itemHeight+20), w, h));

        actions.add(new ExtendableButton(() -> {

            DataWrapper.pop();
            reloadTimes(weekDay);

            System.out.println("LOAD");
        }, "ВЗЯТЬ С ДУИНЫ", timeMarkXPos, SACK_pc_client.Controls.Menu.topMargin+(2+actions.size())*(SACK_pc_client.Controls.Menu.itemHeight+20), w, h));

        actions.add(new ExtendableButton(() -> {

            DataWrapper.push();

            System.out.println("UPLOAD");
        }, "ЗАЛИТЬ ВСЁ НА ДУИНУ", timeMarkXPos, SACK_pc_client.Controls.Menu.topMargin+(2+actions.size())*(SACK_pc_client.Controls.Menu.itemHeight+20), w, h));

    }

    public void update() {

    }

    public void mouseClick(int x, int y) {
        for (ExtendableCheckbox<Ring> ringsRect : ringsRects) {
            ringsRect.click(x, y);
        }
        for (ExtendableButton eb: actions)
            eb.click(x, y);
    }


    public void draw(Graphics2D g2) {
        g2.setColor(UICanvas.lightBackgroundColor);
        g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);

        g2.setFont(UICanvas.fontSmall);
        for (ExtendableCheckbox<Ring> ringsRect : ringsRects) {
            ringsRect.draw(g2);
        }

        for (ExtendableButton eb: actions)
            eb.draw(g2);

    }


}