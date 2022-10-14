package org.cnpem.fitotron.chart;

import org.cnpem.fitotron.file.FitotronData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.*;

public class FitotronChart extends StackPane {

    enum ChartType {
        X_LIN_Y_LIN,
        X_LIN_Y_LOG,
        X_LOG_Y_LOG
    }

    public static final int DEFAUT_POINT_APPEARANCE = 0;
    public static final int DEFAUT_CI_APPEARANCE = 0;

    private boolean isLinear;
    private ArrayList<Double> xAxisValues;
    private ArrayList<Double> yAxisValues;

    private ObservableList<String> chartTitles;
    private IntegerProperty chartIndex;
    private IntegerProperty pointAppearance;
    private IntegerProperty ciAppearance;
    private StringProperty confidence;
    private ObservableList<FitotronData> dataArrayList;
    private ObservableMap<String,ArrayList<FitotronData>> dataHashMap;
    private ObservableMap<String,Color> seriesColorsHashMap;
    private ObservableMap<String,Boolean> seriesVisibilitiesHashMap;

    private HashMap<String, XYChart.Series<Double,Double>> seriesHashMap;
    private HashMap<String, XYChart.Series<Double,Double>> upperBoundSeriesHashMap;
    private HashMap<String, XYChart.Series<Double,Double>> lowerBoundSeriesHashMap;

    private ValueAxis xAxis,yAxis;
    private LineChart lineChart;

    public FitotronChart(
            ObservableList<String> chartTitles,
            IntegerProperty chartIndex,
            StringProperty confidence,
            ObservableList<FitotronData> dataArrayList,
            ObservableMap<String,ArrayList<FitotronData>> dataHashMap,
            ObservableMap<String, Color> seriesColorsHashMap,
            ObservableMap<String,Boolean> seriesVisibilitiesHashMap,
            IntegerProperty pointAppearance,
            IntegerProperty ciAppearance
    ){
        super();

        this.pointAppearance = pointAppearance;
        this.ciAppearance = ciAppearance;

        this.chartTitles = chartTitles;
        this.chartIndex = chartIndex;
        this.confidence = confidence;
        this.dataArrayList = dataArrayList;
        this.dataHashMap = dataHashMap;
        this.seriesColorsHashMap = seriesColorsHashMap;
        this.seriesVisibilitiesHashMap = seriesVisibilitiesHashMap;

        isLinear = true;
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        lineChart = new LineChart(xAxis,yAxis);

        this.getChildren().add(lineChart);

        lineChart.paddingProperty().setValue(new Insets(17,17,0,3));
        lineChart.setLegendVisible(false);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        lineChart.setDisable(true);
    }

    public void initialize(){

        seriesHashMap = new HashMap<>();
        upperBoundSeriesHashMap = new HashMap<>();
        lowerBoundSeriesHashMap = new HashMap<>();

        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();

        this.chartIndex.addListener((observable, oldValue, newValue) -> {
            resetRange();
            updateChartTitle();
        });


        this.dataHashMap.addListener((MapChangeListener<String, ArrayList<FitotronData>>) change -> {
            if(change.wasRemoved()){
                for (XYChart.Data data : seriesHashMap.get(change.getKey()).getData()){
                    for(int i=0;i<xAxisValues.size();i++){
                        if (xAxisValues.get(i).equals(data.getXValue())){
                            xAxisValues.remove(i);
                            break;
                        }
                    }
                    for(int j=0;j<yAxisValues.size();j++){
                          if (yAxisValues.get(j).equals(data.getYValue())){
                              yAxisValues.remove(j);
                              break;
                          }
                    }
                }
                lineChart.getData().remove(seriesHashMap.get(change.getKey()));
                lineChart.getData().remove(upperBoundSeriesHashMap.get(change.getKey()));
                lineChart.getData().remove(lowerBoundSeriesHashMap.get(change.getKey()));
                seriesHashMap.remove(change.getKey());
                upperBoundSeriesHashMap.remove(change.getKey());
                lowerBoundSeriesHashMap.remove(change.getKey());
            }else{
                updateSeries(change.getKey(), change.getValueAdded());
            }
        });

        this.seriesColorsHashMap.addListener((MapChangeListener<String, Color>) change ->{
            if(seriesHashMap.get(change.getKey())!=null){
                if(change.wasAdded()){
                    updateSeriesStyle(change.getKey());
                }
            }
        });

        this.seriesVisibilitiesHashMap.addListener((MapChangeListener<String, Boolean>) change -> {
            if(seriesHashMap.get(change.getKey())!=null){
                if(change.wasAdded()){
                    updateSeriesStyle(change.getKey());
                }
            }
        });

        this.pointAppearance.addListener((observable, oldValue, newValue) -> {
            updateChartAppearance();
        });

        this.ciAppearance.addListener((observable, oldValue, newValue) -> {
            updateChartAppearance();
        });
    }

    public void start(){
        lineChart.setDisable(false);
    }

    public void updateChartTitle(){

        String chartTitle = chartTitles.get(chartIndex.getValue());

        String xAxisLabel = chartTitle.split(" x ")[1];
        String yAxisLabel = chartTitle.split(" x ")[0];

        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
    }

    public void updateSeries(String seriesName, ArrayList<FitotronData> newSeries){

        XYChart.Series<Double,Double> dataSeries = new XYChart.Series<>();
        XYChart.Series<Double,Double> upperBoundary = new XYChart.Series<>();
        XYChart.Series<Double,Double> lowerBoundary = new XYChart.Series<>();

        switch(chartIndex.getValue()){
            case 0:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getFrequency();
                    double y = data.getImpedanceMeasure().getAverage();
                    double uyl = y + data.getImpedanceMeasure().getError();
                    double lyl = y - data.getImpedanceMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LOG_Y_LOG);
                break;
            case 1:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getTime();
                    double y = data.getImpedanceMeasure().getAverage();
                    double uyl = y + data.getImpedanceMeasure().getError();
                    double lyl = y - data.getImpedanceMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LOG);
                break;
            case 2:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getTemperatureMeasure().getAverage();
                    double y = data.getImpedanceMeasure().getAverage();
                    double uyl = y + data.getImpedanceMeasure().getError();
                    double lyl = y - data.getImpedanceMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LOG);
                break;
            case 3:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getHumidityMeasure().getAverage();
                    double y = data.getImpedanceMeasure().getAverage();
                    double uyl = y + data.getImpedanceMeasure().getError();
                    double lyl = y - data.getImpedanceMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LOG);
                break;
            case 4:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getMassMeasure().getAverage();
                    double y = data.getImpedanceMeasure().getAverage();
                    double uyl = y + data.getImpedanceMeasure().getError();
                    double lyl = y - data.getImpedanceMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LOG);
                break;
            case 5:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getRwcMeasure().getAverage();
                    double y = data.getImpedanceMeasure().getAverage();
                    double uyl = y + data.getImpedanceMeasure().getError();
                    double lyl = y - data.getImpedanceMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LOG);
                break;
            case 6:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getLwcMeasure().getAverage();
                    double y = data.getImpedanceMeasure().getAverage();
                    double uyl = y + data.getImpedanceMeasure().getError();
                    double lyl = y - data.getImpedanceMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LOG);
                break;
            case 7:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getTime();
                    double y = data.getTemperatureMeasure().getAverage();
                    double uyl = y + data.getTemperatureMeasure().getError();
                    double lyl = y - data.getTemperatureMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LIN);
                break;
            case 8:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getTime();
                    double y = data.getHumidityMeasure().getAverage();
                    double uyl = y + data.getHumidityMeasure().getError();
                    double lyl = y - data.getHumidityMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LIN);
                break;
            case 9:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getTime();
                    double y = data.getMassMeasure().getAverage();
                    double uyl = y + data.getMassMeasure().getError();
                    double lyl = y - data.getMassMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LIN);
                break;
            case 10:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getTime();
                    double y = data.getRwcMeasure().getAverage();
                    double uyl = y + data.getRwcMeasure().getError();
                    double lyl = y - data.getRwcMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LIN);
                break;
            case 11:
                for (int i=0; i<newSeries.size();i++){
                    FitotronData data = newSeries.get(i);
                    double x = data.getTime();
                    double y = data.getLwcMeasure().getAverage();
                    double uyl = y + data.getLwcMeasure().getError();
                    double lyl = y - data.getLwcMeasure().getError();
                    xAxisValues.add(x);
                    yAxisValues.add(y);
                    XYChart.Data<Double,Double> point = new XYChart.Data<>(x,y);
                    XYChart.Data<Double,Double> upperError = new XYChart.Data<>(x,uyl);
                    XYChart.Data<Double,Double> lowerError = new XYChart.Data<>(x,lyl);
                    point.setNode(new PointFlyout(data));
                    dataSeries.getData().add(i,point);
                    upperBoundary.getData().add(i,upperError);
                    lowerBoundary.getData().add(i,lowerError);
                }
                seriesHashMap.put(seriesName,dataSeries);
                upperBoundSeriesHashMap.put(seriesName,upperBoundary);
                lowerBoundSeriesHashMap.put(seriesName,lowerBoundary);
                updateChartScale(ChartType.X_LIN_Y_LIN);
                break;
            default:
        }

        if(!lineChart.getData().contains(seriesHashMap.get(seriesName))){
            lineChart.getData().add(seriesHashMap.get(seriesName));
            if(!upperBoundSeriesHashMap.isEmpty()){
                lineChart.getData().add(upperBoundSeriesHashMap.get(seriesName));
                lineChart.getData().add(lowerBoundSeriesHashMap.get(seriesName));
            }
        }

        updateSeriesStyle(seriesName);
    }

    public void updateSeriesStyle(String seriesName){

        //Definindo o estilo da curva:
        seriesHashMap.get(seriesName).getNode().setStyle(
                getGeneratedLineStyle(
                        seriesColorsHashMap.get(seriesName),
                        seriesVisibilitiesHashMap.get(seriesName)
                )
        );

        //Definindo a visibilidade dos pontos:
        for(XYChart.Data<Double,Double> data : seriesHashMap.get(seriesName).getData()) {
            data.getNode().setStyle(getGeneratedPointStyle(
                    seriesColorsHashMap.get(seriesName),
                    seriesVisibilitiesHashMap.get(seriesName))
            );
        }

        //Definindo o estilo dos pontos de erro:
        if(!upperBoundSeriesHashMap.isEmpty()){
            upperBoundSeriesHashMap.get(seriesName).getNode().setVisible(false);
            lowerBoundSeriesHashMap.get(seriesName).getNode().setVisible(false);
            for(int i = 0; i < upperBoundSeriesHashMap.get(seriesName).getData().size();i++){
                upperBoundSeriesHashMap.get(seriesName).getData().get(i).getNode().setStyle(
                        getGeneratedErrorStyle(
                                seriesColorsHashMap.get(seriesName),
                                seriesVisibilitiesHashMap.get(seriesName))
                );
                lowerBoundSeriesHashMap.get(seriesName).getData().get(i).getNode().setStyle(
                        getGeneratedErrorStyle(
                                seriesColorsHashMap.get(seriesName),
                                seriesVisibilitiesHashMap.get(seriesName))
                );
            }
        }
    }

    private void updateChartAppearance(){
        for(String seriesName : seriesHashMap.keySet())
            updateSeriesStyle(seriesName);
    }

    private String getGeneratedLineStyle(Color seriesColor, boolean isVisible){

        String color = "#" + seriesColor.toString().toUpperCase().substring(2,seriesColor.toString().length()-2);
        String cssLineColorString = String.format("-fx-stroke: %s; visibility: %s;\n",
                color,
                (isVisible) ? "visible" : "hidden");

        return cssLineColorString;
    }

    private String getGeneratedPointStyle(Color seriesColor, boolean isVisible){

        String cssPointMarkString;

        switch (pointAppearance.getValue()){
            case 1:
                cssPointMarkString = "-fx-background-radius: 0;";
                break;
            case 2:
                cssPointMarkString = "-fx-background-radius: 5px;";
                break;
            case 3:
                cssPointMarkString = "-fx-shape: \"M5,0 L10,8 L0,8 Z\";";
                break;
            default:
                cssPointMarkString = "visibility: hidden;";
        }

        String color = "#" + seriesColor.toString().toUpperCase().substring(2,seriesColor.toString().length()-2);
        String cssPointColorString = String.format("-fx-background-color: %s, white; visibility: %s;\n",
                color,
                (isVisible) ? "visible" : "hidden");

        return cssPointColorString + cssPointMarkString;
    }

    private String getGeneratedErrorStyle(Color seriesColor, boolean isVisible){

        String cssPointMarkString;

        switch (ciAppearance.getValue()){
            case 1:
                cssPointMarkString = "-fx-background-radius: 0;";
                break;
            case 2:
                cssPointMarkString = "-fx-background-radius: 5px;";
                break;
            case 3:
                cssPointMarkString = "-fx-shape: \"M5,0 L10,8 L0,8 Z\";";
                break;
            default:
                cssPointMarkString = "visibility: hidden;";
        }

        String color = "#" + seriesColor.toString().toUpperCase().substring(2,seriesColor.toString().length()-2);
        String cssPointColorString = String.format("-fx-background-color: %s, white; visibility: %s;\n",
                color,
                (isVisible) ? "visible" : "hidden");

        return cssPointColorString + cssPointMarkString;
    }

    private void updateChartScale(ChartType chartType){

        Collections.sort(xAxisValues);
        double xLowerBound = xAxisValues.get(0);
        double xUpperBound = xAxisValues.get(xAxisValues.size()-1);

        Collections.sort(yAxisValues);
        double yLowerBound = yAxisValues.get(0);
        double yUpperBound = yAxisValues.get(yAxisValues.size()-1);

        this.getChildren().remove(lineChart);

        if(chartType == ChartType.X_LIN_Y_LIN){

            xAxis = new NumberAxis();
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(xLowerBound);
            xAxis.setUpperBound(xUpperBound);
            ((NumberAxis) xAxis).setTickUnit((xUpperBound-xLowerBound)/10);
            yAxis = new NumberAxis();
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(yLowerBound/1.25);
            yAxis.setUpperBound(yUpperBound*1.25);
            ((NumberAxis) yAxis).setTickUnit((yUpperBound-yLowerBound)/10);

        }else if(chartType==ChartType.X_LIN_Y_LOG){

            xAxis = new NumberAxis();
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(xLowerBound);
            xAxis.setUpperBound(xUpperBound);
            ((NumberAxis) xAxis).setTickUnit((xUpperBound-xLowerBound)/10);
            yAxis = new LogarithmicAxis();
            yAxis.setLowerBound(yLowerBound/5);
            yAxis.setUpperBound(yUpperBound*5);

        }else if(chartType==ChartType.X_LOG_Y_LOG){

            xAxis = new LogarithmicAxis();
            xAxis.setLowerBound(xLowerBound/2);
            xAxis.setUpperBound(xUpperBound*2);
            yAxis = new LogarithmicAxis();
            yAxis.setLowerBound(yLowerBound/4);
            yAxis.setUpperBound(yUpperBound*4);
        }

        lineChart = new LineChart(xAxis,yAxis);

        lineChart.paddingProperty().setValue(new Insets(17,17,0,3));
        lineChart.setLegendVisible(false);

        this.getChildren().add(lineChart);

        for(String key : seriesHashMap.keySet()){
            lineChart.getData().add(seriesHashMap.get(key));
            if(!upperBoundSeriesHashMap.isEmpty()){
                lineChart.getData().add(upperBoundSeriesHashMap.get(key));
                lineChart.getData().add(lowerBoundSeriesHashMap.get(key));
            }
            updateSeriesStyle(key);
        }

        String xAxisLabel = chartTitles.get(chartIndex.getValue()).split(" x ")[1];
        String yAxisLabel = chartTitles.get(chartIndex.getValue()).split(" x ")[0];

        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
    }

    private void resetRange(){
        xAxisValues.clear();
        yAxisValues.clear();
    }
}
