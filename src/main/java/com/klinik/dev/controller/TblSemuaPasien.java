package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.Util;
import com.klinik.dev.bussiness.BRule;
import com.klinik.dev.datastructure.SearchableCollections;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.events.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Data;
import org.joda.time.DateTime;
import tray.notification.NotificationType;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TblSemuaPasien implements Initializable {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private Dao<Tindakan, Integer> tindakans = DaoManager.createDao(DB.getDB(), Tindakan.class);
    private Dao<RiwayatTindakan, Integer> riwayatTindakans = DaoManager.createDao(DB.getDB(), RiwayatTindakan.class);

    private List<Pasien> pasiens = pasienDao.queryForAll();
    private List<Tindakan> tindakanList = tindakans.queryForAll();

    @FXML
    private TextField tfFilterTable;
    @FXML
    private TableView<Pasien> tblPasien;
    @FXML
    private TableColumn<Pasien, String> noRmColumn, namaColumn, noTelponColumn, alamatColumn;
    @FXML
    private TableColumn<Pasien, Pasien> jadwalCheckupColumn;

    public TblSemuaPasien() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        com.klinik.dev.events.EventBus.getInstance().register(this);
        tblPasien.setItems(FXCollections.observableArrayList(pasiens));
        tblPasien.setTooltip(Util.tableControlTooltip());
        jadwalCheckupColumn.setCellFactory(column -> {
            return new TableCell<Pasien, Pasien>() {
                @Override
                protected void updateItem(Pasien item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        // Format date.
                        setText(item.getJadwalSelanjutnya());
                        for (BRule bRule : item.getTindakan().getTindakan().getBRules()) {
                            if (bRule.isMoreThanPeriod(item.getCheckupTerakhir())) {
                                setTextFill(Paint.valueOf("#ecf0f1"));
                                setStyle("-fx-background-color: #e74c3c");
                                break;
                            } else {
                                setTextFill(Color.BLACK);
                                setStyle("");
                            }
                        }
                    }
                }
            };
        });
        setUpTableColumnItems();
    }

    private void setUpTableColumnItems() {
        noRmColumn.setCellValueFactory(new PropertyValueFactory("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory("nama"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory("noTelepon"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory("alamat"));
        jadwalCheckupColumn.setCellValueFactory(new PropertyValueFactory("pasien"));
    }

    public void onKeyPressed(KeyEvent keyEvent) throws SQLException {
        Optional<ButtonType> decision;
        Pasien selectedPasien = pasiens.get(tblPasien.getSelectionModel().getSelectedIndex());
        switch (keyEvent.getCode()) {
            case D:
                decision = Util.deleteConfirmation().showAndWait();
                boolean isOK = decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE);
                if (isOK) {
                    int deleted = pasienDao.delete(selectedPasien);
                    if (deleted == 1) {
                        EventBus.getInstance().post(new PasienEvent(selectedPasien, OperationType.DELETE));
                        Util.showNotif("Berhasil", "Data telah dihapus", NotificationType.SUCCESS);
                    }
                }
                return;
            case E:
                decision = Util.editConfirmation().showAndWait();
                if (decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE))
                    Log.i(getClass(), "josh");
                break;
            case C:
                ChoiceDialog tindakanChoiceDialog = new ChoiceDialog(null, getListTindakanString());
                tindakanChoiceDialog.setHeaderText("Pilih tindakan");
                tindakanChoiceDialog.setTitle(String.format("Checkup %s", tblPasien.getSelectionModel().getSelectedItem().getNama()));
                Optional<String> selected = tindakanChoiceDialog.showAndWait();
                if (selected.isPresent()) {
                    String selectedChoice = selected.get();
                    int selectedIndex = Integer.parseInt(selectedChoice.split("\\.")[0]) - 1;
                    Tindakan selectedTindakan = tindakanList.get(selectedIndex);
                    selectedPasien.setTindakan(selectedTindakan);
                    selectedPasien.setCheckupTerakhir(DateTime.now());
                    int updated = pasienDao.update(selectedPasien);
                    if (updated == 1) {
                        RiwayatTindakan newRiwayatTindakan = new RiwayatTindakan();
                        newRiwayatTindakan.setTindakan(selectedTindakan);
                        newRiwayatTindakan.setPasien(selectedPasien);
                        riwayatTindakans.create(newRiwayatTindakan);
                        EventBus.getInstance().post(new PasienEvent(selectedPasien, OperationType.UPDATE));
                        EventBus.getInstance().post(new RiwayatTindakanEvent(newRiwayatTindakan, OperationType.CREATE));
                        Util.showNotif("Sukses", "Data pasien telah disimpan", NotificationType.SUCCESS);
                    }
                }
                break;
        }
    }

    private List<String> getListTindakanString() {
        List<String> l = new ArrayList<>();
        int index = 0;
        for (Tindakan tindakan : tindakanList)
            l.add(String.format("%d. %s", ++index, tindakan.getTindakan().getNamaTindakan()));
        return l;
    }

    @Subscribe
    public void onPasien(PasienEvent pasienEvent) {
        Pasien pasien = pasienEvent.getPasien();
        if (pasienEvent.getOPERATION_TYPE() == OperationType.CREATE) {
            this.pasiens.add(pasien);
            this.tblPasien.getItems().add(pasien);
        } else if (pasienEvent.getOPERATION_TYPE() == OperationType.UPDATE) {
            int index = SearchableCollections.binarySearch(pasiens, pasien);
            this.pasiens.set(index, pasien);
            this.tblPasien.getItems().set(index, pasien);
        } else if (pasienEvent.getOPERATION_TYPE() == OperationType.DELETE) {
            int index = SearchableCollections.binarySearch(pasiens, pasien);
            this.pasiens.remove(index);
            this.tblPasien.getItems().remove(index);
        }
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
        int index;
        Tindakan tindakan = tindakanEvent.getTindakan();
        switch (tindakanEvent.getOPERATION_TYPE()) {
            case UPDATE:
                index = SearchableCollections.binarySearch(tindakanList, tindakan);
                if (index > -1)
                    tindakanList.set(index, tindakan);
                break;
            case DELETE:
                index = SearchableCollections.binarySearch(tindakanList, tindakan);
                if (index > -1)
                    tindakanList.remove(index);
                break;
            case CREATE:
                tindakanList.add(tindakan);
                break;
        }
    }
}
