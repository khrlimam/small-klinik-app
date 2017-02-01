package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.Log;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.customui.NumberTextField;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.enums.AGAMA;
import com.klinik.dev.enums.STATUS;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.TindakanEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Data;
import org.joda.time.DateTime;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 27/01/17.
 */
@Data
public class PatientForm implements Initializable {

    private OnOkFormContract formContract;

    private Dao<Tindakan, Integer> tindakans = DaoManager.createDao(DB.getDB(), Tindakan.class);
    private ObservableList<Tindakan> tindakanLists = FXCollections.observableArrayList(tindakans.queryForAll());

    @FXML
    private TextField tfNama, tfNamaPanggilan, tfPekerjaan;
    @FXML
    private NumberTextField tfNoTelpon;
    @FXML
    private TextArea taAlamat;
    @FXML
    private ChoiceBox cbAgama, cbTindakan;
    @FXML
    private RadioButton rbSudah, rbBelum;
    @FXML
    private DatePicker dtTglLahir;

    ToggleGroup rbStatusToggleGroup = new ToggleGroup();

    public PatientForm() throws SQLException {
    }

    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getInstance().register(this);
        cbTindakan.setItems(tindakanLists);
        initComponents();
        resetForm();
    }

    private void initComponents() {
        dtTglLahir.setValue(LocalDate.now().minusYears(22));
        rbBelum.setToggleGroup(rbStatusToggleGroup);
        rbSudah.setToggleGroup(rbStatusToggleGroup);
        rbBelum.setSelected(true);
        cbTindakan.getSelectionModel().select(0);
        cbAgama.setItems(getListAgama());
        cbAgama.getSelectionModel().select(0);
    }

    private ObservableList getListAgama() {
        ObservableList items = FXCollections.observableArrayList();
        for (AGAMA agama : AGAMA.values()) {
            items.add(agama);
        }
        return items;
    }

    @FXML
    protected void onOk() {
        Pasien newPasien = getPasien();
        if (this.formContract != null) {
            formContract.onPositive(newPasien);
            return;
        }
        Log.w(PatientForm.class, "Contract ain't implemented yet!");
    }

    private RadioButton getRbStatus() {
        return (RadioButton) this.rbStatusToggleGroup.getSelectedToggle();
    }

    private STATUS getSTatus() {
        String id = getRbStatus().getId();
        if (id.equals(rbSudah.getId()))
            return STATUS.MENIKAH;
        return STATUS.BELUM_MENIKAH;
    }

    @FXML
    public void resetForm() {
        tfNama.setText("");
        tfNamaPanggilan.setText("");
        tfNoTelpon.setText("");
        tfPekerjaan.setText("");
        cbAgama.getSelectionModel().select(0);
        taAlamat.setText("");
        cbTindakan.getSelectionModel().select(0);
        rbStatusToggleGroup.getSelectedToggle().setSelected(false);
    }

    public Pasien getPasien() {
        Pasien pasien = new Pasien();
        pasien.setNama(tfNama.getText());
        pasien.setNamaPanggilan(tfNamaPanggilan.getText());
        pasien.setNoTelepon(tfNoTelpon.getText());
        pasien.setPekerjaan(tfPekerjaan.getText());
        pasien.setAgama((AGAMA) cbAgama.getSelectionModel().getSelectedItem());
        pasien.setStatus(getSTatus());
        pasien.setAlamat(taAlamat.getText());
        pasien.setTglLahir(new DateTime(dtTglLahir.getValue().toString()));
        pasien.setTglRegister(new DateTime());
        pasien.setTindakan((Tindakan) cbTindakan.getSelectionModel().getSelectedItem());
        pasien.setCheckupTerakhir(DateTime.now());
        return pasien;
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
        int index;
        Tindakan tindakan = tindakanEvent.getTindakan();
        switch (tindakanEvent.getOPERATION_TYPE()) {
            case CREATE:
                tindakanLists.add(tindakan);
                break;
            case DELETE:
                tindakanLists.remove(tindakanLists.indexOf(tindakan));
                break;
            case UPDATE:
                tindakanLists.set(tindakanLists.indexOf(tindakan), tindakan);
                break;
        }
    }

}
