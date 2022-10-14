package org.cnpem.fitotron.table;

import org.cnpem.fitotron.file.FitotronData;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class EditableDoubleTableCell extends TableCell<FitotronData, Double> {

    //----------------------------------------------------------------------------------------------------

    private TextField textField;
    private int minDecimals, maxDecimals;

    //----------------------------------------------------------------------------------------------------

    public EditableDoubleTableCell (int min, int max) {
        minDecimals = min;
        maxDecimals = max;
    }

    //----------------------------------------------------------------------------------------------------

    @Override
    public void startEdit() {
        if(editableProperty().get()){
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.requestFocus();
            }
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem() != null ? getItem().toString() : null);
        setGraphic(null);
    }

    @Override
    public void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if(empty){
            setText(null);
            setGraphic(null);
        }else{
            if(isEditing()) {
                if(textField != null) {
                    textField.setText(getString());
                    textField.selectAll();
                }
                setText(null);
                setGraphic(textField);
            }else{
                setText(getString());
                setGraphic(null);
            }
        }
    }

    @Override
    public void commitEdit(Double item) {

        if(isEditing()){

            super.commitEdit(item);

        }else{

            final TableView<FitotronData> table = getTableView();

            if (table != null) {

                TablePosition<FitotronData, Double> position = new TablePosition<FitotronData, Double>(
                        getTableView(), getTableRow().getIndex(), getTableColumn()
                );

                TableColumn.CellEditEvent<FitotronData, Double> editEvent =
                        new TableColumn.CellEditEvent<FitotronData, Double>(
                                table, position,TableColumn.editCommitEvent(), item
                        );

                Event.fireEvent(getTableColumn(), editEvent);
            }

            updateItem(item, false);

            if(table != null){
                table.edit(-1, null);
            }
        }
    }

    //----------------------------------------------------------------------------------------------------

    private void createTextField(){

        textField = new TextField();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(minDecimals);
        numberFormat.setMaximumFractionDigits(maxDecimals);
        DecimalFormat format = (DecimalFormat) numberFormat;

        textField.setTextFormatter(new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()){
                return c;
            }
            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = format.parse( c.getControlNewText(), parsePosition );

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()){
                return null;
            }else{
                return c;
            }
        }));

        textField.setText(getString());

        textField.setOnAction(evt -> {
            if(textField.getText() != null && !textField.getText().isEmpty()){
                commitEdit(Double.valueOf(textField.getText()));
            }
        });

        textField.setOnKeyPressed((ke) -> {
            if (ke.getCode().equals(KeyCode.ESCAPE)) {
                cancelEdit();
            }
        });

        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setAlignment(Pos.CENTER_RIGHT);

        this.setAlignment(Pos.CENTER_RIGHT);

        textField.selectAll();
    }

    private String getString() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(minDecimals);
        numberFormat.setMaximumFractionDigits(maxDecimals);
        return getItem() == null ? "" : numberFormat.format(getItem());
    }
}


