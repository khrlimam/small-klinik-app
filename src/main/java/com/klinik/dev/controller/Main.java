package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.contract.PatientFormContract;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 26/01/17.
 */
public class Main implements Initializable, PatientFormContract {
    DateTime dayBeforeToday;
    DateTime dayNow;
    DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

    @FXML
    private PatientForm patientFormController;

    public void initialize(URL location, ResourceBundle resources) {
        dayNow = new DateTime();
        dayBeforeToday = new DateTime().plusDays(10);
        patientFormController.setFormContract(this);
    }

    @FXML
    private void searchPatient(ActionEvent event) {
        System.out.println("Josh");
    }

    public void onPositiveButtonClicked() {
        try {
            Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
            int  is = pasienDao.create(patientFormController.getPasien());
            Log.i(getClass(), "pasien = "+patientFormController.getPasien());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
