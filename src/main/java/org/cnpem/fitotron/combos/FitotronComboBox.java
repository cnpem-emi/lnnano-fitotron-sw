package org.cnpem.fitotron.combos;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.ComboBox;

public class FitotronComboBox extends ComboBox {

    private IntegerProperty chartIndex;
    private ObservableList<String> chartTitles;

    public FitotronComboBox(
            IntegerProperty chartIndex,
            ObservableList<String> chartTitles
    ){
        super();

        this.chartIndex = chartIndex;
        this.chartTitles = chartTitles;

        //Desabilitando a caixa de seleção:
        this.setDisable(true);

        //Definindo a largura da caixa de seleção:
        setWidth(320);
    }

    public void initialize(){

        //Adicionando a lista de opções da caixa de seleção:
        this.setItems(chartTitles);


        this.setOnAction(event -> {
            this.setValue(this.getSelectionModel().getSelectedItem());
            chartIndex.set(this.getSelectionModel().getSelectedIndex());
            event.consume();
        });
    }

    public void start(){

        //Habilitando o campo caso esteja desabilitada:
        if(this.isDisable())
            this.setDisable(false);

        //Selecionando o primeiro ítem:
        this.getSelectionModel().selectLast();
        this.fireEvent(new Event(ActionEvent.ACTION));
        this.getSelectionModel().selectFirst();
        this.fireEvent(new Event(ActionEvent.ACTION));
    }
}
