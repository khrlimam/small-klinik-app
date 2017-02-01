package com.klinik.dev;

/**
 * Created by khairulimam on 26/01/17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainMenu extends Application {

    private static final String MAIN_UI = "/uis/main.fxml";

    public static Stage PRIMARY_STAGE;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        this.PRIMARY_STAGE = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_UI));
        primaryStage.setTitle(Util.APP_NAME);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
