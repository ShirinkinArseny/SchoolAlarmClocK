package PC_part.SACK_pc_client.Controls;

import PC_part.SACK_pc_client.Controls.Activities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

public class UICanvas extends Canvas {

    public static final int windowWidth=1100;
    public static final int windowHeight=700;

    public static final Color lightBackgroundColor = new Color(0xe6e6e6);
    public static final Color lightForegroundColor = new Color(0x55d85e);
    public static final Color darkForegroundColor = new Color(0x1e8224);
    public static final Color lightFontColor = lightBackgroundColor;
    public static final Color darkFontColor = new Color(0x000000);

    public static final Font font = new Font("Fira Sans Thin", Font.PLAIN, 35);
    public static final Font fontSmall = new Font("Fira Sans Thin", Font.PLAIN, 20);

    private static ActivityManager activityManager=new ActivityManager();

    private static WeekDaysPanel weekDaysPanel=new WeekDaysPanel(date -> activityManager.setActivity(new TimeTable(date)));

    private static PC_part.SACK_pc_client.Controls.Menu menu=new PC_part.SACK_pc_client.Controls.Menu(
            new String[]{"Чарт", "Расписание", "Время", "Соединение", "Выход"},
            (i) -> {
                switch (i) {
                    case 0: {
                        weekDaysPanel.setState(WeekDaysPanel.State.nonSelectable);
                        activityManager.setActivity(new Chart());
                        break;
                    }
                    case 1: {
                        weekDaysPanel.setState(WeekDaysPanel.State.selectable);
                        activityManager.setActivity(new TimeTable(0));
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


    long nanoTime = System.nanoTime();
    public void start() {
        createBufferStrategy(3);
        bs = getBufferStrategy();
        new Timer(20, e -> {
            long currentNanoTime = System.nanoTime();
            update();
            draw();
            nanoTime = currentNanoTime;
        }).start();
    }

    private void update() {
        menu.update();
    }

    private BufferStrategy bs;

    private void draw() {

        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();


        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        activityManager.draw(g2);
        weekDaysPanel.draw(g2);
        menu.draw(g2);


        g2.dispose();

        bs.show();
    }
}
