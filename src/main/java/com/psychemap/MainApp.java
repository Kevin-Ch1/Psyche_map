package com.psychemap;

import javafx.application.Application;
import javafx.stage.Stage;
import com.psychemap.ui.controller.SceneManager;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.iniciar(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
