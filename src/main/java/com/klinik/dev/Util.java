package com.klinik.dev;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Rule;
import com.klinik.dev.db.model.Tindakan;
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

    public static Class[] classes = {Pasien.class, RiwayatTindakan.class, Rule.class, Tindakan.class};

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

}
