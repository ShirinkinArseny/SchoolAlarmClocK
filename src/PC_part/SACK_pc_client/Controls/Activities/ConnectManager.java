package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Controls.Menu;

import java.awt.*;

public class ConnectManager extends ActivityWithButtons<String> {

    public ConnectManager() {

        super(Menu.topMargin+Menu.itemHeight+10);

        addButton(this::reloadCheckboxes, "ОБНОВИТЬ СПИСОК ПОРТОВ");

        addButton(() -> getSelectedItems().forEach(DataWrapper::connect), "СОЕДИНИТЬСЯ С ЭТИМ");

        addButton(DataWrapper::disconnect, "ОТСОЕДИНИТЬСЯ");

        reloadCheckboxes();
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        g2.setColor(UICanvas.darkFontColor);
        g2.setFont(UICanvas.font);
        g2.drawString(DataWrapper.getConnectionState(), PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45, UICanvas.windowHeight - 50);

        g2.setFont(UICanvas.fontSmall);
        g2.drawString("Доступные порты:", PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45,
                PC_part.SACK_pc_client.Controls.Menu.topMargin+Menu.itemHeight+20);

        if (portsNumber==0) {
            g2.drawString("А нету доступных портов ~_~", PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45,
                    PC_part.SACK_pc_client.Controls.Menu.topMargin+Menu.itemHeight+45);
        }

    }

    private int portsNumber=0;

    @Override
    public void loadCheckBoxes() {
        String[] ports = DataWrapper.getPorts();
        portsNumber=ports.length;
        for (String port : ports) {
            addCheckBox(port);
        }
    }
}
