package com.klinik.dev.util;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.joda.time.DateTime;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by khairulimam on 26/01/17.
 */
public class Util {
    public static final String APP_NAME = "Klinik";
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final String TABLE_CONTROL_TOOLTIP = "Pilih baris kemudian tekan:\n1. D/d untuk menghapus\n2. Double klik pada cell untuk mengubah data\n3. C/c untuk checkup\n4. S/s untuk melihat rincian pasien";
    public static final int MAX_TIME_ABSENCE = 2;
    public static final String DATE_TIME_PATTERN = String.format("%s, HH:mm:ss", DATE_PATTERN);

    public static Class[] classes = {Pasien.class, RiwayatTindakan.class, Rule.class, Tindakan.class, TindakanRule.class};

    public static void showNotif(String title, String message, NotificationType type) {
        TrayNotification tray = new TrayNotification(title, message, type);
        tray.showAndDismiss(Duration.seconds(1));
    }

    public static void migrateUp() {
        ConnectionSource cs = DB.getDB();
        Arrays.stream(classes).forEach(aClass -> {
            try {
                TableUtils.createTableIfNotExists(cs, aClass);
            } catch (SQLException e) {

            }
        });
    }

    public static void migrateDown() {
        ConnectionSource cs = DB.getDB();
        Arrays.stream(classes).forEach(aClass -> {
            try {
                TableUtils.dropTable(cs, aClass, false);
            } catch (SQLException e) {

            }
        });
    }

    public static Tooltip tableControlTooltip() {
        return new Tooltip(TABLE_CONTROL_TOOLTIP);
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
            riwayatTindakan.setDiagnosis("Diagnosis tes " + i);
            riwayatTindakan.setPasien(p);
            riwayatTindakan.setTindakan(t);
            riwayatTindakan.setTarif(pen);
            riwayatTindakan.setTglCheckup(initDate);
            rd.create(riwayatTindakan);
            pen = 50000 + random.nextInt(30000);
            initDate = initDate.plusMonths(1);
        }
    }

    public static Alert setUpDialog(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    public static Alert deleteConfirmation() {
        return setUpDialog("Konfirmasi", "Yakin ingin menghapus data?", "Hati-hati dengan pilihan anda!", Alert.AlertType.CONFIRMATION);
    }

    public static Alert editConfirmation() {
        return setUpDialog("Konfirmasi", "Yakin ingin mengubah data?", null, Alert.AlertType.CONFIRMATION);
    }

    public static Stage makeDialogStage(URL FXMLLocation, String title, Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(FXMLLocation);
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
