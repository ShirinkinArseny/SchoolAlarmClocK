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
    private int delta=-1;
    private int cts=0;

    private void getTimeFromDuino() {
                time = DataWrapper.getDuinoTime();
                delta= (int) (System.currentTimeMillis()/1000-time);
    }

    private void writeLocalTimeToDuino() {
        DataWrapper.setTime();
        getTimeFromDuino();
    }

    public TimeSync() {
        super(0);

        addButton(() -> UICanvas.longOperationWaiter.processIfConnected(this::getTimeFromDuino), Labels.getTimeFromDuino);
        addButton(() -> UICanvas.longOperationWaiter.processIfConnected(this::writeLocalTimeToDuino), Labels.writeLocalTime);

        if (DataWrapper.getIsConnected()) {
            UICanvas.longOperationWaiter.processOperation(this::getTimeFromDuino);
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
            g2.drawString("≈"+Ring.getHumanTime((int)(System.currentTimeMillis()/1000-delta)%(3600*24)), Menu.itemWidth + 65, Menu.topMargin + Menu.itemHeight * 3 + 65);
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
