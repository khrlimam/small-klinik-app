package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
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
import org.joda.time.DateTime;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TblSemuaPasien implements Initializable {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private List<Pasien> pasiens = pasienDao.queryForAll();

    @FXML
    private TextField tfFilterTable;
    @FXML
    private TableView<Pasien> tblPasien;
    @FXML
    private TableColumn<Pasien, String> noRmColumn, namaColumn, noTelponColumn, alamatColumn, jadwalCheckupColumn;

    public TblSemuaPasien() throws SQLException {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tblPasien.setItems(FXCollections.observableArrayList(pasiens));
        setUpTableColumnItems();
        Log.i(getClass(), pasiens.toString());
    }

    private void setUpTableColumnItems() {
        noRmColumn.setCellValueFactory(new PropertyValueFactory("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory("nama"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory("noTelepon"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory("alamat"));
        jadwalCheckupColumn.setCellValueFactory(new PropertyValueFactory("jadwalSelanjutnya"));
    }
}
