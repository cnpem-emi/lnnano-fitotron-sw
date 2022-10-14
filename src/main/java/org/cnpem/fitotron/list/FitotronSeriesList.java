package org.cnpem.fitotron.list;

import org.cnpem.fitotron.file.FitotronData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.paint.Color;

import java.util.*;

public class FitotronSeriesList extends ListView<FitotronSeriesListItem>{

    private int selectedItemIndex;

    private StringProperty selectedSeries;
    private ObservableMap<String,ArrayList<FitotronData>> dataHashMap;
    private ObservableMap<String,Color> seriesColorsHashMap;
    private ObservableMap<String,Boolean> seriesVisibilitiesHashMap;

    public FitotronSeriesList(
            StringProperty selectedSeries,
            ObservableMap<String,ArrayList<FitotronData>> dataHashMap,
            ObservableMap<String,Color> seriesColorsHashMap,
            ObservableMap<String,Boolean> seriesVisibilitiesHashMap
    ){
        super();

        this.selectedSeries = selectedSeries;
        this.dataHashMap = dataHashMap;
        this.seriesColorsHashMap = seriesColorsHashMap;
        this.seriesVisibilitiesHashMap = seriesVisibilitiesHashMap;

        //Desabilitando a lista de séries:
        this.setDisable(true);

        //Definindo a largura e a altura da lista de séries:
        this.setPrefWidth(300);
        this.setPrefHeight(350);

        //Definindo o modo de seleção como simples (um de cada vez):
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void initialize(){

        this.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue!=null)
                selectedSeries.setValue(newValue.getSeriesTitle());
        });

        dataHashMap.addListener((MapChangeListener<String, ArrayList<FitotronData>>) change -> {
            if(change.wasRemoved()){
                selectedItemIndex = this.getSelectionModel().getSelectedIndex();
                removeListItem(change.getKey());
            }else if(change.wasAdded()){
                addListItem(change.getKey());
            }
        });
    }

    public void start(){

        //Habilitando a lista de séries caso esteja desabilitada:
        if(this.isDisable())
            this.setDisable(false);

        //Selecionando o primeiro ítem:
        this.getSelectionModel().selectFirst();
    }

    public void removeListItem(String key){

        seriesColorsHashMap.remove(key);
        seriesVisibilitiesHashMap.remove(key);

        for(int i=0; i<this.getItems().size(); i++){
            if(this.getItems().get(i).getSeriesTitle().equals(key)){
                this.getItems().remove(i);
            }
        }
    }

    public void addListItem(String key){

        Color randomColor = Color.color(Math.random(),Math.random(),Math.random());
        boolean defaultVisibility = true;

        FitotronSeriesListItem listItem = new FitotronSeriesListItem(defaultVisibility, randomColor, key);

        listItem.getCheckBox().addEventHandler(
                ActionEvent.ACTION,
                event -> setSeriesVisibility(listItem.getSeriesTitle(),listItem.getCheckBox().isSelected())
        );

        listItem.getColorPicker().addEventHandler(
                ActionEvent.ACTION,
                event -> setSeriesColor(listItem.getSeriesTitle(),listItem.getColorPicker().getValue())
        );

        //Passando os novos valores de cor e visibilidade da série para os seus respectivos HashMaps:
        seriesColorsHashMap.put(key,randomColor);
        seriesVisibilitiesHashMap.put(key,defaultVisibility);

        this.getItems().add(listItem);

        Collections.sort(this.getItems(), new Comparator<FitotronSeriesListItem>() {
            @Override
            public int compare(FitotronSeriesListItem o1, FitotronSeriesListItem o2) {
                double d1 = Double.parseDouble(o1.getSeriesTitle().replaceAll("[^\\d.]", ""));
                double d2 = Double.parseDouble(o2.getSeriesTitle().replaceAll("[^\\d.]", ""));

                if((d1-d2)>0){
                    return 1;
                }else if ((d1-d2)<0){
                    return -1;
                }else{
                    return 0;
                }
            }
        });

        this.getSelectionModel().select(selectedItemIndex);
    }

    private void setSeriesVisibility(String key, boolean isVisible){
        seriesVisibilitiesHashMap.put(key,isVisible);
    }

    private void setSeriesColor(String key, Color value) {
        seriesColorsHashMap.put(key,value);
    }
}
