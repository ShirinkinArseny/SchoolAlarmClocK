package PC_part.SACK_pc_client.Dialogs;

import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Controls.UICanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MainUI extends JFrame {
    private JPanel contentPane;

    private void setUpDraggingWindow(Component c) {
        final int[] mouseDownX = {0};
        final int[] mouseDownY = {0};
        final Point[] mouseDownWindowLocation = new Point[1];

        c.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownX[0] = e.getXOnScreen();
                mouseDownY[0] = e.getYOnScreen();
                mouseDownWindowLocation[0] =getLocation();
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

        c.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseDownWindowLocation[0]!=null) {
                    setLocation(
                            mouseDownWindowLocation[0].x + e.getXOnScreen() - mouseDownX[0],
                            mouseDownWindowLocation[0].y + e.getYOnScreen() - mouseDownY[0]
                    );
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    public MainUI() {
        setContentPane(contentPane);

        setUndecorated(true);

        DataWrapper.init();
        UICanvas uic=new UICanvas();

        contentPane.add(uic);
        setUpDraggingWindow(uic);


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setResizable(false);
        setVisible(true);



        uic.start();
    }

    public static void main(String[] args) {
        new MainUI();
    }
}
