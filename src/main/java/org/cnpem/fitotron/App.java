package org.cnpem.fitotron;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {

        this.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("plotter.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        //stage.getIcons().add(new Image("file:resources/org/cnpem/lnnano/images/icon.png"));
        stage.setTitle("LNnano - Fitotron");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setStageTitle(String title){
        stage.setTitle(title);
    }

    public static Stage getStage(){
        return stage;
    }
}