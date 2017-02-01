package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.MainMenu;
import com.klinik.dev.Util;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Rule;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.enums.OPERATION_TYPE;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.PasienEvent;
import com.klinik.dev.events.RiwayatTindakanEvent;
import com.klinik.dev.events.TindakanEvent;
import com.sun.istack.internal.Nullable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;
import org.joda.time.DateTime;
import tray.notification.NotificationType;

import java.io.IOException;
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
public class TblSemuaPasien implements Initializable, OnOkFormContract {

    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
    private Dao<Tindakan, Integer> tindakans = DaoManager.createDao(DB.getDB(), Tindakan.class);
    private Dao<RiwayatTindakan, Integer> riwayatTindakans = DaoManager.createDao(DB.getDB(), RiwayatTindakan.class);

    private List<Tindakan> tindakanList = tindakans.queryForAll();
    private ObservableList<Pasien> listPasiens = FXCollections.observableArrayList(pasienDao.queryForAll());


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
        tblPasien.setEditable(true);
        tblPasien.setItems(getPasiensSortedList());
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
                        for (Rule rule : item.getTindakan().getRules().getRules()) {
                            if (rule.isMoreThanPeriod(item.getCheckupTerakhir())) {
                                setTextFill(Paint.valueOf("#ecf0f1"));
                                setStyle("-fx-background-color: #e74c3c");
                                break;
                            } else {
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
        noRmColumn.setCellValueFactory(new PropertyValueFactory<>("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        noTelponColumn.setCellValueFactory(new PropertyValueFactory<>("noTelepon"));
        alamatColumn.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        jadwalCheckupColumn.setCellValueFactory(new PropertyValueFactory<>("pasien"));

        namaColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        noTelponColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        alamatColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        namaColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Pasien, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Pasien, String> t) {
                Pasien p = t.getTableView().getSelectionModel().getSelectedItem();
                p.setNama(t.getNewValue());
                try {
                    pasienDao.update(p);
                    EventBus.getInstance().post(new PasienEvent(p, OPERATION_TYPE.UPDATE));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        noTelponColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Pasien, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Pasien, String> t) {
                Pasien p = t.getTableView().getSelectionModel().getSelectedItem();
                p.setNoTelepon(t.getNewValue());
                try {
                    pasienDao.update(p);
                    EventBus.getInstance().post(new PasienEvent(p, OPERATION_TYPE.UPDATE));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        alamatColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Pasien, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Pasien, String> t) {
                Pasien p = t.getTableView().getSelectionModel().getSelectedItem();
                p.setAlamat(t.getNewValue());
                try {
                    pasienDao.update(p);
                    EventBus.getInstance().post(new PasienEvent(p, OPERATION_TYPE.UPDATE));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private SortedList<Pasien> getPasiensSortedList() {
        FilteredList<Pasien> pasienFilteredList = new FilteredList<Pasien>(listPasiens, pasien -> true);
        tfFilterTable.textProperty().addListener((observable, oldValue, newValue) -> {
            pasienFilteredList.setPredicate(pasien -> {
                if (newValue == null || newValue.isEmpty())
                    return true;
                if (String.valueOf(pasien.getNoRekamMedis()).contains((newValue)))
                    return true;
                else if (pasien.getNama().toLowerCase().contains(newValue.toLowerCase()))
                    return true;
                return false;
            });
        });
        SortedList<Pasien> pasienSortedList = new SortedList<>(pasienFilteredList);
        return pasienSortedList;
    }

    public void overrideItems(List<Pasien> pasiens) {
        listPasiens = FXCollections.observableArrayList(pasiens);
        tblPasien.setItems(getPasiensSortedList());
    }

    public void onKeyPressed(KeyEvent keyEvent) throws SQLException, IOException {
        Optional<ButtonType> decision;
        Pasien selectedPasien = tblPasien.getSelectionModel().getSelectedItem();
        switch (keyEvent.getCode()) {
            case R:
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/uis/rekammedisdialog.fxml"));
                AnchorPane pane = loader.load();
                RekamMedisDialog rekamMedisDialog = loader.getController();
                pasienDao.refresh(selectedPasien);
                rekamMedisDialog.setLvMedicalRecordItems(selectedPasien.getRiwayatTindakans());
                rekamMedisDialog.getLblPatientname().setText(selectedPasien.getNama());
                Stage stage = new Stage();
                stage.setTitle(String.format("Riwayat Medis %s", selectedPasien.getNamaPanggilan()));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(MainMenu.PRIMARY_STAGE);
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.showAndWait();
                break;
            case D:
                decision = Util.deleteConfirmation().showAndWait();
                boolean isOK = decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE);
                if (isOK) {
                    int deleted = pasienDao.delete(selectedPasien);
                    if (deleted == 1) {
                        EventBus.getInstance().post(new PasienEvent(selectedPasien, OPERATION_TYPE.DELETE));
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
                ChoiceDialog<String> tindakanChoiceDialog = new ChoiceDialog<>(null, getListTindakanString());
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
                        EventBus.getInstance().post(new PasienEvent(selectedPasien, OPERATION_TYPE.UPDATE));
                        EventBus.getInstance().post(new RiwayatTindakanEvent(newRiwayatTindakan, OPERATION_TYPE.CREATE));
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
            l.add(String.format("%d. %s", ++index, tindakan.getNamaTindakan()));
        return l;
    }

    @Subscribe
    public void onPasien(PasienEvent pasienEvent) {
        Pasien pasien = pasienEvent.getPasien();
        if (pasienEvent.getOPERATION_TYPE() == OPERATION_TYPE.CREATE) {
            this.listPasiens.add(pasien);
        } else if (pasienEvent.getOPERATION_TYPE() == OPERATION_TYPE.UPDATE) {
            this.listPasiens.set(listPasiens.indexOf(pasien), pasien);
        } else if (pasienEvent.getOPERATION_TYPE() == OPERATION_TYPE.DELETE) {
            this.listPasiens.remove(listPasiens.indexOf(pasien));
        }
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
        Tindakan tindakan = tindakanEvent.getTindakan();
        switch (tindakanEvent.getOPERATION_TYPE()) {
            case UPDATE:
                tindakanList.set(tindakanList.indexOf(tindakan), tindakan);
                break;
            case DELETE:
                tindakanList.remove(tindakanList.indexOf(tindakan));
                break;
            case CREATE:
                tindakanList.add(tindakan);
                break;
        }
    }

    @Override
    public void onPositive(@Nullable Object data) {

    }
}
