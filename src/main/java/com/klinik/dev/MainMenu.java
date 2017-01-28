package com.klinik.dev;

/**
 * Created by khairulimam on 26/01/17.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.joda.time.DateTime;

import java.io.IOException;

public class MainMenu extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/uis/main.fxml"));
        primaryStage.setTitle(Util.APP_NAME);
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
}
