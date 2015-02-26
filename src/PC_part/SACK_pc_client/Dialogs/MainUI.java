package PC_part.SACK_pc_client.Dialogs;

import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Controls.UICanvas;

import javax.swing.*;

public class MainUI extends JFrame {
    private JPanel contentPane;

    public MainUI() {
        setContentPane(contentPane);

        DataWrapper.init();
        UICanvas uic=new UICanvas();

        contentPane.add(uic);

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
