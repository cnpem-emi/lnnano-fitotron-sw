package org.cnpem.fitotron.combos;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class FitotronConfidenceInterval extends HBox {

    private ObservableList<String> confidenceIntervals;
    private StringProperty confidence;

    private Label label;
    private ComboBox comboBox;

    public FitotronConfidenceInterval(
            ObservableList<String> confidanceIntervals,
            StringProperty confidance
    ){
        super();

        //Coletando a referência do observável de confiança:
        this.confidence = confidance;
        this.confidenceIntervals = confidanceIntervals;

        this.label = new Label("Nível de Confiança");
        this.comboBox = new ComboBox();

        //Desabilitando a caixa de seleção:
        this.setDisable(true);

        //Definindo a largura da caixa de seleção:
        comboBox.setPrefWidth(80);

        //Definindo o alinhamento dos valores dentro do objeto de texto:
        label.setAlignment(Pos.BASELINE_CENTER);

        //Definindo a aparência do campo de texto/numérico:
        this.setStyle(
                "-fx-background-color: #F0F0F0;" +
                "-fx-border-color: #cdcfd1;" +
                "-fx-border-insets: 0;" +
                "-fx-border-width: 1;"
        );

        this.getChildren().add(label);
        HBox.setHgrow(label, Priority.NEVER);
        HBox.setMargin(label,new Insets(8,8,8,0));

        this.getChildren().add(comboBox);
        HBox.setHgrow(comboBox, Priority.NEVER);
        HBox.setMargin(comboBox,new Insets(8,0,8,0));

        this.setMaxWidth(223);
        this.setMaxHeight(15);
        this.setAlignment(Pos.CENTER);
    }

    public void initialize(){

        //Adicionando a lista de opções da caixa de seleção:
        comboBox.setItems(confidenceIntervals);

        //Definindo o Listener do observável do índice de confiança:
        confidence.addListener((observable, oldValue, newValue) -> {
            comboBox.getSelectionModel().select(confidenceIntervals.indexOf(confidence.getValue()));
        });

        //Definindo EventHandler da ação de selecionar um novo ítem:
        comboBox.setOnAction(event -> {
            confidence.set((String) comboBox.getSelectionModel().getSelectedItem());
            event.consume();
        });
    }

    public void start(){

        //Habilitando o campo caso esteja desabilitada:
        if(this.isDisable())
            this.setDisable(false);

        //Selecionando o ítem correspondente à confiança coletada:
        comboBox.getSelectionModel().select(confidenceIntervals.indexOf(confidence.getValue()));
    }
}
