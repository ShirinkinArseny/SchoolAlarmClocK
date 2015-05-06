package PC_part.SACK_pc_client.Dialogs;

import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.Ring;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.function.Consumer;

public class AddRingDialogue extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel resultLabel;
    private JTextField input;
    private JCheckBox isShort;
    private final Consumer<Ring> onRingSet;

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

            resultLabel.setText(h+" "+ Labels.hours +", "+m+" "+Labels.minutes+", "+s+" "+Labels.seconds);
            buttonOK.setEnabled(true);
        }

        else if (
                input.getText().matches(
                        "(([0-1]?\\d)|"+
                                "(2[0-3]))"+
                                "[ ]*:[ ]*"+
                                "[0-5]?\\d"
                )


                )
        {
            String[] times=input.getText().split(":");
            int h=Integer.valueOf(times[0].trim());
            int m=Integer.valueOf(times[1].trim());
            resultLabel.setText(h+" "+Labels.hours +", "+m+" "+Labels.minutes);
            buttonOK.setEnabled(true);


        }

        else {
            if (Objects.equals(input.getText(), ""))
                resultLabel.setText(Labels.empty);
            else
                resultLabel.setText(Labels.cannotParseTime);
            buttonOK.setEnabled(false);
        }


    }

    public AddRingDialogue(Consumer<Ring> onTimeSet) {
        this.onRingSet = onTimeSet;
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
        setLocation(UICanvas.clickX-200, UICanvas.clickY-100);

        setVisible(true);
    }

    private void onOK() {
        String[] times=input.getText().split(":");
        int h=Integer.valueOf(times[0].trim());
        int m=Integer.valueOf(times[1].trim());
        int s=times.length>2?Integer.valueOf(times[2].trim()):0;

        onRingSet.accept(new Ring(h * 3600 + m * 60 + s, isShort.isSelected()));
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
