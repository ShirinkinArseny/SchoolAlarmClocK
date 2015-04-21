package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.Common.Logger;
import PC_part.SACK_pc_client.Configurable.Labels;
import PC_part.SACK_pc_client.Controls.ConditionedButtonPanel;
import PC_part.SACK_pc_client.Controls.Menu;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.DataWrapper;
import PC_part.SACK_pc_client.Dialogs.AddRingDialogue;
import PC_part.SACK_pc_client.Ring;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
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

        super(Menu.topMargin + Menu.itemHeight * 2 + 10, true);

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

        addButton(this::saveOnDisk, Labels.saveOnDisk);

        addButton(this::loadFromDisk, Labels.loadFromDisk);
    }

    private static byte[] readLine(File f) throws Exception {
        ByteArrayOutputStream ous;
        InputStream ios = new FileInputStream(f);

        byte[] buffer = new byte[4096];
        ous = new ByteArrayOutputStream();
        int read;
        try {
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ios.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ous.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] b=ous.toByteArray();
        if (b.length==0) {
            Logger.logError(TimeTable.class, "File length is 0!");
            throw new Exception("File length is 0!");
        }

        return b;
    }

    private void loadFromDisk() {
        JFileChooser jfc=new JFileChooser();
        jfc.setLocation(UICanvas.clickX, UICanvas.clickY);
        jfc.showOpenDialog(null);

        File f=jfc.getSelectedFile();
        if (f!=null) {
            byte[] res;
            try {
                res = readLine(f);
                DataWrapper.deSerializeTable(res);
                reloadCheckboxes();
            } catch (Exception e) {
                DataWrapper.processError(Labels.cannotReadFile);
                e.printStackTrace();
            }
        }
    }

    private void saveOnDisk() {
        JFileChooser jfc=new JFileChooser();
        jfc.setLocation(UICanvas.clickX, UICanvas.clickY);
        jfc.showSaveDialog(null);

        File f=jfc.getSelectedFile();
        if (f!=null) {
            FileOutputStream fw = null;
            try {
                fw = new FileOutputStream(f);
                fw.write(DataWrapper.getSerializedTable());
            } catch (IOException e) {
                DataWrapper.processError(Labels.cannotWriteToFile);
                e.printStackTrace();
            } finally {
                if (fw!=null)
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

    }

    @Override
    public void loadCheckBoxes() {
        if (weekday != -1) {
            ArrayList<Ring> rings = DataWrapper.getRings(weekday);
            rings.forEach(this::addCheckBox);
        }
    }
}