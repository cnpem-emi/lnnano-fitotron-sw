package org.cnpem.fitotron.table;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.cnpem.fitotron.file.FitotronData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.cnpem.fitotron.measurement.MeasurementController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class FitotronTable extends TableView{

    private IntegerProperty chartIndex;
    private StringProperty selectedSeries;
    private ObservableList<FitotronData> dataArrayList;
    private ObservableMap<String, ArrayList<FitotronData>> dataHashMap;

    private TableColumn<FitotronData, Double> timeColumn = new TableColumn<FitotronData, Double>("Tempo [min]");
    private TableColumn<FitotronData, Double> impedanceColumn = new TableColumn<FitotronData, Double>("Impedância [Ω]");
    private TableColumn<FitotronData, Double> frequencyColumn = new TableColumn<FitotronData, Double>("Frequência [Hz]");
    private TableColumn<FitotronData, Double> temperatureColumn = new TableColumn<FitotronData, Double>("Temperatura [°C]");
    private TableColumn<FitotronData, Double> humidityColumn = new TableColumn<FitotronData, Double>("Umidade [%]");
    private TableColumn<FitotronData, Double> massColumn = new TableColumn<FitotronData, Double>("Massa [mg]");
    private TableColumn<FitotronData, Double> rwcColumn = new TableColumn<FitotronData, Double>("RWC [%]");
    private TableColumn<FitotronData, Double> lwcColumn = new TableColumn<FitotronData, Double>("LWC [%]");

    public FitotronTable(
            IntegerProperty chartIndex,
            StringProperty selectedSeries,
            ObservableList<FitotronData> dataArrayList,
            ObservableMap<String, ArrayList<FitotronData>> dataHashMap
    ){
        //Inicializando o construtor da classe pai:
        super();

        this.chartIndex = chartIndex;
        this.selectedSeries = selectedSeries;
        this.dataArrayList = dataArrayList;
        this.dataHashMap = dataHashMap;

        //Desabilitando a tabela:
        this.setDisable(true);

        //Habilitando a edição das células:
        this.setEditable(true);

        //Escrevendo "Tabela vazia" para indicar que no momento da abertura do programa não há dados carregados:
        this.setPlaceholder(new Label("Tabela vazia"));

        //Desabilidando o redimensionamento da largura das colunas da tabela:
        timeColumn.setResizable(false);
        impedanceColumn.setResizable(false);
        frequencyColumn.setResizable(false);
        temperatureColumn.setResizable(false);
        humidityColumn.setResizable(false);
        massColumn.setResizable(false);
        rwcColumn.setResizable(false);
        lwcColumn.setResizable(false);

        //Desabilitando a edição das colunas:
        timeColumn.setEditable(false);
        impedanceColumn.setEditable(false);
        frequencyColumn.setEditable(false);
        temperatureColumn.setEditable(false);
        humidityColumn.setEditable(false);
        massColumn.setEditable(false);
        rwcColumn.setEditable(false);
        lwcColumn.setEditable(false);

        //Pré-definindo a largura das colunas da tabela:
        timeColumn.setPrefWidth(150);
        impedanceColumn.setPrefWidth(150);
        frequencyColumn.setPrefWidth(150);
        temperatureColumn.setPrefWidth(150);
        humidityColumn.setPrefWidth(150);
        massColumn.setPrefWidth(150);
        rwcColumn.setPrefWidth(150);
        lwcColumn.setPrefWidth(150);

        //Definindo o alinhamento do texto das células:
        timeColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        impedanceColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        frequencyColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        temperatureColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        humidityColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        massColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        rwcColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        lwcColumn.setStyle("-fx-alignment: CENTER-LEFT;");

        //Definindo o parâmetro do objeto da classe Measure pelo qual a coluna deve buscar para preencher suas células:
        timeColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("time"));
        impedanceColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("impedance"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("frequency"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("temperature"));
        humidityColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("humidity"));
        massColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("mass"));
        rwcColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("rwc"));
        lwcColumn.setCellValueFactory(new PropertyValueFactory<FitotronData,Double>("lwc"));

        //Definindo o objeto que lhe dará com o valor inserido:
        timeColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(1,1);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {
                    showAlertDialog("Mensagem",
                            "Não é possível editar este valor!",
                            "Apenas os valores de Temperatura, Umidade e Massa podem ser alterados."
                    );
                }

                mouseEvent.consume();
            });

            return tableCell;
        });
        impedanceColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(2,2);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {
                    showAlertDialog("Mensagem",
                            "Não é possível editar este valor!",
                            "Apenas os valores de Temperatura, Umidade e Massa podem ser alterados."
                    );
                }

                mouseEvent.consume();
            });

            return tableCell;
        });
        frequencyColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(4,4);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {
                    showAlertDialog("Mensagem",
                            "Não é possível editar este valor!",
                            "Apenas os valores de Temperatura, Umidade e Massa podem ser alterados."
                    );
                }

                mouseEvent.consume();
            });

            return tableCell;
        });
        temperatureColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(2,2);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {

                    FitotronData data = (FitotronData) this.getSelectionModel().getSelectedItem();

                    double[] values = showMeasurementDialogAndWait(
                                    5, data.getTime(), data.getFrequency(), "Temperatura [°C]");

                    if (values != null) {

                        for(int i=0;i<dataArrayList.size();i++){
                            if(dataArrayList.get(i).getTime()==data.getTime()){
                                dataArrayList.get(i).setTemperature(values);
                            }
                        }

                        String[] keySet = new String[dataHashMap.keySet().size()];
                        dataHashMap.keySet().toArray(keySet);

                        for (String key : keySet) {
                            ArrayList<FitotronData> arrayList = dataHashMap.get(key);
                            dataHashMap.remove(key);
                            dataHashMap.put(key, arrayList);
                        }

                        this.refresh();
                    }
                }
                mouseEvent.consume();
            });

            return tableCell;
        });
        humidityColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(2,2);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {

                    FitotronData data = (FitotronData) this.getSelectionModel().getSelectedItem();

                    double[] values = showMeasurementDialogAndWait(
                            5, data.getTime(), data.getFrequency(), "Umidade [%]");

                    if (values != null) {

                        for(int i=0;i<dataArrayList.size();i++){
                            if(dataArrayList.get(i).getTime()==data.getTime()){
                                dataArrayList.get(i).setHumidity(values);
                            }
                        }

                        String[] keySet = new String[dataHashMap.keySet().size()];
                        dataHashMap.keySet().toArray(keySet);

                        for (String key : keySet) {
                            ArrayList<FitotronData> arrayList = dataHashMap.get(key);
                            dataHashMap.remove(key);
                            dataHashMap.put(key, arrayList);
                        }

                        this.refresh();
                    }
                }
                mouseEvent.consume();
            });

            return tableCell;
        });
        massColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(2,2);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {

                    FitotronData data = (FitotronData) this.getSelectionModel().getSelectedItem();

                    double[] values = showMeasurementDialogAndWait(
                            5, data.getTime(), data.getFrequency(), "Massa [mg]");

                    if (values != null) {

                        for(int i=0;i<dataArrayList.size();i++){
                            if(dataArrayList.get(i).getTime()==data.getTime()){
                                dataArrayList.get(i).setMass(values);
                            }
                        }

                        String[] keySet = new String[dataHashMap.keySet().size()];
                        dataHashMap.keySet().toArray(keySet);

                        for (String key : keySet) {
                            ArrayList<FitotronData> arrayList = dataHashMap.get(key);
                            dataHashMap.remove(key);
                            dataHashMap.put(key, arrayList);
                        }

                        this.refresh();
                    }
                }
                mouseEvent.consume();
            });

            return tableCell;
        });
        rwcColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(1,2);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {
                    showAlertDialog("Mensagem",
                            "Não é possível editar este valor!",
                            "Apenas os valores de Temperatura, Umidade e Massa podem ser alterados."
                    );
                }

                mouseEvent.consume();
            });

            return tableCell;
        });
        lwcColumn.setCellFactory(col ->{

            EditableDoubleTableCell tableCell = new EditableDoubleTableCell(1,2);

            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {

                if (mouseEvent.getClickCount() == 2) {
                    showAlertDialog("Mensagem",
                            "Não é possível editar este valor!",
                            "Apenas os valores de Temperatura, Umidade e Massa podem ser alterados."
                    );
                }

                mouseEvent.consume();
            });

            return tableCell;
        });
    }

    public void initialize(){

        //Definindo o handler do evento de deletamento de dados:
//        this.setOnKeyPressed(event -> {
//            if(event.getCode()== KeyCode.DELETE){
//                removeItem((FitotronData) this.getSelectionModel().getSelectedItem());
//            }
//            event.consume();
//        });

        //Definindo o Listener relativo a mudança de gráfico:
        chartIndex.addListener((observable, oldValue, newValue) -> {
            selectChart(newValue.intValue());
        });

        //Definindo o Listener relativo a mudança de seleção de série:
        selectedSeries.addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
                selectSeries(newValue);
        });
    }

    public void start(){

        //Habilitando a tabela caso esteja desabilitada:
        if(this.isDisable())
            this.setDisable(false);
    }

    protected void selectChart(int chartIndex){

        ///Atualizando o HashMap observável:

        dataHashMap.clear();

        HashMap<String,ArrayList<FitotronData>> hashMap = new HashMap<>();

        if(chartIndex==0){

            HashMap<String, ArrayList<Double>> passedFrequencies = new HashMap<>();

            for(FitotronData data : dataArrayList){
                String seriesTitle = String.format("%f [min]",data.getTime());
                passedFrequencies.putIfAbsent(seriesTitle, new ArrayList<>());
                if (!passedFrequencies.get(seriesTitle).contains(data.getFrequency())){
                    passedFrequencies.get(seriesTitle).add(data.getFrequency());
                    hashMap.putIfAbsent(seriesTitle, new ArrayList<>());
                    hashMap.get(seriesTitle).add(data);
                }
            }

        }else{

            HashMap<String, ArrayList<LocalDateTime>> passedTimes = new HashMap<>();

            for(FitotronData data : dataArrayList){
                String seriesTitle = String.format("%f [Hz]",data.getFrequency());
                passedTimes.putIfAbsent(seriesTitle, new ArrayList<>());
                if(!passedTimes.get(seriesTitle).contains(data.getDateTime())){
                    passedTimes.get(seriesTitle).add(data.getDateTime());
                    hashMap.putIfAbsent(seriesTitle, new ArrayList<>());
                    hashMap.get(seriesTitle).add(data);
                }
            }
        }

        ///Redefinindo as colunas da tabela:

        this.getColumns().clear();

        switch (chartIndex) {
            case 0:
                this.getColumns().addAll(impedanceColumn, frequencyColumn);
                break;
            case 1:
                this.getColumns().addAll(impedanceColumn, timeColumn);
                break;
            case 2:
                this.getColumns().addAll(impedanceColumn, temperatureColumn);
                break;
            case 3:
                this.getColumns().addAll(impedanceColumn, humidityColumn);
                break;
            case 4:
                this.getColumns().addAll(impedanceColumn, massColumn);
                break;
            case 5:
                this.getColumns().addAll(impedanceColumn, rwcColumn);
                break;
            case 6:
                this.getColumns().addAll(impedanceColumn, lwcColumn);
                break;
            case 7:
                this.getColumns().addAll(temperatureColumn, timeColumn);
                break;
            case 8:
                this.getColumns().addAll(humidityColumn, timeColumn);
                break;
            case 9:
                this.getColumns().addAll(massColumn, timeColumn);
                break;
            case 10:
                this.getColumns().addAll(rwcColumn, timeColumn);
                break;
            case 11:
                this.getColumns().addAll(lwcColumn, timeColumn);
                break;
        }

        ///Adicionando os novos valores ao HashMap de dados:

        dataHashMap.putAll(hashMap);
    }

    public void selectSeries(String key){

        //Removendo os dados antigos:
        this.getItems().clear();

        //Definindo os valores da tabela com novos dados:
        this.setItems(FXCollections.observableArrayList(dataHashMap.get(key)));
    }

    protected void removeItem(FitotronData item){

        //Se o item não for nulo, remova-o do conjunto de dados...
        if (item == null)
            return;

        //Coletando a frequência e o instante de tempo do ítem a ser removido:

        double time;
        double frequency;

        String key = selectedSeries.getValue();

        if (chartIndex.getValue().equals(0)) {

            time = Double.parseDouble(key.replace("[min]","").trim().replace(",","."));
            frequency = item.getFrequency();

        } else if (chartIndex.getValue().equals(1)) {

            time = item.getTime();
            frequency = Double.parseDouble(key.replace("[Hz]","").trim().replace(",","."));

        } else {

            showAlertDialog("Mensagem",
                    "Não é possível excluir este ponto!",
                    "Apenas pontos dos gráficos 'Impedância x Frequência' e 'Impedância x Tempo' podem ser excluídos."
            );
            return;
        }

        //Removendo os itens do ArrayList de dados:
        int length = dataArrayList.size();
        int counter = 0;

        for (int i = 0; i < length-counter; i++){
            if (dataArrayList.get(i).getTime()==time && dataArrayList.get(i).getFrequency()==frequency) {
                dataArrayList.remove(i);
                counter++;
                i--;
            }
        }

        //Removendo os itens do HashMap de dados:
        ArrayList<FitotronData> arrayList = dataHashMap.get(key);
        arrayList.remove(item);
        dataHashMap.remove(key);
        dataHashMap.put(key, arrayList);

        //Removendo o ítem da tabela:
        this.getItems().remove(item);

        //Atualizando a tabela:
        this.refresh();
    }

    private double[] showMeasurementDialogAndWait(int sampleSize,double time, double frequency, String columnTitle){

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/org/cnpem/fitotron/measurement.fxml"));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(loader.load());

            String unit = columnTitle.substring(columnTitle.indexOf('['));
            String title = String.format("%s ( %.3f [min] , %.3f [Hz] )",
                    columnTitle.replace(unit,""),
                    time,
                    frequency
            );

            ButtonType okBttn = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelBttn = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.setTitle(title);
            dialog.getDialogPane().setPrefWidth(375);
            dialog.getDialogPane().setPrefHeight(300);
            dialog.getDialogPane().getButtonTypes().add(okBttn);
            dialog.getDialogPane().getButtonTypes().add(cancelBttn);
            Node okButtonNode = dialog.getDialogPane().lookupButton(okBttn);

            MeasurementController measurementController = loader.getController();
            measurementController.setList(unit,sampleSize);
            MeasurementController.setOkButtonReference(okButtonNode);

            Optional<ButtonType> buttonType = dialog.showAndWait();

            if(buttonType.get() == okBttn){
                double[] values = measurementController.getValues();
                dialog.close();
                return values;
            }else{
                dialog.close();
                return null;
            }

        }catch (IOException e){
            System.out.println("Erro na coleta das medidas!");
            return null;
        }
    }

    private void showAlertDialog(String title, String header, String content){

        Alert invalidFileAlert = new Alert(Alert.AlertType.WARNING);
        invalidFileAlert.setTitle(title);
        invalidFileAlert.setHeaderText(header);
        invalidFileAlert.setContentText(content);
        invalidFileAlert.show();
    }
}