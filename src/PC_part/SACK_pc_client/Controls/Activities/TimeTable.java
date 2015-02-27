package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Controls.Menu;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Dialogs.AddRingDialogue;
import PC_part.SACK_pc_client.Ring;
import PC_part.SACK_server_pc_part.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TimeTable extends ActivityWithButtons<Ring> {

    private static final ArrayList<Ring> copiedItems=new ArrayList<>();

    private int weekday=-1;

    public TimeTable(int weekDay) {

        super(Menu.topMargin+Menu.itemHeight*2+10);

        weekday=weekDay;
        reloadCheckboxes();

        addButton(() -> new AddRingDialogue(integer -> {
            Ring r=new Ring(integer);
            DataWrapper.addRing(weekDay, r);
            reloadCheckboxes();
        }), "ДОБАВИТЬ ЕЩЁ ОДИН");

        addButton(() -> {

                    DataWrapper.setRings(weekDay, getNotSelectedItems()
                                .collect(Collectors.toCollection(ArrayList::new)));

                    reloadCheckboxes();
                },
                "УДАЛИТЬ ВЫДЕЛЕННЫЕ");

        addButton(() -> {
            copiedItems.clear();
            getSelectedItems().forEach(copiedItems::add);
        }, "КОПИРОВАТЬ ВЫДЕЛЕННЫЕ");

        addButton(() -> {

            DataWrapper.addRings(weekDay, copiedItems);
            reloadCheckboxes();

        }, "ВСТАВИТЬ");


        addButton(this::dropSelection, "СБРОСИТЬ ВЫДЕЛЕНИЕ");

        addButton(() -> {

            DataWrapper.pop();
            reloadCheckboxes();

        }, "ВЗЯТЬ С ДУИНЫ");

        addButton(DataWrapper::push, "ЗАЛИТЬ ВСЁ НА ДУИНУ");
    }

    @Override
    public void loadCheckBoxes() {
        if (weekday!=-1) {
            ArrayList<Ring> rings = DataWrapper.getRings(weekday);
            rings.forEach(this::addCheckBox);
        }
    }


}