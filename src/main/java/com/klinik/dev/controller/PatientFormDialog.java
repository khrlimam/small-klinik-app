package com.klinik.dev.controller;

import com.google.common.io.Files;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.enums.OPERATION_TYPE;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.PasienEvent;
import com.klinik.dev.util.FileUtil;
import com.klinik.dev.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.Data;
import tray.notification.NotificationType;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 28/01/17.
 */
@Data
public class PatientFormDialog implements Initializable, OnOkFormContract {

    private Dao<Pasien, Integer> pasienDao = Pasien.getDao();
    private Dao<RiwayatTindakan, Integer> riwayatTindakanDao = RiwayatTindakan.getDao();

    @FXML
    PatientForm patientFormController;

    public PatientFormDialog() throws SQLException {
    }

    public void initialize(URL location, ResourceBundle resources) {
        patientFormController.setFormContract(this);
    }

    public void onPositive() {
        try {
            Pasien pasien = patientFormController.getPasien();
            if (patientFormController.getFoto() != null) {
                File fileSrc = patientFormController.getFoto();
                String fileExtension = Files.getFileExtension(fileSrc.getName());
                File fileDestination = FileUtil.generateFileToUploadFolder(fileExtension);
                FileUtil.uploadFile(fileSrc, fileDestination);
                pasien.setFotoPath(fileDestination.getAbsolutePath());
            }
            RiwayatTindakan riwayatTindakan = patientFormController.getRiwayatTindakan(pasien);
            pasienDao.create(pasien);
            riwayatTindakanDao.create(riwayatTindakan);
            Util.showNotif("Sukses", "Berhasil menambahkan data", NotificationType.SUCCESS);
            EventBus.getInstance().post(new PasienEvent(pasien, OPERATION_TYPE.CREATE));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
