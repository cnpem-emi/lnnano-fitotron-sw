package org.cnpem.fitotron.chart;

import org.cnpem.fitotron.PlotterController;
import org.cnpem.fitotron.file.FitotronData;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.stage.Popup;

public class PointFlyout extends Label {

    private Popup popup;
    private StringBuilder stringBuilder;

    public PointFlyout(FitotronData data){
        super();

        this.popup = new Popup();
        this.stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("Horário.....: %s\n",
                data.getDateTime().toString().replace("T"," / ")));

        stringBuilder.append(String.format("Tempo.......: %.1f [min]\n",
                data.getTime()));

        stringBuilder.append(String.format("Frequência..: %.3f [Hz]\n",
                data.getFrequency()));

        stringBuilder.append(String.format("Impedância..: %1.3E ± %1.3E [Ω]\n",
                data.getImpedanceMeasure().getAverage(),data.getImpedanceMeasure().getError()));

        stringBuilder.append(String.format("Temperatura.: %.2f ± %.2f [ºC]\n",
                data.getTemperatureMeasure().getAverage(),data.getTemperatureMeasure().getError()));

        stringBuilder.append(String.format("Umidade.....: %.2f ± %.2f [%%]\n",
                data.getHumidityMeasure().getAverage(),data.getHumidityMeasure().getError()));

        stringBuilder.append(String.format("Massa.......: %.2f ± %.2f [mg]\n",
                data.getMassMeasure().getAverage(),  data.getMassMeasure().getError()));

        stringBuilder.append(String.format("RWC.........: %.2f ± %.2f [%%]\n",
                data.getRwcMeasure().getAverage(),data.getRwcMeasure().getError()));

        stringBuilder.append(String.format("LWC.........: %.2f ± %.2f [%%]\n",
                data.getLwcMeasure().getAverage(),data.getLwcMeasure().getError()));

        stringBuilder.append(String.format("Confiança...: %.0f [%%]\n", FitotronData.getConfidence()));

        Label label = new Label(stringBuilder.toString());
        label.setPadding(new Insets(5));
        label.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-font-family: consolas");

        popup.getContent().add(label);
        popup.setAutoFix(true);

        this.setPrefWidth(10);
        this.setPrefHeight(10);

        this.setOnMouseEntered(PlotterController.showStatistics);
        this.setOnMouseExited(PlotterController.showStatistics);
    }

    public Popup getPopup(){
        return popup;
    }
}
