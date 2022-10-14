package org.cnpem.fitotron;

import org.cnpem.fitotron.combos.FitotronComboBox;
import org.cnpem.fitotron.combos.FitotronConfidenceInterval;
import org.cnpem.fitotron.chart.FitotronChart;
import org.cnpem.fitotron.chart.PointFlyout;
import org.cnpem.fitotron.file.FitotronCalculator;
import org.cnpem.fitotron.file.FitotronData;
import org.cnpem.fitotron.file.FitotronFileManager;
import org.cnpem.fitotron.file.ObservableFile;
import org.cnpem.fitotron.list.FitotronSeriesList;
import org.cnpem.fitotron.table.FitotronTable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PlotterController implements Initializable{

    //--------------------------------------------------------------------------------------------------------------

    //Objetos da Interface do usuário:

    @FXML
    private BorderPane main_BorderPane;

    @FXML
    private MenuItem save_MenuItem;
    @FXML
    private MenuItem saveAs_MenuItem;
    @FXML
    private MenuItem export_MenuItem;

    @FXML
    private Menu points_SubMenu;
    @FXML
    private CheckMenuItem pointsNone_CheckMenuItem;
    @FXML
    private CheckMenuItem pointsSquare_CheckMenuItem;
    @FXML
    private CheckMenuItem pointsTriangle_CheckMenuItem;
    @FXML
    private CheckMenuItem pointsCircle_CheckMenuItem;

    @FXML
    private Menu confidenceInterval_SubMenu;
    @FXML
    private CheckMenuItem confidenceIntervalNone_CheckMenuItem;
    @FXML
    private CheckMenuItem confidenceIntervalSquare_CheckMenuItem;
    @FXML
    private CheckMenuItem confidenceIntervalTriangle_CheckMenuItem;
    @FXML
    private CheckMenuItem confidenceIntervalCircle_CheckMenuItem;

    @FXML
    private StackPane center_StackPane;

    @FXML
    private VBox toolbox_VBox;

    @FXML
    private ScrollPane log_ScrollPane;
    @FXML
    private TextFlow log_TextFlow;

    //--------------------------------------------------------------------------------------------------------------

    //Variáveis Globais:

    private static final int NO_MARK_APPEARANCE = 0;
    private static final int SQUARE_MARK_APPEARANCE = 1;
    private static final int CIRCLE_MARK_APPEARANCE = 2;
    private static final int TRIANGLE_MARK_APPEARANCE = 3;

    private boolean isEnabled;

    private ObservableList<Text> logs;
    private ObservableList<File> files;
    private ObservableFile savedFile;

    private ObservableList<String> confidenceIntervals;
    private StringProperty confidence;

    private ObservableList<String> chartTitles;
    private IntegerProperty chartIndex;
    private StringProperty selectedSeries;
    private ObservableList<FitotronData> dataArrayList;
    private ObservableMap<String, ArrayList<FitotronData>> dataHashMap;
    private ObservableMap<String, Color> seriesColorsHashMap;
    private ObservableMap<String,Boolean> seriesVisibilitiesHashMap;
    private IntegerProperty pointAppearance;
    private IntegerProperty ciAppearance;
    private BooleanProperty isLinear;

    private FitotronFileManager fitotronFileManager;
    private FitotronCalculator fitotronCalculator;
    private FitotronComboBox fitotronComboBox;
    private FitotronTable fitotronTable;
    private FitotronSeriesList fitotronSeriesList;
    private FitotronChart fitotronChart;
    private FitotronConfidenceInterval fitotronConfidenceInterval;

    public static EventHandler<MouseEvent> showStatistics = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(event.getEventType() == MouseEvent.MOUSE_ENTERED){
                ((PointFlyout) event.getSource()).getPopup().show(
                        App.getStage(),event.getScreenX(),event.getScreenY()
                );
            }else if(event.getEventType() == MouseEvent.MOUSE_EXITED){
                ((PointFlyout) event.getSource()).getPopup().hide();
            }
            event.consume();
        }
    };

    private EventHandler<ActionEvent> startComponentsEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            fitotronComboBox.start();
            fitotronTable.start();
            fitotronSeriesList.start();
            fitotronChart.start();
            fitotronConfidenceInterval.start();

            if(!isEnabled){
                setEnabled(true);
            }

            pointsSquare_CheckMenuItem.fire();
            confidenceIntervalNone_CheckMenuItem.fire();

            event.consume();
        }
    };

    //--------------------------------------------------------------------------------------------------------------

    //Métodos de Inicialzação:

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setEnabled(false);

        this.confidenceIntervals = FXCollections.observableArrayList(
                "95%","90%","85%","80%","75%"
        );

        this.chartTitles = FXCollections.observableArrayList(
                "Impedância [Ω] x Frequência [Hz]",
                "Impedância [Ω] x Tempo [min]",
                "Impedância [Ω] x Temperatura [°C]",
                "Impedância [Ω] x Umidade [%]",
                "Impedância [Ω] x Massa [mg]",
                "Impedância [Ω] x RWC [%]",
                "Impedância [Ω] x LWC [%]",
                "Temperatura [°C] x Tempo [min]",
                "Umidade [%] x Tempo [min]",
                "Massa [mg] x Tempo [min]",
                "RWC [%] x Tempo [min]",
                "LWC [%] x Tempo [min]"
        );

        this.logs = FXCollections.observableArrayList();
        this.files = FXCollections.observableArrayList();
        this.savedFile = new ObservableFile();
        this.confidence = new SimpleStringProperty();
        this.chartIndex = new SimpleIntegerProperty();
        this.selectedSeries = new SimpleStringProperty();
        this.dataArrayList = FXCollections.observableArrayList();
        this.dataHashMap = FXCollections.observableHashMap();
        this.seriesColorsHashMap = FXCollections.observableHashMap();
        this.seriesVisibilitiesHashMap = FXCollections.observableHashMap();
        this.pointAppearance = new SimpleIntegerProperty(FitotronChart.DEFAUT_POINT_APPEARANCE);
        this.ciAppearance = new SimpleIntegerProperty(FitotronChart.DEFAUT_CI_APPEARANCE);
        this.isLinear = new SimpleBooleanProperty(true);

        this.fitotronComboBox = new FitotronComboBox(
                chartIndex,
                chartTitles
        );
        this.fitotronTable = new FitotronTable(
                chartIndex,
                selectedSeries,
                dataArrayList,
                dataHashMap
        );
        this.fitotronSeriesList = new FitotronSeriesList(
                selectedSeries,
                dataHashMap,
                seriesColorsHashMap,
                seriesVisibilitiesHashMap
        );
        this.fitotronChart = new FitotronChart(
                chartTitles,
                chartIndex,
                confidence,
                dataArrayList,
                dataHashMap,
                seriesColorsHashMap,
                seriesVisibilitiesHashMap,
                pointAppearance,
                ciAppearance
        );
        this.fitotronConfidenceInterval = new FitotronConfidenceInterval(
                confidenceIntervals,
                confidence
        );
        this.fitotronFileManager = new FitotronFileManager(
                main_BorderPane,
                logs,
                files,
                savedFile,
                confidence,
                dataArrayList
        );
        this.fitotronCalculator = new FitotronCalculator(
                logs,
                files,
                savedFile,
                confidence,
                dataArrayList,
                startComponentsEventHandler
        );

        toolbox_VBox.getChildren().add(fitotronComboBox);
        toolbox_VBox.getChildren().add(fitotronTable);
        toolbox_VBox.getChildren().add(fitotronSeriesList);

        center_StackPane.getChildren().add(fitotronChart);
        center_StackPane.getChildren().add(fitotronConfidenceInterval);

        toolbox_VBox.setVgrow(fitotronComboBox, Priority.NEVER);
        VBox.setMargin(fitotronComboBox,new Insets(8,8,8,8));
        toolbox_VBox.setVgrow(fitotronTable, Priority.ALWAYS);
        VBox.setMargin(fitotronTable,new Insets(0,8,8,8));
        toolbox_VBox.setVgrow(fitotronSeriesList, Priority.NEVER);
        VBox.setMargin(fitotronSeriesList,new Insets(0,8,8,8));

        StackPane.setAlignment(fitotronConfidenceInterval, Pos.TOP_RIGHT);
        StackPane.setMargin(fitotronConfidenceInterval, new Insets(30,30,0,0));

        main_BorderPane.setCenter(center_StackPane);

        initializeComponents();
    }

    //--------------------------------------------------------------------------------------------------------------

    //Método de inicialização dos componentes:

    private void initializeComponents() {

        fitotronFileManager.initialize();
        fitotronCalculator.initialize();
        fitotronComboBox.initialize();
        fitotronTable.initialize();
        fitotronSeriesList.initialize();

        fitotronChart.initialize();
        fitotronConfidenceInterval.initialize();

        App.getStage().setOnCloseRequest(event -> {
            fitotronFileManager.saveAndClose();
            event.consume();
        });

        logs.addListener((ListChangeListener<Text>) c -> {
            if(c.next()){
                log_TextFlow.getChildren().add(c.getAddedSubList().get(0));
                log_ScrollPane.layout();
                log_ScrollPane.setVvalue(log_TextFlow.getHeight());
            }
        });
    }

    //--------------------------------------------------------------------------------------------------------------

    //Métodos relacionados a eventos da interface do usuário:

    @FXML
    public void onOpenMenuItemClick(ActionEvent actionEvent) {
        fitotronFileManager.open();
    }

    @FXML
    public void onImportMenuItemClick(ActionEvent event){
        fitotronFileManager.importData();
    }

    @FXML
    public void onSaveAsMenuItemClick(ActionEvent event) {
       fitotronFileManager.saveAs();
    }

    @FXML
    public void onSaveMenuItemClick(ActionEvent event) {
       fitotronFileManager.save();
    }

    @FXML
    public void onExportMenuItemClick(ActionEvent event) {
       fitotronFileManager.exportData();
    }

    @FXML
    public void onExitMenuItemClick(ActionEvent event) {
        fitotronFileManager.saveAndClose();
    }

    @FXML
    public void onPointsCheckMenuItemClick(ActionEvent event){
        if(event.getSource()==pointsNone_CheckMenuItem){
            pointsNone_CheckMenuItem.setSelected(true);
            pointsSquare_CheckMenuItem.setSelected(false);
            pointsCircle_CheckMenuItem.setSelected(false);
            pointsTriangle_CheckMenuItem.setSelected(false);
            pointAppearance.setValue(NO_MARK_APPEARANCE);
        }else if (event.getSource()==pointsSquare_CheckMenuItem){
            pointsNone_CheckMenuItem.setSelected(false);
            pointsSquare_CheckMenuItem.setSelected(true);
            pointsCircle_CheckMenuItem.setSelected(false);
            pointsTriangle_CheckMenuItem.setSelected(false);
            pointAppearance.setValue(SQUARE_MARK_APPEARANCE);
        }else if(event.getSource()==pointsCircle_CheckMenuItem){
            pointsNone_CheckMenuItem.setSelected(false);
            pointsSquare_CheckMenuItem.setSelected(false);
            pointsCircle_CheckMenuItem.setSelected(true);
            pointsTriangle_CheckMenuItem.setSelected(false);
            pointAppearance.setValue(CIRCLE_MARK_APPEARANCE);
        }else if(event.getSource()==pointsTriangle_CheckMenuItem){
            pointsNone_CheckMenuItem.setSelected(false);
            pointsSquare_CheckMenuItem.setSelected(false);
            pointsCircle_CheckMenuItem.setSelected(false);
            pointsTriangle_CheckMenuItem.setSelected(true);
            pointAppearance.setValue(TRIANGLE_MARK_APPEARANCE);
        }
    }

    @FXML
    public void onConfidenceIntervalCheckMenuItemClick(ActionEvent event){
        if(event.getSource()==confidenceIntervalNone_CheckMenuItem){
            confidenceIntervalNone_CheckMenuItem.setSelected(true);
            confidenceIntervalSquare_CheckMenuItem.setSelected(false);
            confidenceIntervalCircle_CheckMenuItem.setSelected(false);
            confidenceIntervalTriangle_CheckMenuItem.setSelected(false);
            ciAppearance.setValue(NO_MARK_APPEARANCE);
        }else if(event.getSource()==confidenceIntervalSquare_CheckMenuItem){
            confidenceIntervalNone_CheckMenuItem.setSelected(false);
            confidenceIntervalSquare_CheckMenuItem.setSelected(true);
            confidenceIntervalCircle_CheckMenuItem.setSelected(false);
            confidenceIntervalTriangle_CheckMenuItem.setSelected(false);
            ciAppearance.setValue(SQUARE_MARK_APPEARANCE);
        }else if(event.getSource()==confidenceIntervalCircle_CheckMenuItem){
            confidenceIntervalNone_CheckMenuItem.setSelected(false);
            confidenceIntervalSquare_CheckMenuItem.setSelected(false);
            confidenceIntervalCircle_CheckMenuItem.setSelected(true);
            confidenceIntervalTriangle_CheckMenuItem.setSelected(false);
            ciAppearance.setValue(CIRCLE_MARK_APPEARANCE);
        }else if(event.getSource()==confidenceIntervalTriangle_CheckMenuItem){
            confidenceIntervalNone_CheckMenuItem.setSelected(false);
            confidenceIntervalSquare_CheckMenuItem.setSelected(false);
            confidenceIntervalCircle_CheckMenuItem.setSelected(false);
            confidenceIntervalTriangle_CheckMenuItem.setSelected(true);
            ciAppearance.setValue(TRIANGLE_MARK_APPEARANCE);
        }
    }

    @FXML
    public void onAboutMenuItemClick(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("about.fxml"));
            DialogPane pane = loader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Sobre o software");

            dialog.setHeight(250);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            Optional<ButtonType> buttonType = dialog.showAndWait();
            if(buttonType.get() == ButtonType.OK)
                dialog.close();

        }catch (IOException e){
            System.out.println("Erro no diálogo sobre o software!");
        }
    }

    //--------------------------------------------------------------------------------------------------------------

    //Métodos auxiliares:

    private void setEnabled(boolean isEnabled){

        this.isEnabled = isEnabled;

        boolean isDisabled = !isEnabled;

        save_MenuItem.setDisable(isDisabled);
        saveAs_MenuItem.setDisable(isDisabled);
        export_MenuItem.setDisable(isDisabled);

        points_SubMenu.setDisable(isDisabled);
        pointsNone_CheckMenuItem.setDisable(isDisabled);
        pointsSquare_CheckMenuItem.setDisable(isDisabled);
        pointsCircle_CheckMenuItem.setDisable(isDisabled);
        pointsTriangle_CheckMenuItem.setDisable(isDisabled);

        confidenceInterval_SubMenu.setDisable(isDisabled);
        confidenceIntervalNone_CheckMenuItem.setDisable(isDisabled);
        confidenceIntervalSquare_CheckMenuItem.setDisable(isDisabled);
        confidenceIntervalCircle_CheckMenuItem.setDisable(isDisabled);
        confidenceIntervalTriangle_CheckMenuItem.setDisable(isDisabled);
    }
}