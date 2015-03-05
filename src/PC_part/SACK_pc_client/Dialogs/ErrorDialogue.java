package PC_part.SACK_pc_client.Dialogs;

import PC_part.SACK_pc_client.Controls.UICanvas;

import javax.swing.*;
import java.awt.event.*;

public class ErrorDialogue extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea errorText;

    public ErrorDialogue(String text) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> dispose());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        errorText.setText(text);

        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setSize(400, 200);
        setLocation(UICanvas.clickX - 200, UICanvas.clickY - 100);

        setVisible(true);
    }

}
