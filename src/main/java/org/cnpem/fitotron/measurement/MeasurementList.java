package org.cnpem.fitotron.measurement;

import javafx.scene.control.ListView;

public class MeasurementList extends ListView<MeasurementField>{

    private String unit;
    private int listSize;
    private int emptyFields;

    public MeasurementList(int initialSize, String unit){
        super();

        this.unit = unit;
        this.listSize = initialSize;
        this.emptyFields = initialSize;

        this.setStyle("-fx-selection-bar:white; -fx-selection-bar-non-focused: white;");

        this.getStylesheets().add(
                getClass().getResource("/org/cnpem/fitotron/styles/measurementListStyle.css").toExternalForm());

        for (int i = 0; i<initialSize; i++){

            String measurementFieldTitle = String.format("Medida #%d", i+1);

            MeasurementField field = new MeasurementField(measurementFieldTitle,unit);
            field.setFieldChangeListener((observableValue, aBoolean, t1) -> {

                emptyFields = (t1) ? emptyFields+1 : emptyFields-1;

                if(emptyFields==0){
                    MeasurementController.okButtonNode.setDisable(false);
                }else{
                    MeasurementController.okButtonNode.setDisable(true);
                }
            });

            this.getItems().add(field);
        }
    }

    public void changeListSize(int newSize){
        this.listSize = newSize;
        this.getChildren().clear();
        for (int i = 0; i<newSize; i++){
            String measurementFieldTitle = String.format("Medida #%d", i+1);
            this.getItems().add(new MeasurementField(measurementFieldTitle,unit));
        }
    }

    public double[] getValues(){
        double[] values = new double[listSize];
        for (int i=0; i<listSize;i++)
            values[i] = this.getItems().get(i).getValue();
        return values;
    }
}
