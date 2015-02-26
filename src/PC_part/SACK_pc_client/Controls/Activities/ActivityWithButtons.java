package PC_part.SACK_pc_client.Controls.Activities;

import PC_part.SACK_pc_client.Controls.ExtendableButton;
import PC_part.SACK_pc_client.Controls.ExtendableCheckbox;
import PC_part.SACK_pc_client.Controls.UICanvas;
import PC_part.SACK_pc_client.Resources.Images;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public abstract class ActivityWithButtons<T> implements Activity {

    private ArrayList<ExtendableButton> actions=new ArrayList<>();
    private ArrayList<ExtendableCheckbox<T>> checkboxes = new ArrayList<>();
    private int checkBoxesYOffset;

    public ActivityWithButtons(int checkBoxesYOffset) {
        this.checkBoxesYOffset = checkBoxesYOffset;
        reloadCheckboxes();
    }

    private static final int timeMarkXPos= UICanvas.windowWidth- Images.bigBackArrow.getWidth();
    public void addButton(Runnable r, String title) {
        int w= Images.bigBackArrow.getWidth();
        int h=Images.bigBackArrow.getHeight();

        actions.add(new ExtendableButton(r, title, timeMarkXPos,
                PC_part.SACK_pc_client.Controls.Menu.topMargin + (2 + actions.size()) * (PC_part.SACK_pc_client.Controls.Menu.itemHeight + 20),
                w, h));
    }

    public void addCheckBox(T data) {
        checkboxes.add(new ExtendableCheckbox<>(data, (checkboxes.size() / 14) * 160 + PC_part.SACK_pc_client.Controls.Menu.itemWidth + 45,
                checkboxes.size() % 14 * 30 + PC_part.SACK_pc_client.Controls.Menu.topMargin + checkBoxesYOffset,
                155, 25));
    }

    public abstract void loadCheckBoxes();

    public void reloadCheckboxes() {
        checkboxes.clear();
        loadCheckBoxes();
    }

    public Stream<T> getSelectedItems() {
        return checkboxes.stream()
                .filter(ExtendableCheckbox::getSelected)
                .map(ExtendableCheckbox::getValue);
    }

    public Stream<T> getNotSelectedItems() {
        return checkboxes.stream()
                .filter(item -> !item.getSelected())
                .map(ExtendableCheckbox::getValue);
    }

    public void dropSelection() {
        checkboxes.stream().filter(ExtendableCheckbox::getSelected).forEach(ExtendableCheckbox::unselect);
    }

    public void mouseClick(int x, int y) {
        for (ExtendableCheckbox<T> rect : checkboxes) {
            rect.click(x, y);
        }
        for (ExtendableButton eb: actions)
            eb.click(x, y);
    }


    public void draw(Graphics2D g2) {
        g2.setColor(UICanvas.lightBackgroundColor);
        g2.fillRect(0, 0, UICanvas.windowWidth, UICanvas.windowHeight);

        g2.setFont(UICanvas.fontSmall);
        for (ExtendableCheckbox<T> rect : checkboxes) {
            rect.draw(g2);
        }

        for (ExtendableButton eb: actions)
            eb.draw(g2);

    }

}
