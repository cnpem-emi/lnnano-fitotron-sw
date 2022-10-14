package org.cnpem.fitotron.list;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class FitotronSeriesListItem extends HBox {

    private CheckBox checkBox;
    private Label label;
    private ColorPicker colorPicker;

    public FitotronSeriesListItem(boolean isSelected, Color color, String title){
        super();

        this.setAlignment(Pos.CENTER_LEFT);

        this.checkBox = new CheckBox();
        this.colorPicker = new ColorPicker();
        this.label = new Label();

        this.checkBox.setSelected(isSelected);
        this.colorPicker.setValue(color);
        this.label.setText(title);

        this.getChildren().add(checkBox);
        HBox.setHgrow(checkBox, Priority.NEVER);
        HBox.setMargin(checkBox,new Insets(0,8,0,8));

        this.getChildren().add(colorPicker);
        HBox.setHgrow(colorPicker, Priority.NEVER);
        HBox.setMargin(colorPicker,new Insets(0,8,0,8));

        this.getChildren().add(label);
        HBox.setHgrow(label, Priority.ALWAYS);
        HBox.setMargin(label,new Insets(0,0,0,0));

        colorPicker.setPrefWidth(45);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public ColorPicker getColorPicker(){
        return colorPicker;
    }

    public String getSeriesTitle() {
        return label.getText();
    }
}
