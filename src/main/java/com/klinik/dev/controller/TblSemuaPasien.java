package com.klinik.dev.controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.Util;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.Tindakan;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lombok.Data;
import tray.notification.NotificationType;

import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TblSemuaPasien implements Initializable {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    Dao<Tindakan, Integer> tindakans = DaoManager.createDao(DB.getDB(), Tindakan.class);

    private List<Pasien> pasiens = pasienDao.queryForAll();
    private List<Tindakan> tindakanList = tindakans.queryForAll();

    @FXML
    private TextField tfFilterTable;
    @FXML
    private TableView<Pasien> tblPasien;
    @FXML
    private TableColumn<Pasien, String> noRmColumn, namaColumn, noTelponColumn, alamatColumn, jadwalCheckupColumn;

    public TblSemuaPasien() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tblPasien.setItems(FXCollections.observableArrayList(pasiens));
        tblPasien.setTooltip(Util.tableControlTooltip());
        setUpTableColumnItems();
        Log.i(getClass(), pasiens.toString());
    }

    public void populateDataPasien(Pasien newPasien) {
        pasiens.add(newPasien);
        tblPasien.getItems().add(newPasien);
    }

    private void setUpTableColumnItems() {
        noRmColumn.setCellValueFactory(new PropertyValueFactory("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory("nama"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory("noTelepon"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory("alamat"));
        jadwalCheckupColumn.setCellValueFactory(new PropertyValueFactory("jadwalSelanjutnya"));
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        Optional<ButtonType> decision;
        switch (keyEvent.getCode()) {
            case D:
                decision = Util.deleteConfirmation().showAndWait();
                if (decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    try {
                        int selectedIndex = tblPasien.getSelectionModel().getSelectedIndex();
                        int deleted = pasienDao.delete(pasiens.get(selectedIndex));
                        tblPasien.getItems().remove(selectedIndex);
                        if (deleted == 1)
                            Util.showNotif("Berhasil", "Data telah dihapus", NotificationType.SUCCESS);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                return;
            case E:
                decision = Util.editConfirmation().showAndWait();
                if (decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE))
                    Log.i(getClass(), "Edit table with index " + tblPasien.getSelectionModel().getSelectedIndex());
                break;
            case C:
                ChoiceDialog<String> dialog = new ChoiceDialog<>(null, getTindakan());
                dialog.setTitle(String.format("Checkup %s", tblPasien.getSelectionModel().getSelectedItem().getNama()));
                dialog.setHeaderText("Pilih tindakan");
                Optional<String> selected = dialog.showAndWait();
                if (selected.isPresent()) {
                    String selectedChoice = selected.get();
                    int selectedIndex = Integer.parseInt(selectedChoice.split(" ")[0])-1;
                    Tindakan selectedTindakan = tindakanList.get(selectedIndex);
                    Pasien selectedPasien = pasiens.get(tblPasien.getSelectionModel().getSelectedIndex());
                    Log.i(getClass(), selectedPasien.toString());
                }
                break;
        }
    }

    private List<String> getTindakan() {
        List<String> tindakans = new ArrayList<>();
        int index = 0;
        for (Tindakan tindakan : tindakanList)
            tindakans.add(String.format("%d %s", ++index, tindakan.getTindakan().getNamaTindakan()));
        return tindakans;
    }
}
