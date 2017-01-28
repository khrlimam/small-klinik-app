package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.contract.PatientFormContract;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 28/01/17.
 */
public class PatientFormDialog implements Initializable, PatientFormContract {

    private int noRm;

    @FXML
    PatientForm patientFormController;

    public void initialize(URL location, ResourceBundle resources) {
        patientFormController.setFormContract(this);
    }

    public void onPositiveButtonClicked() {
        try {
            Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
            pasienDao.update(patientFormController.getPasien());
            Log.i(getClass(), "pasien = "+patientFormController.getPasien());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
