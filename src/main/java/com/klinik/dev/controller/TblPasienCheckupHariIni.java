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
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.OperationType;
import com.klinik.dev.events.PasienEvent;
import com.klinik.dev.events.TindakanEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lombok.Data;
import org.joda.time.DateTime;
import tray.notification.NotificationType;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TblPasienCheckupHariIni implements Initializable {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private Dao<Tindakan, Integer> tindakans = DaoManager.createDao(DB.getDB(), Tindakan.class);
    private Dao<RiwayatTindakan, Integer> riwayatTindakans = DaoManager.createDao(DB.getDB(), RiwayatTindakan.class);

    private List<Pasien> pasiens = pasienDao.queryForAll();
    private List<Pasien> pasienhariIni = new ArrayList<>();
    private List<Tindakan> tindakanList = tindakans.queryForAll();

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
        EventBus.getInstance().register(this);
        tblPasien.setItems(FXCollections.observableArrayList(jadwalPasienHariIni()));
        setUpTableColumnItems();
    }

    private List<Pasien> jadwalPasienHariIni() {
        for (Pasien p : pasiens) {
            if (p.getTindakan() != null)
                for (BRule bRule : p.getTindakan().getTindakan().getBRules()) {
                    if (bRule != null)
                        if (bRule.isTodayCheckup(p.getCheckupTerakhir())) {
                            this.pasienhariIni.add(p);
                            break; //break the loop one condition is satisfied
                        }
                }
        }
        return this.pasienhariIni;
    }

    private void setUpTableColumnItems() {
        noRmColumn.setCellValueFactory(new PropertyValueFactory("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory("nama"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory("noTelepon"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory("alamat"));
    }

    public void onKeyPressed(KeyEvent keyEvent) throws SQLException {
        Optional<ButtonType> decision;
        Pasien selectedPasien = pasienhariIni.get(tblPasien.getSelectionModel().getSelectedIndex());
        switch (keyEvent.getCode()) {
            case D:
                decision = Util.deleteConfirmation().showAndWait();
                if (decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
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
                    Log.i(getClass(), "Edit table with index " + tblPasien.getSelectionModel().getSelectedIndex());
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
                    selectedPasien.setCheckupTerakhir(DateTime.now());
                    selectedPasien.setTindakan(selectedTindakan);
                    int updated = pasienDao.update(selectedPasien);
                    if (updated == 1) {
                        Util.showNotif("Sukses", "Data pasien telah disimpan", NotificationType.SUCCESS);
                        RiwayatTindakan newRiwayatTindakan = new RiwayatTindakan();
                        newRiwayatTindakan.setTindakan(selectedTindakan);
                        newRiwayatTindakan.setPasien(selectedPasien);
                        riwayatTindakans.create(newRiwayatTindakan);
                        EventBus.getInstance().post(new PasienEvent(selectedPasien, OperationType.UPDATE));
                    }
                }
                break;
        }
    }

    private boolean isPasienMustCheckupHariIni(Pasien selectedPasien) {
        for (BRule bRule : selectedPasien.getTindakan().getTindakan().getBRules()) {
            if (bRule.isTodayCheckup(selectedPasien.getCheckupTerakhir())) {
                return true;
            }
        }
        return false;
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
        boolean isMust = isPasienMustCheckupHariIni(pasien);
        int index = SearchableCollections.binarySearch(pasienhariIni, pasien);
        boolean pasienDitemukan = index > -1;
        if (isMust) {
            if (!pasienDitemukan) {
                pasienhariIni.add(pasien);
                tblPasien.getItems().add(pasien);
            } else if (pasienDitemukan && pasienEvent.getOPERATION_TYPE() == OperationType.UPDATE) {
                pasienhariIni.set(index, pasien);
                tblPasien.getItems().set(index, pasien);
            } else if (pasienDitemukan && pasienEvent.getOPERATION_TYPE() == OperationType.DELETE) {
                pasienhariIni.remove(index);
                tblPasien.getItems().remove(index);
            }
        }else if (!isMust && pasienDitemukan) {
            pasienhariIni.remove(index);
            tblPasien.getItems().remove(index);
        }
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
        int index;
        Tindakan tindakan = tindakanEvent.getTindakan();
        switch (tindakanEvent.getOPERATION_TYPE()) {
            case CREATE:
                tindakanList.add(tindakan);
                break;
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
        }
    }
}
