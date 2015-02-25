package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Controls.ExtendableButton;
import PC_part.SACK_pc_client.Controls.ExtendableCheckbox;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Resources.Images;

import java.awt.*;
import java.util.ArrayList;

public class ConnectManager implements Activity {

    private ArrayList<ExtendableButton> actions;
    private ArrayList<ExtendableCheckbox<String>> portsRects = new ArrayList<>();

    private void reloadPorts() {
        portsRects.clear();
        String[] ports = DataWrapper.getPorts();
        for (int i = 0; i < ports.length; i++) {
            portsRects.add(new ExtendableCheckbox<>(ports[i], (i / 14) * 160 + PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45,
                    i % 14 * 30 + PC_part.SACK_pc_client.Controls.Menu.topMargin + 10,
                    155, 25));
        }
    }

    public ConnectManager() {
        int timeMarkXPos = UICanvas.windowWidth - Images.bigBackArrow.getWidth();

        int w = Images.bigBackArrow.getWidth();
        int h = Images.bigBackArrow.getHeight();

        actions = new ArrayList<>();

        actions.add(new ExtendableButton(this::reloadPorts, "ОБНОВИТЬ СПИСОК ПОРТОВ", timeMarkXPos,
                PC_part.SACK_pc_client.Controls.Menu.topMargin + (2 + actions.size()) * (PC_part.SACK_pc_client.Controls.Menu.itemHeight + 20),
                w, h));

        actions.add(new ExtendableButton(() -> {
            for (ExtendableCheckbox<String> es : portsRects)
                if (es.getSelected()) {
                    DataWrapper.connect(es.getValue());
                    break;
                }

        }, "СОЕДИНИТЬСЯ С ЭТИМ", timeMarkXPos,
                PC_part.SACK_pc_client.Controls.Menu.topMargin + (2 + actions.size()) * (PC_part.SACK_pc_client.Controls.Menu.itemHeight + 20),
                w, h));

        actions.add(new ExtendableButton(DataWrapper::disconnect

                , "ОТСОЕДИНИТЬСЯ", timeMarkXPos,
                PC_part.SACK_pc_client.Controls.Menu.topMargin + (2 + actions.size()) * (PC_part.SACK_pc_client.Controls.Menu.itemHeight + 20),
                w, h));

        reloadPorts();
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(UICanvas.lightBackgroundColor);
        g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);

        for (ExtendableCheckbox<String> port : portsRects) {
            port.draw(g2);
        }

        for (ExtendableButton eb : actions)
            eb.draw(g2);

        g2.setColor(UICanvas.darkFontColor);
        g2.setFont(UICanvas.font);
        g2.drawString(DataWrapper.getConnectionState(), PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45, UICanvas.windowHeight - 50);
    }

    @Override
    public void mouseClick(int x, int y) {

        for (ExtendableCheckbox<String> port : portsRects) {
            port.click(x, y);
        }

        for (ExtendableButton eb : actions)
            eb.click(x, y);
    }
}
