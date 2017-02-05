package com.klinik.dev;

/**
 * Created by khairulimam on 26/01/17.
 */

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.util.Log;
import com.klinik.dev.util.Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.joda.time.DateTime;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

public class App extends Application {

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
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    void generateData() throws SQLException {
        Dao<Pasien, Integer> pd = DaoManager.createDao(DB.getDB(), Pasien.class);
        Dao<RiwayatTindakan, Integer> rd = DaoManager.createDao(DB.getDB(), RiwayatTindakan.class);
        Dao<Tindakan, Integer> td = DaoManager.createDao(DB.getDB(), Tindakan.class);
        Tindakan t = td.queryBuilder().queryForFirst();
        double pen = 50000;
        DateTime initDate = DateTime.now().plusYears(1);
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            Pasien p = pd.queryBuilder().queryForFirst();
            p.setCheckupTerakhir(initDate);
            RiwayatTindakan riwayatTindakan = new RiwayatTindakan();
            riwayatTindakan.setDiagnosis("Diagnosis tes "+i);
            riwayatTindakan.setPasien(p);
            riwayatTindakan.setTindakan(t);
            riwayatTindakan.setTarif(pen);
            riwayatTindakan.setTglCheckup(initDate);
            rd.create(riwayatTindakan);
            pen = 50000+random.nextInt(30000);
            initDate = initDate.plusMonths(1);
        }
    }

}
