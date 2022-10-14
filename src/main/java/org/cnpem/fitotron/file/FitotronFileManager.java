package org.cnpem.fitotron.file;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.List;

public class FitotronFileManager {

    private Parent parent;

    private ObservableFile savedFile;

    private StringProperty confidence;
    private ObservableList<Text> logs;
    private ObservableList<File> files;
    private ObservableList<FitotronData> dataArrayList;

    public FitotronFileManager(
            Parent parent,
            ObservableList<Text> logs,
            ObservableList<File> files,
            ObservableFile savedFile,
            StringProperty confidence,
            ObservableList<FitotronData> dataArrayList
    ){
        this.parent = parent;
        this.logs = logs;
        this.files = files;
        this.savedFile = savedFile;
        this.confidence = confidence;
        this.dataArrayList = dataArrayList;
    }

    public void initialize(){
        //Inicializador vazio...
    }

    public void open(){

        //Perguntando ao usuário se deseja salvar o projeto antes de abrir um novo arquivo:
        if(!dataArrayList.isEmpty()){
            boolean save = showConfirmationDialog(
                    "Mensagem",
                    "Deseja salvar os dados antes de abrir um novo arquivo?",
                    "Suas alterações serão perdidas se não salvá-las.");
            if(save){
                saveAs();
            }
        }

        ///Selecionando o arquivo da análise:

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir arquivo de dados");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Arquivo do Fitotron",
                "*.fit"
        ));

        File file = fileChooser.showOpenDialog(parent.getScene().getWindow());

        //Se um aquivo for selecionado:
        if(file!=null){
            files.clear();
            files.add(file);
        }
    }

    public void save(){
        //Realizando o processo de salvamento dos dados:
        if(savedFile.getValue()==null)
            savedFile.setValue(createNewFile("Arquivo do Fitotron","*.fit"));
        saveData(savedFile.getValue());
    }

    public void saveAs(){
        //Realizando o processo de salvamento dos dados:
        savedFile.setValue(createNewFile("Arquivo do Fitotron","*.fit"));
        saveData(savedFile.getValue());
    }

    public void importData(){

        //Perguntando ao usuário se deseja salvar o projeto antes da importação:
        if(!dataArrayList.isEmpty()){
            boolean save = showConfirmationDialog(
                    "Mensagem",
                    "Deseja salvar os dados antes da importação?",
                    "Suas alterações serão perdidas se não salvá-las.");
            if(save){
                saveAs();
            }
        }

        ///Selecionando o(s) arquivo(s):

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar dados");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Tabela(s) de dados",
                "*.csv"
        ));

        List<File> files = fileChooser.showOpenMultipleDialog(parent.getScene().getWindow());

        //Se nenhum aquivo for selecionado, fazer nada:
        if(files!=null){
            this.files.clear();
            this.files.addAll(files);
        }
    }

    public void exportData(){
        //Realizando o processo de exportação dos dados para o formato de planilha do Excel:
        saveData(createNewFile("Tabela de dados","*.csv"));
    }

    public void saveAndClose(){

        if(files.isEmpty())
            System.exit(0);

        //Declarando e definindo os botões do diálogo de fechamento do programa:
        ButtonType yesBttn = new ButtonType("Sim", ButtonBar.ButtonData.YES);
        ButtonType noBttn = new ButtonType("Não", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        //Declarando e definindo os atributos do diálogo de fechamento do programa:
        Alert warningAlertDialog = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Suas alterações serão perdidas se não salvá-las.",
                yesBttn,
                cancelButton,
                noBttn
        );
        warningAlertDialog.setTitle("Mensagem");
        warningAlertDialog.setHeaderText("Deseja salvar os dados antes de sair?");

        //Exibindo o diálogo de fechamento do programa, aguardando a resposta do usuário e coletando o botão pressionado:
        ButtonType buttonType = warningAlertDialog.showAndWait().get();

        //Caso o botão "Não" tenha sido pressionado, fechar o programa:
        if(buttonType==noBttn){

            //Fechando o programa:
            System.exit(0);

        //Caso o botão "Sim" tenha sido pressionado, salvar e fechar o programa:
        }else if(buttonType==yesBttn){

            //Realizando o processo de salvamento dos dados:
            File file = createNewFile("Arquivo do Fitotron","*.fit");
            //Caso um arquivo tenha sido criado/selecionado para o salvamento:
            if(file!=null){
                //Gravando os dados no arquivo:
                saveData(file);
                //Fechando o programa:
                System.exit(0);
            }
        }
    }

    protected File createNewFile(String description, String extension){

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Salvar dados como");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));

        File newFile = fileChooser.showSaveDialog(parent.getScene().getWindow());
        if(newFile!=null && !newFile.getName().contains(".")) {
            newFile = new File(newFile.getAbsolutePath() + extension);
        }

        return newFile;
    }

    protected void saveData(File file){

        if (file == null)
            return;

        try{
            PrintWriter writer;
            writer = new PrintWriter(file);

            writer.println(
                    "Horário," +
                    "Tempo [min]," +
                            "Frequência [Hz]," +
                            "Impedância (Tamanho da Amostra)," +
                            "Impedância (Média) [Ohm]," +
                            "Impedância (Desvio Padrão) [Ohm]," +
                            "Impedância (Erro para " + confidence.getValue() + " de Confiança) [Ohm]," +
                            "Temperatura (Tamanho da Amostra)," +
                            "Temperatura (Média) [°C]," +
                            "Temperatura (Desvio Padrão) [°C]," +
                            "Temperatura (Erro para " + confidence.getValue() + " de Confiança) [°C]," +
                            "Umidade (Tamanho da Amostra)," +
                            "Umidade (Média) [%]," +
                            "Umidade (Desvio Padrão) [%]," +
                            "Umidade (Erro para " + confidence.getValue() + " de Confiança) [%]," +
                            "Massa (Tamanho da Amostra)," +
                            "Massa (Média) [mg]," +
                            "Massa (Desvio Padrão) [mg]," +
                            "Massa (Erro para " + confidence.getValue() + " de Confiança) [mg]," +
                            "RWC (Tamanho da Amostra)," +
                            "RWC (Média) [%]," +
                            "RWC (Desvio Padrão) [%]," +
                            "RWC (Erro para " + confidence.getValue() + " de Confiança) [%]," +
                            "LWC (Tamanho da Amostra)," +
                            "LWC (Média) [%]," +
                            "LWC (Desvio Padrão) [%]," +
                            "LWC (Erro para " + confidence.getValue() + " de Confiança) [%]"
                    );

            for(FitotronData data : dataArrayList){
                writer.println(data.toString());
            }

            writer.close();

            logs.add(new Text(String.format("Tabela salva com sucesso em %s.\n", file.getAbsolutePath())));

        } catch (IOException ex){

            logs.add(new Text(String.format("Falha no salvamento da tabela em %s.\n", file.getAbsolutePath())));
            return;
        }
    }

    private boolean showConfirmationDialog(String title, String header, String content){

        //Declarando e definindo os botões do diálogo de fechamento do programa:
        ButtonType yesBttn = new ButtonType("Sim", ButtonBar.ButtonData.YES);
        ButtonType noBttn = new ButtonType("Não", ButtonBar.ButtonData.NO);

        Alert warningAlertDialog = new Alert(
                Alert.AlertType.CONFIRMATION,
                content,
                yesBttn,
                noBttn
        );

        warningAlertDialog.setTitle(title);
        warningAlertDialog.setHeaderText(header);

        if(warningAlertDialog.showAndWait().get()==yesBttn){
            return true;
        }else{
            return false;
        }
    }
}
