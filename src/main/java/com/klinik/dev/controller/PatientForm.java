package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.App;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.customui.NumberTextField;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.enums.STATUS;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.TindakanEvent;
import com.klinik.dev.util.FileUtil;
import com.klinik.dev.util.Log;
import com.klinik.dev.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import lombok.Data;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.joda.time.DateTime;
import tray.notification.NotificationType;

import java.io.File;
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
    private FileChooser fileChooser = new FileChooser();

    private Dao<Tindakan, Integer> tindakanDao = DaoManager.createDao(DB.getDB(), Tindakan.class);
    private ObservableList<Tindakan> tindakanLists = FXCollections.observableArrayList(tindakanDao.queryForAll());


    @FXML
    private TextField tfNama, tfPekerjaan;
    @FXML
    private NumberTextField tfNoTelpon, tfTarif;
    @FXML
    private TextArea taAlamat, taDiagnosis;
    @FXML
    private ChoiceBox cbTindakan;
    @FXML
    private RadioButton rbSudah, rbBelum;
    @FXML
    private DatePicker dtTglLahir;

    ToggleGroup rbStatusToggleGroup = new ToggleGroup();
    private File foto;

    public PatientForm() throws SQLException {
    }

    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getInstance().register(this);
        cbTindakan.setItems(tindakanLists);
        initComponents();
    }

    private void initComponents() {
        fileChooser.getExtensionFilters().add(FileUtil.ALLOWED_IMAGE);
        fileChooser.setTitle("Pilih gambar");
        dtTglLahir.setValue(LocalDate.now().minusYears(22));
        rbBelum.setToggleGroup(rbStatusToggleGroup);
        rbSudah.setToggleGroup(rbStatusToggleGroup);
        rbBelum.setSelected(true);
        cbTindakan.getSelectionModel().select(0);
    }

    @FXML
    protected void onOk() {
        if (this.formContract != null) {
            formContract.onPositive();
            resetForm();
            return;
        }
        Log.w(PatientForm.class, "Contract ain't implemented yet!");
    }

    public RiwayatTindakan getRiwayatTindakan(Pasien newPasien) {
        RiwayatTindakan riwayatTindakan = new RiwayatTindakan();
        riwayatTindakan.setPasien(newPasien);
        riwayatTindakan.setDiagnosis(taDiagnosis.getText());
        riwayatTindakan.setTarif(Double.parseDouble(tfTarif.getText()));
        riwayatTindakan.setTindakan(newPasien.getTindakan());
        riwayatTindakan.setTglCheckup(DateTime.now());
        return riwayatTindakan;
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
        this.foto = null;
        tfNama.setText("");
        tfNoTelpon.setText("");
        tfPekerjaan.setText("");
        taAlamat.setText("");
        taDiagnosis.setText("");
        tfTarif.setText("");
        cbTindakan.getSelectionModel().select(0);
        rbBelum.setSelected(true);
    }

    public Pasien getPasien() throws SQLException {
        Pasien pasien = new Pasien();
        pasien.setNama(tfNama.getText());
        pasien.setDiagnosis(taDiagnosis.getText());
        pasien.setNoTelepon(tfNoTelpon.getText());
        pasien.setPekerjaan(tfPekerjaan.getText());
        pasien.setStatus(getSTatus());
        pasien.setAlamat(taAlamat.getText());
        pasien.setTglLahir(new DateTime(dtTglLahir.getValue().toString()));
        pasien.setTglRegister(new DateTime());
        Tindakan selectedTindakan = (Tindakan) cbTindakan.getSelectionModel().getSelectedItem();
        tindakanDao.refresh(selectedTindakan);
        pasien.setTindakan(selectedTindakan);
        pasien.setCheckupTerakhir(DateTime.now());
        return pasien;
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
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

    public void pilihGambar() {
        File sourceFile = fileChooser.showOpenDialog(App.PRIMARY_STAGE);
        try {
            Magic.getMagicMatch(sourceFile, false).getMimeType();
            this.foto = sourceFile;
        } catch (MagicParseException e) {
            e.printStackTrace();
        } catch (MagicMatchNotFoundException e) {
            Util.showNotif("Error", String.format("Gambar tidak valid! %s", e.getMessage()), NotificationType.ERROR);
        } catch (MagicException e) {
            e.printStackTrace();
        }
    }
}
