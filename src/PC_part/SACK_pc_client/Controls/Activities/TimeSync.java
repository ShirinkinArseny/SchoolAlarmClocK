package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.*;
import PC_part.SACK_pc_client.Controls.Menu;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Ring;

import java.awt.*;
import java.text.SimpleDateFormat;

public class TimeSync extends ActivityWithButtons {

    private int time=-1;

    private void syncTime() {
        if (DataWrapper.getIsConnected()) {

            UICanvas.longOperationWaiter.lock();
            new Thread(() -> {
                time = DataWrapper.getDuinoTime();
                UICanvas.longOperationWaiter.unlock();
            }).start();
        } else {
            DataWrapper.processError(Labels.cannotPerformOperationCuzNoConnection);
        }
    }

    public TimeSync() {
        super(0);

        addButton(this::syncTime, Labels.getTimeFromDuino);
        addButton(() -> {
            if (DataWrapper.getIsConnected()) {


                UICanvas.longOperationWaiter.lock();
                new Thread(() -> {
                    DataWrapper.setTime();
                    syncTime();
                    UICanvas.longOperationWaiter.unlock();
                }).start();

            } else {
                DataWrapper.processError(Labels.cannotPerformOperationCuzNoConnection);
            }
        }, Labels.writeLocalTime);

        if (DataWrapper.getIsConnected()) {
            new Thread(this::syncTime).start();
        }
    }

    private static final SimpleDateFormat hms=new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat dmy=new SimpleDateFormat("dd.MM.yyyy");

    public void draw(Graphics2D g2) {
        super.draw(g2);


        g2.setColor(Design.darkFontColor);
        g2.setFont(Design.fontSmall);


        if (DataWrapper.getIsConnected()) {
            g2.drawString(Labels.duinoTime, Menu.itemWidth + 65, Menu.topMargin + Menu.itemHeight * 3 + 25);
            g2.drawString(Ring.getHumanTime(time), Menu.itemWidth + 65, Menu.topMargin + Menu.itemHeight * 3 + 65);
        }

        g2.setFont(Design.font);
        g2.drawString(Labels.localTime,
                Menu.itemWidth + 65, Menu.topMargin + Menu.itemHeight * 7 + 15);
        g2.drawString(hms.format(new java.util.Date()),
                Menu.itemWidth + 65, Menu.topMargin+Menu.itemHeight*8+15);
        g2.drawString(dmy.format(new java.util.Date()),
                Menu.itemWidth + 65, Menu.topMargin+Menu.itemHeight*9+15);
    }

    @Override
    public void loadCheckBoxes() {

    }
}
