package PC_part.SACK_pc_client.Dialogs;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Controls.UICanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class MainUI extends JFrame {
    private JPanel contentPane;

    public MainUI() {

        try {
            UIManager.setLookAndFeel(com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel.class.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }



        setContentPane(contentPane);

        setUndecorated(!Design.windowBoarders);

        DataWrapper.init();
        UICanvas uic = new UICanvas();

        contentPane.add(uic);

        if (Design.dragWindow)
            setUpDraggingWindow(uic);


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);

        setResizable(Design.resizableWindow);

        if (Design.resizableWindow)
            setUpResizingWindow();

        setVisible(true);


        uic.start();
    }

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
                mouseDownWindowLocation[0] = getLocation();
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
                if (mouseDownWindowLocation[0] != null) {
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

    private void setUpResizingWindow() {

        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                UICanvas.windowWidth=getWidth();
                UICanvas.windowHeight=getHeight();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

    }

    public static void main(String[] args) {
        new MainUI();
    }
}
