package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.App;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;
import tray.notification.NotificationType;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 26/01/17.
 */
@Data
public class Main implements Initializable {

    private Stage pasienStage, tindakanDanRuleStage;
    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private List<Pasien> pasiens = pasienDao.queryForAll();

    @FXML
    private TblSemuaPasien tblSemuaPasienController;

    public Main() throws SQLException {
    }

    public void initialize(URL location, ResourceBundle resources) {
        pasienStage = makeDialogStage("/uis/patientdialog.fxml", "Tambah pasien", App.PRIMARY_STAGE);
        tindakanDanRuleStage = makeDialogStage("/uis/tindakanruleformdialog.fxml", "Tambah rule dan tindakan", App.PRIMARY_STAGE);
    }

    private Stage makeDialogStage(String fxml, String title, Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
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
}
