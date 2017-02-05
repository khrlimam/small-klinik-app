package com.klinik.dev.controller;

import com.google.common.io.Files;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.App;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.util.FileUtil;
import com.klinik.dev.util.Log;
import com.klinik.dev.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import lombok.Data;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import tray.notification.NotificationType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 04/02/17.
 */
@Data
public class ShowPatient implements Initializable {

    @FXML
    private Label lblNoRm, lblNama, lblUmur, lblNoTelepon, lblAlamat, lblPekerjaan, lblTglRegistrasi;
    @FXML
    private ImageView ivPatient;
    @FXML
    TableView tblRiwayatTindakan;
    @FXML
    TableColumn<RiwayatTindakan, String> tanggalColumn, tindakanColumn, diagnosisColumn;

    private Pasien pasien;
    private ObservableList<RiwayatTindakan> riwayatTindakans;
    private FileChooser fileChooser;
    private Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);

    private Image fotoProfile = new Image(getClass().getResourceAsStream("/foto/default.png"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ivPatient.setImage(fotoProfile);
        fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih gambar");
        fileChooser.getExtensionFilters().add(FileUtil.ALLOWED_IMAGE);
    }

    public ShowPatient() throws SQLException {
    }

    public void reinitialize(Pasien pasien) throws FileNotFoundException {
        this.pasien = pasien;
        lblNama.setText(pasien.getNama());
        lblNoRm.setText(String.format("RM%04d", pasien.getNoRekamMedis()));
        int age = LocalDate.now().getYear() - pasien.getTglLahir().getYear();
        lblUmur.setText(String.format("%d tahun", age));
        lblNoTelepon.setText(pasien.getNoTelepon());
        lblAlamat.setText(pasien.getAlamat());
        lblPekerjaan.setText(pasien.getPekerjaan());
        lblTglRegistrasi.setText(pasien.getTglRegisterToString());
        String fotoProfilePath = pasien.getFotoPath();
        if (fotoProfilePath != null) {
            fotoProfile = new Image(new FileInputStream(new File(fotoProfilePath)));
            ivPatient.setImage(fotoProfile);
        }
        riwayatTindakans = FXCollections.observableArrayList(pasien.getRiwayatTindakans());
        tblRiwayatTindakan.setItems(riwayatTindakans);
        setupColumn();
    }

    private void setupColumn() {
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        tindakanColumn.setCellValueFactory(new PropertyValueFactory<>("tindakan"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
    }

    public void gantiGambar() throws SQLException {
        if (pasien != null) {
            try {
                File fileSrcFile = fileChooser.showOpenDialog(App.PRIMARY_STAGE);
                Magic.getMagicMatch(fileSrcFile, false).getMimeType();
                String fileExtension = Files.getFileExtension(fileSrcFile.getName());
                File fileDestination = FileUtil.generateFileToUploadFolder(fileExtension);
                FileUtil.uploadFile(fileSrcFile, fileDestination);
                pasien.setFotoPath(fileDestination.getAbsolutePath());
                pasienDao.update(pasien);
                fotoProfile = new Image(new FileInputStream(fileDestination));
                ivPatient.setImage(fotoProfile);
                Util.showNotif("Sukses", "Gambar telah diganti", NotificationType.SUCCESS);
            } catch (MagicParseException e) {
                Util.showNotif("Error", String.format("Gambar tidak valid! %s", e.getCause()), NotificationType.ERROR);
                e.printStackTrace();
            } catch (MagicMatchNotFoundException e) {
                Util.showNotif("Error", String.format("Gambar tidak valid! %s", e.getCause()), NotificationType.ERROR);
                e.printStackTrace();
            } catch (MagicException e) {
                Util.showNotif("Error", String.format("Gambar tidak valid! %s", e.getCause()), NotificationType.ERROR);
                e.printStackTrace();
            } catch (IOException e) {
                Util.showNotif("Error", String.format("Gambar tidak bisa dibuka! %s", e.getCause()), NotificationType.ERROR);
                e.printStackTrace();
            }
        }
    }
}
