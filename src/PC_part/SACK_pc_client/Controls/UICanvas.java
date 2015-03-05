package PC_part.SACK_pc_client.Controls;

import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.Activities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

public class UICanvas extends Canvas {

    public static int windowWidth=1100;
    public static int windowHeight=700;

    public static int clickX, clickY;

    public static final LongOperationWaiter longOperationWaiter=new LongOperationWaiter();

    private static final ActivityManager activityManager=new ActivityManager();

    private static final WeekDaysPanel weekDaysPanel=new WeekDaysPanel(date -> activityManager.setActivity(new TimeTable(date)));

    private static final PC_part.SACK_pc_client.Controls.Menu menu=new PC_part.SACK_pc_client.Controls.Menu(
            new String[]{Labels.chart, Labels.table, Labels.timeSync, Labels.connection, Labels.exit},
            (i) -> {
                switch (i) {
                    case 0: {
                        weekDaysPanel.setState(WeekDaysPanel.State.nonSelectable);
                        activityManager.setActivity(new Chart());
                        break;
                    }
                    case 1: {
                        weekDaysPanel.setState(WeekDaysPanel.State.selectable);
                        activityManager.setActivity(new TimeTable(weekDaysPanel.getLastSelectedDay()));
                        break;
                    }
                    case 2: {
                        weekDaysPanel.setState(WeekDaysPanel.State.hidden);
                        activityManager.setActivity(new TimeSync());
                        break;
                    }
                    case 3: {
                        weekDaysPanel.setState(WeekDaysPanel.State.hidden);
                        activityManager.setActivity(new ConnectManager());
                        break;
                    }
                    case 4: {
                        weekDaysPanel.setState(WeekDaysPanel.State.hidden);
                        System.exit(0);
                        break;
                    }
                }

            });

    public UICanvas() {
        super();
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickX=e.getXOnScreen();
                clickY=e.getYOnScreen();
                menu.mouseClick(e.getX(), e.getY());
                weekDaysPanel.mouseClick(e.getX(), e.getY());
                activityManager.mouseClick(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        menu.reselectMenuItem(1);
    }


    public void start() {
        createBufferStrategy(3);
        bs = getBufferStrategy();
        new Timer(20, e -> {
            update();
            draw();
        }).start();
    }

    private void update() {
        menu.update();
    }

    private BufferStrategy bs;

    private void draw() {

        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();


        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        activityManager.draw(g2);
        menu.drawShadow(g2);
        weekDaysPanel.draw(g2);
        menu.draw(g2);


        g2.dispose();

        bs.show();
    }
}
