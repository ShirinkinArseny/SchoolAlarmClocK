package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Configurable.Design;
import PC_part.SACK_pc_client.Controls.ConditionedButtonPanel;
import PC_part.SACK_pc_client.Controls.ExtendableButton;
import PC_part.SACK_pc_client.Controls.ExtendableCheckbox;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.Resources.Images;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class ActivityWithButtons<T> implements Activity {

    private final ArrayList<ExtendableButton> actions=new ArrayList<>();
    private final ArrayList<ExtendableCheckbox<T>> checkboxes = new ArrayList<>();
    private final int checkBoxesYOffset;
    private int selectedItemsNumber=0;

    public boolean getSelectedItemsExists() {
        return selectedItemsNumber!=0;
    }

    private ConditionedButtonPanel cbt=null;

            public void setConditionedButtonPanel(ConditionedButtonPanel cbt ) {
                this.cbt=cbt;
            }

    ActivityWithButtons(int checkBoxesYOffset) {
        this.checkBoxesYOffset = checkBoxesYOffset;
        reloadCheckboxes();
    }

    void addButton(Runnable r, String title) {
        int w= Images.bigBackArrow.getWidth();
        int h=Images.bigBackArrow.getHeight();
        int timeMarkXPos= UICanvas.windowWidth- Images.bigBackArrow.getWidth();

        actions.add(new ExtendableButton(r, title, timeMarkXPos,
                PC_part.SACK_pc_client.Controls.Menu.topMargin + (2 + actions.size()) * (PC_part.SACK_pc_client.Controls.Menu.itemHeight + 10),
                w, h));
    }

    void addCheckBox(T data) {
        checkboxes.add(new ExtendableCheckbox<>(data, (checkboxes.size() / 14) * 160 + PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45,
                checkboxes.size() % 14 * 30 + PC_part.SACK_pc_client.Controls.Menu.topMargin + checkBoxesYOffset,
                155, 25));
    }

    protected abstract void loadCheckBoxes();

    void reloadCheckboxes() {
        checkboxes.clear();
        loadCheckBoxes();
        updateSelectedItemsNumber();
    }

    Stream<T> getSelectedItems() {
        return checkboxes.stream()
                .filter(ExtendableCheckbox::getSelected)
                .map(ExtendableCheckbox::getValue);
    }

    Stream<T> getNotSelectedItems() {
        return checkboxes.stream()
                .filter(item -> !item.getSelected())
                .map(ExtendableCheckbox::getValue);
    }

    void dropSelection() {
        checkboxes.stream().filter(ExtendableCheckbox::getSelected).forEach(ExtendableCheckbox::unselect);
        updateSelectedItemsNumber();
    }

    private void updateSelectedItemsNumber() {
        selectedItemsNumber=0;
        checkboxes.stream().filter(ExtendableCheckbox::getSelected).forEach(rect -> selectedItemsNumber++);

    }

    public void mouseClick(int x, int y) {
        for (ExtendableButton eb: actions)
            eb.click(x, y);
        for (ExtendableCheckbox<T> rect : checkboxes) {
            rect.click(x, y);
        }
        updateSelectedItemsNumber();
        if (cbt!=null)
            cbt.mouseClick(x, y);

    }


    public void draw(Graphics2D g2) {
        g2.setColor(Design.lightBackgroundColor);
        g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);

        g2.setFont(Design.fontSmall);
        for (ExtendableCheckbox<T> rect : checkboxes) {
            rect.draw(g2);
        }

        for (ExtendableButton eb: actions)
            eb.draw(g2);


        if (cbt!=null)
            cbt.draw(g2);

        UICanvas.longOperationWaiter.draw(g2);
    }

}
