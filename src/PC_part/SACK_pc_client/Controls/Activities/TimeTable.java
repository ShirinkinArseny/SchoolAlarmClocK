package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.Menu;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Dialogs.AddRingDialogue;
import PC_part.SACK_pc_client.Ring;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TimeTable extends ActivityWithButtons<Ring> {



    private static final ArrayList<Ring> copiedItems = new ArrayList<>();

    private int weekday = -1;

    public TimeTable(int weekDay) {

        super(Menu.topMargin + Menu.itemHeight * 2 + 10);

        weekday = weekDay;
        reloadCheckboxes();

        addButton(() -> new AddRingDialogue(integer -> {
            Ring r = new Ring(integer);
            DataWrapper.addRing(weekDay, r);
            reloadCheckboxes();
        }), Labels.addOneMoreRing);

        addButton(() -> {

                    DataWrapper.setRings(weekDay, getNotSelectedItems()
                            .collect(Collectors.toCollection(ArrayList::new)));

                    reloadCheckboxes();
                },
                Labels.removeSelected);

        addButton(() -> {
            copiedItems.clear();
            getSelectedItems().forEach(copiedItems::add);
        }, Labels.copySelected);

        addButton(() -> {

            DataWrapper.addRings(weekDay, copiedItems);
            reloadCheckboxes();

        }, Labels.insert);


        addButton(this::dropSelection, Labels.dropSelection);

        addButton(() -> {

            if (DataWrapper.getIsConnected()) {


                UICanvas.longOperationWaiter.lock();
                new Thread(() -> {
                    DataWrapper.pop();
                    reloadCheckboxes();
                    UICanvas.longOperationWaiter.unlock();
                }).start();

            } else {
                DataWrapper.processError(Labels.cannotPerformOperationCuzNoConnection);
            }

        }, Labels.getRingsFromDuino);

        addButton(() -> {
            if (DataWrapper.getIsConnected()) {


                UICanvas.longOperationWaiter.lock();
                new Thread(() -> {
                    DataWrapper.push();
                    UICanvas.longOperationWaiter.unlock();
                }).start();

            } else
                DataWrapper.processError(Labels.cannotPerformOperationCuzNoConnection);
        }, Labels.writeRingsToDuino);
    }

    @Override
    public void loadCheckBoxes() {
        if (weekday != -1) {
           ArrayList<Ring> rings = DataWrapper.getRings(weekday);
           rings.forEach(this::addCheckBox);
        }
    }


}