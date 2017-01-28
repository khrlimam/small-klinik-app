package com.klinik.dev;

/**
 * Created by khairulimam on 26/01/17.
 */

import com.dooapp.fxform.FXForm;
import com.j256.ormlite.table.TableUtils;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class MainMenu extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        Parent root = FXMLLoader.load(getClass().getResource("/uis/main.fxml"));
        primaryStage.setTitle(Util.APP_NAME);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setMaxHeight(600);
        primaryStage.setMaxWidth(800);
        primaryStage.setMaximized(false);
        primaryStage.show();
//        TableUtils.dropTable(DB.getDB(), Pasien.class, false);
        TableUtils.createTableIfNotExists(DB.getDB(), Pasien.class);
    }

}
