package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.ConditionedButtonPanel;
import PC_part.SACK_pc_client.Controls.Menu;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Dialogs.AddRingDialogue;
import PC_part.SACK_pc_client.Ring;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TimeTable extends ActivityWithButtons<Ring> {


    private static final ArrayList<Ring> copiedItems = new ArrayList<>();

    private int weekday = -1;

    private void loadRingsFromDuino() {
        DataWrapper.pop();
        reloadCheckboxes();
    }

    private void insertCopied() {
        DataWrapper.addRings(weekday, copiedItems);
        reloadCheckboxes();
    }

    private void copySelectedItems() {
        copiedItems.clear();
        getSelectedItems().forEach(copiedItems::add);
    }

    private void removeSelectedItems() {
        DataWrapper.setRings(weekday, getNotSelectedItems()
                .collect(Collectors.toCollection(ArrayList::new)));

        reloadCheckboxes();
    }

    private void addNewRing(int time) {
        Ring r = new Ring(time);
        DataWrapper.addRing(weekday, r);
        reloadCheckboxes();
    }

    public TimeTable(int weekDay) {

        super(Menu.topMargin + Menu.itemHeight * 2 + 10);

        setConditionedButtonPanel(
                new ConditionedButtonPanel(
                        new Runnable[]{this::removeSelectedItems, this::copySelectedItems, this::dropSelection},
                        new String[]{Labels.removeSelected, Labels.copySelected, Labels.dropSelection}, this::getSelectedItemsExists));

        weekday = weekDay;
        reloadCheckboxes();

        addButton(() -> new AddRingDialogue(this::addNewRing), Labels.addOneMoreRing);

        addButton(this::insertCopied, Labels.insert);

        addButton(() -> UICanvas.longOperationWaiter.processIfConnected(this::loadRingsFromDuino), Labels.getRingsFromDuino);

        addButton(() -> UICanvas.longOperationWaiter.processIfConnected(DataWrapper::push), Labels.writeRingsToDuino);
    }

    @Override
    public void loadCheckBoxes() {
        if (weekday != -1) {
            ArrayList<Ring> rings = DataWrapper.getRings(weekday);
            rings.forEach(this::addCheckBox);
        }
    }
}