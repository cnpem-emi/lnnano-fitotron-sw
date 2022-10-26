package org.cnpem.fitotron.file;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import org.apache.commons.math3.distribution.TDistribution;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

public class FitotronData{

    ///Propriedades:

    private LocalDateTime dateTime;
    private static ObservableList<LocalDateTime> dateTimeList = FXCollections.observableArrayList();
    private static ObservableMap<LocalDateTime, Measure> massHashMap = FXCollections.observableHashMap();

    private double time;
    private double frequency;
    private Measure impedance;
    private Measure temperature;
    private Measure humidity;
    private Measure mass;
    private Measure rwc;
    private Measure lwc;

    ///EventListeners:

    ListChangeListener<LocalDateTime> dateTimeListChangeListener = change -> {
        if(change.next()){
            this.time = ChronoUnit.MINUTES.between(dateTimeList.get(0),dateTime);
        }
    };

    MapChangeListener<LocalDateTime,Measure> massHashMapListener = change -> {
        Measure m0 = massHashMap.get(dateTimeList.get(0));
        Measure mi = massHashMap.get(dateTime);

        double propagetedStandardDeviation =
                Math.sqrt(
                        Math.pow(100*mi.average/Math.pow(m0.average,2),2)*Math.pow(m0.standardDeviation,2)
                        + Math.pow(-100/m0.average,2)*Math.pow(mi.standardDeviation,2)
                );

        double rwc = 100*(m0.average-mi.average)/(m0.average);
        double rwcStandardDeviation = propagetedStandardDeviation;

        double lwc = 100-rwc;
        double lwcStandardDeviation = propagetedStandardDeviation;

        this.rwc.set(mi.getSampleSize(),rwc,rwcStandardDeviation);
        this.lwc.set(mi.getSampleSize(),lwc,lwcStandardDeviation);
    };

    ///Construtores:

    public FitotronData(LocalDateTime localDateTime, double frequency, double[] impedances) {
        super();

        this.dateTime = localDateTime;
        this.frequency = frequency;
        this.impedance = new Measure(impedances);
        this.temperature = new Measure();
        this.humidity = new Measure();
        this.mass = new Measure();
        this.rwc = new Measure();
        this.lwc = new Measure();

        sortedInsert(this.dateTime);
        massHashMap.put(this.dateTime, this.mass);

        dateTimeList.addListener(dateTimeListChangeListener);
        massHashMap.addListener(massHashMapListener);
    }

    public FitotronData(LocalDateTime dateTime,
                        double frequency,
                        int impedanceSampleSize,  double impedance, double impedanceStandardDeviation,
                        int temperatureSampleSize, double temperature, double temperatureStandardDeviation,
                        int humiditySampleSize, double humidity, double humidityStandardDeviation,
                        int massSampleSize, double mass, double massStandardDeviation,
                        double rwc, double rwcStandardDeviation,
                        double lwc, double lwcStandardDeviation)
    {
        super();

        this.dateTime = dateTime;
        this.frequency = frequency;
        this.impedance = new Measure(impedanceSampleSize, impedance,impedanceStandardDeviation);
        this.temperature = new Measure(temperatureSampleSize,temperature,temperatureStandardDeviation);
        this.humidity = new Measure(humiditySampleSize, humidity, humidityStandardDeviation);
        this.mass = new Measure(massSampleSize, mass, massStandardDeviation);
        this.rwc = new Measure(massSampleSize, rwc ,rwcStandardDeviation);
        this.lwc = new Measure(massSampleSize, lwc, lwcStandardDeviation);

        sortedInsert(this.dateTime);
        massHashMap.put(this.dateTime, this.mass);

        dateTimeList.addListener(dateTimeListChangeListener);
        massHashMap.addListener(massHashMapListener);
    }

    ///Setters:

    public void setTemperature(double[] samples) {
        this.temperature.set(samples);
    }

    public void setHumidity(double[] samples) {
        this.humidity.set(samples);
    }

    public void setMass(double[] samples) {
        this.mass.set(samples);
        this.massHashMap.replace(dateTime,mass);
    }

    ///Getters:

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getTime() {
        return time;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getImpedance() {
        return impedance.getAverage();
    }

    public double getTemperature() {
        return temperature.getAverage();
    }

    public double getHumidity() {
        return humidity.getAverage();
    }

    public double getMass() {
        return mass.getAverage();
    }

    public double getRwc() {
        return rwc.getAverage();
    }

    public double getLwc() {
        return lwc.getAverage();
    }

    public Measure getImpedanceMeasure() {
        return impedance;
    }

    public Measure getTemperatureMeasure() {
        return temperature;
    }

    public Measure getHumidityMeasure() {
        return humidity;
    }

    public Measure getMassMeasure() {
        return mass;
    }

    public Measure getRwcMeasure() {
        return rwc;
    }

    public Measure getLwcMeasure() {
        return lwc;
    }

    ///Métodos Sobrescritos:

    @Override
    public boolean equals(Object obj) {

        FitotronData fitotronData = (FitotronData) obj;

        boolean result;

        result = this.dateTime.equals(fitotronData.getDateTime());
        result = result && this.time == fitotronData.getTime();
        result = result && this.frequency == fitotronData.getFrequency();
        result = result && this.impedance.equals(fitotronData.getImpedanceMeasure());
        result = result && this.temperature.equals(fitotronData.getTemperatureMeasure());
        result = result && this.humidity.equals(fitotronData.getHumidityMeasure());
        result = result && this.mass.equals(fitotronData.getMassMeasure());

        return result;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(dateTime.toString());
        stringBuilder.append(",");
        stringBuilder.append(time);
        stringBuilder.append(",");
        stringBuilder.append(frequency);
        stringBuilder.append(",");
        stringBuilder.append(impedance.toString());
        stringBuilder.append(",");
        stringBuilder.append(temperature.toString());
        stringBuilder.append(",");
        stringBuilder.append(humidity.toString());
        stringBuilder.append(",");
        stringBuilder.append(mass.toString());
        stringBuilder.append(",");
        stringBuilder.append(rwc.toString());
        stringBuilder.append(",");
        stringBuilder.append(lwc.toString());

        return stringBuilder.toString();
    }

    ///Métodos Estáticos:

    public static void setConfidence(int confidence) {
        Measure.confidence.setValue(confidence);
    }

    public static double getConfidence() {
        return Measure.confidence.get();
    }

    ///Métodos Auxiliares

    public void sortedInsert(LocalDateTime dateTime){

        int size = dateTimeList.size()+1;

        for(int i=0;i<size;i++){
            if(i<(size-2)){
                if((dateTimeList.get(i).isBefore(dateTime))
                        && dateTimeList.get(i+1).isAfter(dateTime)){
                    dateTimeList.add(i+1,dateTime);
                    break;
                }
            }else{
                dateTimeList.add(dateTime);
            }
        }

        this.time = ChronoUnit.MINUTES.between(dateTimeList.get(0),dateTime);
    }

    ///Classes Internas:

    public class Measure{

        ///Propriedades:

        protected static IntegerProperty confidence = new SimpleIntegerProperty(95);

        private int sampleSize;
        private double average;
        private double standardDeviation;
        private double error;

        ///EventHandler:
        private ChangeListener<Number> confidenceChangeListener = (observableValue, number, t1) -> {
            this.confidence.set(t1.intValue());
            this.error = error(sampleSize,standardDeviation);
        };

        ///Construtores

        public Measure(){
            this.sampleSize = 0;
            this.average = 0;
            this.standardDeviation = 0;
            this.error = 0;
            confidence.addListener(confidenceChangeListener);
        }

        public Measure(double[] samples){
            set(samples);
            confidence.addListener(confidenceChangeListener);
        }

        public Measure(int sampleSize, double average, double standardDeviation){
            set(sampleSize,average,standardDeviation);
            confidence.addListener(confidenceChangeListener);
        }

        //Setters:

        public void set(double[] samples) {
            this.sampleSize = samples.length;
            this.average = average(samples);
            this.standardDeviation = standardDeviation(samples, average);
            this.error = error(sampleSize, standardDeviation);
        }

        public void set(int sampleSize, double average, double standardDeviation){
            this.sampleSize = sampleSize;
            this.average = average;
            this.standardDeviation = standardDeviation;
            this.error = error(sampleSize,standardDeviation);
        }

        ///Getters:

        protected int getSampleSize() {
            return sampleSize;
        }

        public double getAverage() {
            return average;
        }

        public double getStandardDeviation(){
            return standardDeviation;
        }

        public double getError() {
            return error;
        }

        ///Métodos Auxiliares:

        private double average(double[] values){

            double sum = 0;
            int n = 0;

            for(double value : values){
                n++;
                sum += value;
            }

            return sum/n;
        }

        private double standardDeviation(double[] values, double average){

            double sum = 0;
            double n = 0;

            for (Double value : values) {
                n++;
                sum += Math.pow(value-average,2);
            }

            return Math.sqrt(sum/(n-1));
        }

        private double error(int n, double standardDeviation){

            if(n<2) return 0;

            TDistribution tDistribution = new TDistribution(n-1);

            double alpha = 1 - ((double)confidence.getValue()) / 100;

            double t = -tDistribution.inverseCumulativeProbability(alpha/2);

            return (t*standardDeviation/Math.sqrt(n));
        }

        ///Métodos sobrescritos:

        @Override
        public boolean equals(Object obj) {

            Measure measure = (Measure) obj;

            boolean result;

            result = this.getSampleSize()==measure.getSampleSize();
            result = result && this.getAverage()==measure.getAverage();
            result = result && this.getStandardDeviation()==measure.getStandardDeviation();
            result = result && this.getError()==measure.getError();

            return result;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(sampleSize);
            stringBuilder.append(",");
            stringBuilder.append(average);
            stringBuilder.append(",");
            stringBuilder.append(standardDeviation);
            stringBuilder.append(",");
            stringBuilder.append(error);

            return stringBuilder.toString();
        }
    }
}