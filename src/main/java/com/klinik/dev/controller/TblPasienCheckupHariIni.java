package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.bussiness.BRule;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Data;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TblPasienCheckupHariIni implements Initializable {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private List<Pasien> allPasiens = pasienDao.queryForAll();

    @FXML
    private TextField tfFilterTable;
    @FXML
    private TableView<Pasien> tblPasien;
    @FXML
    private TableColumn<Pasien, String> noRmColumn, namaColumn, noTelponColumn, alamatColumn;

    public TblPasienCheckupHariIni() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Log.i(getClass(), jadwalPasienHariIni().toString());
        tblPasien.setItems(FXCollections.observableArrayList(jadwalPasienHariIni()));
        setUpTableColumnItems();
    }

    private List<Pasien> jadwalPasienHariIni() {
        List<Pasien> pasiensHariIni = new ArrayList<>();
        for (Pasien p : allPasiens) {
            if (p.getTindakan() != null)
                for (BRule bRule : p.getTindakan().getTindakan().getBRules()) {
                    if (bRule.isTodayCheckup(p.getCheckupTerakhir()))
                        pasiensHariIni.add(p);
                }
        }
        return pasiensHariIni;
    }

    private void setUpTableColumnItems() {
        noRmColumn.setCellValueFactory(new PropertyValueFactory("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory("nama"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory("noTelepon"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory("alamat"));
    }

}
