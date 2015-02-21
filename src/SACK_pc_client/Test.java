package SACK_pc_client;

import javax.swing.*;

public class Test extends JFrame {
    private JPanel contentPane;

    public Test() {
        setContentPane(contentPane);

        UICanvas uic=new UICanvas();

        contentPane.add(uic);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);

        uic.start();
    }

    public static void main(String[] args) {
        new Test();
    }
}
