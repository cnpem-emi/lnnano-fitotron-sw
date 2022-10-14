package org.cnpem.fitotron.measurement;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class MeasurementController {

    @FXML
    public VBox content_VBox;

    private MeasurementList measurementList;

    protected static Node okButtonNode;

    public void setList(String unit, int initialSize){
        measurementList = new MeasurementList(initialSize,unit);
        content_VBox.getChildren().add(measurementList);
    }

    public static void setOkButtonReference(Node buttonNode) {
        okButtonNode = buttonNode;
        okButtonNode.setDisable(true);
    }

    public double[] getValues(){
        return measurementList.getValues();
    }
}
