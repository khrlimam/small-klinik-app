package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.util.Util;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Rule;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.enums.OPERATION_TYPE;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.PasienEvent;
import com.klinik.dev.events.TindakanEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TblPasienCheckupHariIni implements Initializable {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private Dao<Tindakan, Integer> tindakans = DaoManager.createDao(DB.getDB(), Tindakan.class);
    private Dao<RiwayatTindakan, Integer> riwayatTindakans = DaoManager.createDao(DB.getDB(), RiwayatTindakan.class);

    private List<Pasien> pasiens = pasienDao.queryForAll();
    private List<Tindakan> tindakanList = tindakans.queryForAll();
    private ObservableList<Pasien> pasienHariIni;

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
        jadwalPasienHariIni(pasiens);
        tblPasien.setItems(getPasienHariIniSortedList());
        setUpTableColumnItems();
    }

    private void jadwalPasienHariIni(List<Pasien> pasiens) {
        pasienHariIni = FXCollections.observableArrayList();
        for (Pasien p : pasiens) {
            if (p.getTindakan() != null)
                for (Rule rule : p.getTindakan().getRules().getRules()) {
                    if (rule != null)
                        if (rule.isTodayCheckup(p.getCheckupTerakhir())) {
                            this.pasienHariIni.add(p);
                            break;
                        }
                }
        }
    }

    public void overrideItems(List<Pasien> pasiens) {
        jadwalPasienHariIni(pasiens);
        tblPasien.setItems(getPasienHariIniSortedList());
    }

    private void setUpTableColumnItems() {
        noRmColumn.setCellValueFactory(new PropertyValueFactory("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory("nama"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory("noTelepon"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory("alamat"));
    }

    public void onKeyPressed(KeyEvent keyEvent) throws SQLException {
        Optional<ButtonType> decision;
        Pasien selectedPasien = tblPasien.getSelectionModel().getSelectedItem();
        switch (keyEvent.getCode()) {
            case D:
                decision = Util.deleteConfirmation().showAndWait();
                if (decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    int deleted = pasienDao.delete(selectedPasien);
                    if (deleted == 1) {
                        EventBus.getInstance().post(new PasienEvent(selectedPasien, OPERATION_TYPE.DELETE));
                        Util.showNotif("Berhasil", "Data telah dihapus", NotificationType.SUCCESS);
                    }
                }
                return;
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
                        EventBus.getInstance().post(new PasienEvent(selectedPasien, OPERATION_TYPE.UPDATE));
                    }
                }
                break;
        }
    }

    private boolean isPasienMustCheckupHariIni(Pasien selectedPasien) {
        for (Rule rule : selectedPasien.getTindakan().getRules().getRules()) {
            if (rule.isTodayCheckup(selectedPasien.getCheckupTerakhir())) {
                return true;
            }
        }
        return false;
    }

    private SortedList<Pasien> getPasienHariIniSortedList() {
        FilteredList<Pasien> pasienFilteredList = new FilteredList<Pasien>(pasienHariIni, pasien -> true);
        tfFilterTable.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldvalue, String newValue) {
                pasienFilteredList.setPredicate((Pasien pasien) -> {
                    if (newValue == null || newValue.isEmpty())
                        return true;
                    if (String.valueOf(pasien.getNoRekamMedis()).contains((newValue)))
                        return true;
                    else if (pasien.getNama().toLowerCase().contains(newValue.toLowerCase()))
                        return true;
                    return false;
                });
            }
        });
        SortedList<Pasien> pasienSortedList = new SortedList<>(pasienFilteredList);
        return pasienSortedList;
    }

    private List<String> getListTindakanString() {
        List<String> l = new ArrayList<>();
        int index = 0;
        for (Tindakan tindakan : tindakanList)
            l.add(String.format("%d. %s", ++index, tindakan.toString()));
        return l;
    }

    @Subscribe
    public void onPasien(PasienEvent pasienEvent) {
        Pasien pasien = pasienEvent.getPasien();
        boolean isMust = isPasienMustCheckupHariIni(pasien);
        int index = pasienHariIni.indexOf(pasien);
        boolean pasienDitemukan = index > -1;
        if (isMust) {
            if (!pasienDitemukan) {
                pasienHariIni.add(pasien);
            } else if (pasienEvent.getOPERATION_TYPE() == OPERATION_TYPE.UPDATE) {
                pasienHariIni.set(index, pasien);
            } else if (pasienEvent.getOPERATION_TYPE() == OPERATION_TYPE.DELETE) {
                pasienHariIni.remove(index);
            }
        } else if (!isMust && pasienDitemukan) {
            pasienHariIni.remove(pasienHariIni.indexOf(pasien));
        }
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
        Tindakan tindakan = tindakanEvent.getTindakan();
        switch (tindakanEvent.getOPERATION_TYPE()) {
            case CREATE:
                tindakanList.add(tindakan);
                break;
            case UPDATE:
                tindakanList.set(tindakanList.indexOf(tindakan), tindakan);
                break;
            case DELETE:
                tindakanList.remove(tindakanList.indexOf(tindakan));
                break;
        }
    }
}
