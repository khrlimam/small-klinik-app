package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Util;
import com.klinik.dev.contract.PopulateFxWithThis;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Data;
import tray.notification.NotificationType;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 26/01/17.
 */
@Data
public class Main implements Initializable, PopulateFxWithThis {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private ObservableList<Pasien> dataPasien;
    private Stage pasienStage, tindakanDanRuleStage;

    //bullshit with the oo rules! ignore it for critical purpose!
    PatientFormDialog patientFormDialog;

    @FXML
    private TblSemuaPasien semuaPasienController;
    @FXML
    private TblPasienCheckupHariIni pasienCheckupHariIniController;

    public Main() throws SQLException {
    }

    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void showFormTambahPasien() {
        if (pasienStage != null)
            pasienStage.showAndWait();
    }

    @FXML
    private void showFormTambahTindakanDanRule() {
        if (tindakanDanRuleStage != null)
            tindakanDanRuleStage.showAndWait();
    }

    @FXML
    private void searchPatient() {
        Util.showNotif("Coba", "Coba pesan", NotificationType.WARNING);
    }

    @Override
    public void populate(Object data) {
        semuaPasienController.getTblPasien().getItems().add((Pasien) data);
    }
}
