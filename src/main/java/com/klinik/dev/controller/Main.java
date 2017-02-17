package com.klinik.dev.controller;

import com.klinik.dev.App;
import com.klinik.dev.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.Data;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 26/01/17.
 */
@Data
public class Main implements Initializable {

    private Stage pasienStage, tindakanDanRuleStage, monitorPemasukanStage;

    public void initialize(URL location, ResourceBundle resources) {
        pasienStage = Util.makeDialogStage(getClass().getResource("/uis/patientdialog.fxml"), "Tambah pasien", App.PRIMARY_STAGE);
        tindakanDanRuleStage = Util.makeDialogStage(getClass().getResource("/uis/tindakanruleformdialog.fxml"), "Tambah rule dan tindakan", App.PRIMARY_STAGE);
        monitorPemasukanStage = Util.makeDialogStage(getClass().getResource("/uis/monitorpemasukan.fxml"), "Monitor pemasukan", App.PRIMARY_STAGE);
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
    private void showMonitorPemasukan() {
        if (monitorPemasukanStage != null)
            monitorPemasukanStage.showAndWait();
    }
}
