package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.customui.NumberTextField;
import com.klinik.dev.datastructure.ComparableCollections;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.TindakanEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import lombok.Data;
import org.joda.time.DateTime;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 04/02/17.
 */
@Data
public class CheckupDialog implements Initializable {

    private OnOkFormContract onOkFormContract;

    @FXML
    private ChoiceBox<Tindakan> cbTindakan;
    @FXML
    private NumberTextField tfTarif;
    @FXML
    private TextArea taDiagnosis;

    private Dao<Tindakan, Integer> tindakanDao = Tindakan.getDao();
    private ObservableList listTindakan = FXCollections.observableArrayList(tindakanDao.queryForAll());

    public CheckupDialog() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getInstance().register(this);
        cbTindakan.setItems(listTindakan);
        cbTindakan.getSelectionModel().select(0);
    }

    public Tindakan getTindakan() {
        return cbTindakan.getSelectionModel().getSelectedItem();
    }

    public RiwayatTindakan getRiwayatTindakan() {
        RiwayatTindakan riwayatTindakan = new RiwayatTindakan();
        riwayatTindakan.setDiagnosis(taDiagnosis.getText());
        riwayatTindakan.setTarif(Double.parseDouble(tfTarif.getText()));
        riwayatTindakan.setTglCheckup(DateTime.now());
        return riwayatTindakan;
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
        Tindakan tindakan = tindakanEvent.getTindakan();
        int index = ComparableCollections.binarySearch(listTindakan, tindakanEvent.getTindakan());
        switch (tindakanEvent.getOPERATION_TYPE()) {
            case DELETE:
                if (index > -1)
                    listTindakan.remove(index);
                break;
            case UPDATE:
                if (index > -1)
                    listTindakan.set(index, tindakan);
                break;
            case CREATE:
                listTindakan.add(tindakan);
                break;
        }
    }

    public void onOk(ActionEvent event) {
        if (onOkFormContract != null) {
            onOkFormContract.onPositive();
            resetForm();
        }
    }

    private void resetForm() {
        cbTindakan.getSelectionModel().select(0);
        tfTarif.setText("");
        taDiagnosis.setText("");
    }
}
