package com.klinik.dev.controller;

import com.klinik.dev.Log;
import com.klinik.dev.contract.PatientFormContract;
import com.klinik.dev.db.model.Pasien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Data;
import org.joda.time.DateTime;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 27/01/17.
 */
public
@Data
class PatientForm implements Initializable {

    private PatientFormContract formContract;
    private Pasien pasien = new Pasien();

    @FXML
    private TextField tfNama, tfNamaPanggilan, tfNoTelpon, tfPekerjaan;
    @FXML
    private TextArea taAlamat;
    @FXML
    private ChoiceBox cbAgama;
    @FXML
    private RadioButton rbSudah, rbBelum;
    @FXML
    private DatePicker dtTglLahir;

    ToggleGroup rbStatusToggleGroup = new ToggleGroup();

    public PatientForm() {
    }

    public void initialize(URL location, ResourceBundle resources) {
        dtTglLahir.setValue(LocalDate.now().minusYears(22));
        rbBelum.setToggleGroup(rbStatusToggleGroup);
        rbSudah.setToggleGroup(rbStatusToggleGroup);
        rbBelum.setSelected(true);
        cbAgama.setItems(getListAgama());
        cbAgama.getSelectionModel().select(0);
    }

    private ObservableList getListAgama() {
        ObservableList items = FXCollections.observableArrayList();
        for (Pasien.AGAMA agama : Pasien.AGAMA.values()) {
            items.add(agama);
        }
        return items;
    }

    @FXML
    protected void onOk() {
        if (this.formContract != null) {
            formContract.onPositiveButtonClicked();
            return;
        }
        Log.w(PatientForm.class, "Contract ain't implemented yet!");
    }

    private RadioButton getRbStatus() {
        return (RadioButton) this.rbStatusToggleGroup.getSelectedToggle();
    }

    public Pasien.STATUS getSTatus() {
        String id = getRbStatus().getId();
        if (id.equals(rbSudah.getId()))
            return Pasien.STATUS.MENIKAH;
        else
            return Pasien.STATUS.BELUM_MENIKAH;
    }


    public Pasien getPasien() {
        pasien.setNama(tfNama.getText());
        pasien.setNamaPanggilan(tfNamaPanggilan.getText());
        pasien.setNoTelepon(tfNoTelpon.getText());
        pasien.setPekerjaan(tfPekerjaan.getText());
        pasien.setAgama((Pasien.AGAMA) cbAgama.getSelectionModel().getSelectedItem());
        pasien.setStatus(getSTatus());
        pasien.setAlamat(taAlamat.getText());
        pasien.setTglLahir(new DateTime(dtTglLahir.getValue().toString()));
        pasien.setTglRegister(new DateTime());
        return pasien;
    }

}
