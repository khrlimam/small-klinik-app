package com.klinik.dev.util;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.sql.SQLException;

/**
 * Created by khairulimam on 26/01/17.
 */
public class Util {
    public static final String APP_NAME = "Klinik";
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final String TABLE_CONTROL_TOOLTIP = "Pilih baris kemudian tekan:\n1. D/d untuk menghapus\n2. Double klik pada cell untuk mengubah data\n3. C/c untuk checkup\n4. S/s untuk melihat rincian pasien";
    public static final int MAX_TIME_ABSENCE = 2;

    public static Class[] classes = {Pasien.class, RiwayatTindakan.class, Rule.class, Tindakan.class, TindakanRule.class};

    public static void showNotif(String title, String message, NotificationType type) {
        TrayNotification tray = new TrayNotification(title, message, type);
        tray.showAndDismiss(Duration.seconds(1));
    }

    public static void migrateUp() {
        ConnectionSource cs = DB.getDB();
        for (Class c : classes) {
            try {
                TableUtils.createTableIfNotExists(cs, c);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void migrateDown() {
        ConnectionSource cs = DB.getDB();
        for (Class c : classes) {
            try {
                TableUtils.dropTable(cs, c, false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Tooltip tableControlTooltip() {
        return new Tooltip(TABLE_CONTROL_TOOLTIP);
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

}
