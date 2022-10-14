package org.cnpem.fitotron.file;

import org.cnpem.fitotron.App;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FitotronCalculator{

    private ObservableList<Text> logs;
    private ObservableList<File> files;
    private ObservableFile savedFile;

    private StringProperty confidence;
    private ObservableList<FitotronData> dataArrayList;

    EventHandler<ActionEvent> startComponentsEventHandler;

    public FitotronCalculator(
            ObservableList<Text> logs,
            ObservableList<File> files,
            ObservableFile savedFile,
            StringProperty confidence,
            ObservableList<FitotronData> dataArrayList,
            EventHandler<ActionEvent> startComponentsEventHandler
    ){
        super();

        this.files = files;
        this.logs = logs;
        this.savedFile = savedFile;
        this.confidence = confidence;
        this.dataArrayList = dataArrayList;
        this.startComponentsEventHandler = startComponentsEventHandler;
    }

    public void initialize(){

        confidence.set("###");

        files.addListener((ListChangeListener<File>) c -> {
            while(c.next()){
                if(c.wasAdded()){
                    readData();
                }
            }
        });

        confidence.addListener((observable, oldValue, newValue) -> {
            if(!dataArrayList.isEmpty()){
                FitotronData.setConfidence(Integer.valueOf(newValue.substring(0,2)));
                logs.add(new Text(String.format(
                        "Intervalos de confiança recalculados com sucesso (IC = %.0f%%).\n", FitotronData.getConfidence()
                )));
                startComponentsEventHandler.handle(new ActionEvent());
            }
        });

        savedFile.addListener((observable, oldValue, newValue) -> {
            App.setStageTitle(String.format("LNnano - Fitotron : <%s>",
                    (newValue==null) ? "..." : newValue.getAbsolutePath())
            );
        });
    }

    public void readData(){

        String fileName = files.get(0).getName();
        String fileExtension = fileName.substring(fileName.length()-3);

        if(fileExtension.equals("fit")){
            loadData();
        }else if (fileExtension.equals("csv")){
            importData();
        }else{
            logs.add(new Text(String.format(
                    "Arquivo inválido (%s)! Selecione apenas arquivos .fit e .csv compatíveis com o Fitotron.\n",
                    fileName
            )));
        }
    }

    public void loadData(){

        File file = files.get(0);

        try(FileReader fl = new FileReader(file.getAbsolutePath())){

            BufferedReader br = new BufferedReader(fl);
            ArrayList<FitotronData> samples = new ArrayList<>();
            ArrayList<String> lines = new ArrayList<>(br.lines().toList());

            for (int i = 1; i<lines.size(); i++) {

                String[] values = lines.get(i).split(",");

                FitotronData sample = new FitotronData(
                        LocalDateTime.parse(values[0]),
                        Double.parseDouble(values[2]),
                        Integer.parseInt(values[3]),
                        Double.parseDouble(values[4]),
                        Double.parseDouble(values[5]),
                        Integer.parseInt(values[7]),
                        Double.parseDouble(values[8]),
                        Double.parseDouble(values[9]),
                        Integer.parseInt(values[11]),
                        Double.parseDouble(values[12]),
                        Double.parseDouble(values[13]),
                        Integer.parseInt(values[15]),
                        Double.parseDouble(values[16]),
                        Double.parseDouble(values[17]),
                        Double.parseDouble(values[20]),
                        Double.parseDouble(values[21]),
                        Double.parseDouble(values[24]),
                        Double.parseDouble(values[25])
                );

                samples.add(sample);
            }

            Collections.sort(samples, new Comparator<FitotronData>() {
                @Override
                public int compare(FitotronData o1, FitotronData o2) {

                    int resultTime = o1.getDateTime().compareTo(o2.getDateTime());
                    double resultFreq = o1.getFrequency() - o2.getFrequency();

                    if(resultTime>0 || resultTime==0 && resultFreq>0){
                        return 1;
                    }else if(resultTime==0 && resultFreq==0){
                        return 0;
                    }else{
                        return -1;
                    }
                }
            });

            String confidenceString = String.format("%.0f%%",FitotronData.getConfidence());

            dataArrayList.clear();
            confidence.set(confidenceString);
            dataArrayList.addAll(samples);

            logs.add(new Text(String.format("Arquivo %s lido com sucesso (%d medidas obtidas).\n",
                    file.getName(),samples.size())));

            savedFile.setValue(files.get(0));

            startComponentsEventHandler.handle(new ActionEvent());

        }catch (Exception e){

            logs.add(new Text(String.format("Falha ao ler o arquivo %s !\n", file.getName())));
            return;
        }
    }

    public void importData(){

        ArrayList<FitotronData> samples = new ArrayList<>();

        for (File file : files) {

            LocalDateTime time;

            try{

                String fileName = file.getName();

                time = LocalDateTime.of(
                        Integer.parseInt(fileName.substring(fileName.length()-24,fileName.length()-20)),
                        Integer.parseInt(fileName.substring(fileName.length()-19,fileName.length()-17)),
                        Integer.parseInt(fileName.substring(fileName.length()-16,fileName.length()-14)),
                        Integer.parseInt(fileName.substring(fileName.length()-13,fileName.length()-11)),
                        Integer.parseInt(fileName.substring(fileName.length()-10,fileName.length()-8)),
                        Integer.parseInt(fileName.substring(fileName.length()-7,fileName.length()-5))
                );

            }catch(Exception e){

                logs.add(new Text(String.format(
                        "Arquivo inválido (%s)!\nNão foi possível extrair do título do arquivo o horário da amostragem.\n",
                        file.getName())
                ));
                return;
            }

            logs.add(new Text(String.format("Lendo o arquivo %s ...\n", file.getName())));

            try(FileReader fl = new FileReader(file.getAbsolutePath())){

                BufferedReader br = new BufferedReader(fl);
                ArrayList<String> lines = new ArrayList<>(br.lines().toList());
                HashMap<Double,ArrayList<Double>> valuesHashMap = new HashMap<>();

                for (int i = 1; i<lines.size(); i++){

                    String[] values = lines.get(i).split(",");

                    if(!values[0].equals("NaN") && !values[3].equals("NaN")){

                        double frequency = Double.parseDouble(values[0]);
                        double impedance = Double.parseDouble(values[3]);

                        if(!valuesHashMap.containsKey(frequency))
                            valuesHashMap.put(frequency,new ArrayList<>());

                        valuesHashMap.get(frequency).add(impedance);
                    }
                }

                for (double frequency : valuesHashMap.keySet()){
                    double[] impedances = valuesHashMap.get(frequency).stream().mapToDouble(lf->lf).toArray();
                    FitotronData measure = new FitotronData(time, frequency, impedances);
                    samples.add(measure);
                }

                logs.add(new Text(String.format("Arquivo %s lido com sucesso.\n",file.getName())));

            }catch (Exception e){

                logs.add(new Text(String.format("Falha ao ler o arquivo (%s)!\nEstrutura de dados corrompida.\n",file.getName())));

                e.printStackTrace();
                return;
            }
        }

        Collections.sort(samples, new Comparator<FitotronData>() {
            @Override
            public int compare(FitotronData o1, FitotronData o2) {

                int resultTime = o1.getDateTime().compareTo(o2.getDateTime());
                double resultFreq = o1.getFrequency() - o2.getFrequency();

                if(resultTime>0 || resultTime==0 && resultFreq>0){
                    return 1;
                }else if(resultTime==0 && resultFreq==0){
                    return 0;
                }else{
                    return -1;
                }
            }
        });

        String confidenceString = String.format("%.0f%%",FitotronData.getConfidence());

        dataArrayList.clear();
        confidence.set(confidenceString);
        dataArrayList.addAll(samples);

        logs.add(new Text(String.format("Importação de dados bem sucedida (%d medidas obtidas).\n", samples.size())));

        savedFile.setValue(null);

        startComponentsEventHandler.handle(new ActionEvent());
    }
}