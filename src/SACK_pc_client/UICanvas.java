package SACK_pc_client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

public class UICanvas extends Canvas {

    public static final Color lightBackgroundColor = new Color(0xe6e6e6);
    public static final Color lightForegroundColor = new Color(0x55d85e);
    public static final Color darkForegroundColor = new Color(0x1e8224);
    public static final Color lightFontColor = lightBackgroundColor;
    public static final Color darkFontColor = new Color(0x000000);

    public static final Font font = new Font("Fira Sans Thin", Font.PLAIN, 35);

    private static Menu menu=new Menu(new String[]{"Расписание", "Чарт", "Настройки", "Выход"},
            () -> {

            });
    private static WeekDaysPanel weekDaysPanel=new WeekDaysPanel();

    public UICanvas() {
        super();
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                menu.mouseClick(e.getX(), e.getY());
                weekDaysPanel.mouseClick(e.getX(), e.getY());
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
        weekDaysPanel.update();
    }

    private BufferStrategy bs;

    private void draw() {

        Graphics2D g2 = (Graphics2D) bs.getDrawGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        g2.setColor(lightBackgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        menu.draw(g2, getWidth(), getHeight());
        weekDaysPanel.draw(g2, getWidth(), getHeight());


        g2.dispose();

        bs.show();
    }
}
