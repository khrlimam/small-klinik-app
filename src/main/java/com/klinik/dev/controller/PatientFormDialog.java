package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Util;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.Data;
import tray.notification.NotificationType;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 28/01/17.
 */
@Data
public class PatientFormDialog implements Initializable, OnOkFormContract {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);

    private int noRm;

    @FXML
    PatientForm patientFormController;

    public PatientFormDialog() throws SQLException {
    }

    public void initialize(URL location, ResourceBundle resources) {
        patientFormController.setFormContract(this);
    }

    public void onPositiveButtonClicked(Object object) {
        try {
            int created = pasienDao.create((Pasien) object);
            if (created == 1)
                Util.showNotif("Sukses", "Berhasil menambahkan data", NotificationType.SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
