package SACK_pc_client.Dialogs;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.function.Consumer;

public class AddRingDialogue extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea input;
    private JLabel resultLabel;
    private Consumer<Integer> onTimeSet;

    private void updateTime() {


        if (

        input.getText().matches(
                "(([0-1]?\\d)|"+
                        "(2[0-3]))"+
                        "[ ]*:[ ]*"+
                        "[0-5]?\\d"+
                        "[ ]*:[ ]*"+
                        "[0-5]?\\d"
        )
                ) {

                String[] times=input.getText().split(":");
            int h=Integer.valueOf(times[0].trim());
            int m=Integer.valueOf(times[1].trim());
            int s=Integer.valueOf(times[2].trim());

            resultLabel.setText(h+" часов, "+m+" минут, "+s+" секунд");
            buttonOK.setEnabled(true);
        }
        else {
            if (Objects.equals(input.getText(), ""))
                resultLabel.setText("Пусто ~_~");
            else
                resultLabel.setText("Не могу распознать время T_T");
            buttonOK.setEnabled(false);
        }


    }

    public AddRingDialogue(Consumer<Integer> onTimeSet) {
        this.onTimeSet = onTimeSet;
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
                updateTime();
            }
        });
        input.setText(""); updateTime();

        setSize(400, 200);
        setVisible(true);
    }

    private void onOK() {
        String[] times=input.getText().split(":");
        int h=Integer.valueOf(times[0].trim());
        int m=Integer.valueOf(times[1].trim());
        int s=Integer.valueOf(times[2].trim());
        onTimeSet.accept(h*3600+m*60+s);
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
