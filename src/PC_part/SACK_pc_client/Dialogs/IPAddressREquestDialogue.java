package PC_part.SACK_pc_client.Dialogs;

import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.UICanvas;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class IPAddressREquestDialogue extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea input;
    private JLabel result;
    private final Consumer<int[]> onAddressSet;

    private final int[] currentIP=new int[]{0,0,0,0};

    private void deny(String reason) {
        buttonOK.setEnabled(false);
        result.setText(Labels.wrongIP+": "+reason);
    }

    private void accept() {
        buttonOK.setEnabled(true);
        result.setText(Labels.niceIP);
    }

    private void updateIP() {

        if (input.getText().equals("")) {
            deny(Labels.emptyIP);
            return;
        }

        if (!input.getText().matches("(\\d+\\.)*\\d*")) {
            deny(Labels.wrongCharactersInLine);
            return;
        }

        String[] text=input.getText().split("\\.");
        if (text.length==8 || text.length==6) deny(Labels.wrongBytesNumber_6_or_8);
        else
            if (text.length==4) {

                for (int i=0; i<4; i++) {
                    try {
                        currentIP[i] = Integer.valueOf(text[i]);
                    }
                    catch (NumberFormatException e) {
                        deny(Labels.tooBigByte);
                        return;
                    }

                    if (currentIP[i]>=256) {
                        deny(Labels.bigByte);
                        return;
                    }
                }

                accept();

            } else
                deny(Labels.wrongBytesNumber);

    }

    public IPAddressREquestDialogue(Consumer<int[]> onAddressSet) {
        this.onAddressSet = onAddressSet;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateIP();
            }
        });
        input.setText(""); updateIP();

        setSize(400, 200);
        setLocation(UICanvas.clickX - 200, UICanvas.clickY - 100);

        setVisible(true);
    }

    private void onOK() {
        onAddressSet.accept(currentIP);
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
