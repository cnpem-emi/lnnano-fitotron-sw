package org.cnpem.fitotron.measurement;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class MeasurementField extends HBox{

    private Label title, unit;
    private TextField textField;
    private BooleanProperty isEmpty;

    public MeasurementField(String title, String unit){

        super();

        //Inicializando os objetos da interface do campo de texto/numérico:
        this.title = new Label(title);
        this.textField = new TextField();
        this.unit = new Label(unit);
        this.isEmpty = new SimpleBooleanProperty(true);

        //Definindo a largura campo de texto/numérico:
        this.setAlignment(Pos.BASELINE_CENTER);
        this.setWidth(280);
        textField.setMinWidth(100);

        //Configurando a caixa de texto para lhe dar apenas com números:
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setGroupingUsed(false);
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

        //Definindo o alinhamento dos valores dentro dos objetos do campo de texto/numérico:
        this.title.setAlignment(Pos.BASELINE_CENTER);
        this.textField.setAlignment(Pos.BASELINE_RIGHT);
        this.unit.setAlignment(Pos.BASELINE_CENTER);

        //Definindo a aparência do campo de texto/numérico:
        this.title.setStyle("-fx-background: #FFFFFF;");
        this.textField.setStyle("-fx-background: #FFFFFF;");
        this.unit.setStyle("-fx-background: #FFFFFF;");

        ///Adicionando os objetos filhos à inteface e definindo as suas margens e suas propriedades de redimensionamento:

        this.getChildren().add(this.title);
        HBox.setHgrow(this.title, Priority.NEVER);
        HBox.setMargin(this.title,new Insets(11,8,5,8));

        this.getChildren().add(this.textField);
        HBox.setHgrow(this.textField, Priority.ALWAYS);
        HBox.setMargin(this.textField,new Insets(8,0,8,0));

        this.getChildren().add(this.unit);
        HBox.setHgrow(this.unit, Priority.NEVER);
        HBox.setMargin(this.unit,new Insets(11,8,5,8));

        //Inicializando os Listeners do campo numérico:

        textField.setOnKeyTyped(keyEvent -> {
            isEmpty.setValue(textField.getText().isEmpty());
            keyEvent.consume();
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(textField.isFocused())
                Platform.runLater(() -> textField.selectAll());
        });
    }

    public double getValue(){
        return Double.parseDouble(textField.getText());
    }

    public void setValue(double value){
        textField.setText(String.valueOf(value));
    }

    public void setFieldChangeListener(ChangeListener<Boolean> changeListener){
        isEmpty.addListener(changeListener);
    }
}
