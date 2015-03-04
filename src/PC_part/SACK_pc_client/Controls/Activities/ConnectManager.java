package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Controls.Menu;
import PC_part.SACK_pc_client.Dialogs.IPAddressREquestDialogue;

import java.awt.*;

public class ConnectManager extends ActivityWithButtons<String> {

    private void tryWiFi() {
        new IPAddressREquestDialogue(DataWrapper::connect);
    }

    public ConnectManager() {

        super(Menu.topMargin + Menu.itemHeight + 10);

        addButton(this::reloadCheckboxes, Labels.updatePortsList);

        addButton(() -> {
                    UICanvas.longOperationWaiter.lock();
                    new Thread(() -> {
                        getSelectedItems().forEach(DataWrapper::connect);
                        DataWrapper.pingDuino();
                        try {
                            Thread.sleep(1500);//dirty hack for serial stabilisation
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        UICanvas.longOperationWaiter.unlock();
                    }).start();
                }, Labels.connectToSelected
        );

        addButton(DataWrapper::disconnect, Labels.disconnect);

        addButton(this::tryWiFi, Labels.network);

        reloadCheckboxes();
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        g2.setColor(Design.darkFontColor);
        g2.setFont(Design.font);
        g2.drawString(DataWrapper.getConnectionState(), PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45, UICanvas.windowHeight - 50);

        g2.setFont(Design.fontSmall);
        g2.drawString(Labels.usablePorts, PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45,
                PC_part.SACK_pc_client.Controls.Menu.topMargin + Menu.itemHeight + 20);

        if (portsNumber == 0) {
            g2.drawString(Labels.noUsablePorts, PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45,
                    PC_part.SACK_pc_client.Controls.Menu.topMargin + Menu.itemHeight + 45);
        }

    }

    private int portsNumber = 0;

    @Override
    public void loadCheckBoxes() {
        String[] ports = DataWrapper.getPorts();
        portsNumber = ports.length;
        for (String port : ports) {
            addCheckBox(port);
        }
    }
}
