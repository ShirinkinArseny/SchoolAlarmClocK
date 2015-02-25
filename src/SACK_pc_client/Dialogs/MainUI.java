package SACK_pc_client.Dialogs;

import SACK_pc_client.UICanvas;

import javax.swing.*;

public class MainUI extends JFrame {
    private JPanel contentPane;

    public MainUI() {
        setContentPane(contentPane);

        UICanvas uic=new UICanvas();

        contentPane.add(uic);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setVisible(true);

        uic.start();
    }

    public static void main(String[] args) {
        new MainUI();
    }
}
