package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.contract.OnOkFormContract;
import com.klinik.dev.customui.NumberTextField;
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

    private Dao<Tindakan, Integer> tindakanDao = DaoManager.createDao(DB.getDB(), Tindakan.class);
    private ObservableList tindakanLists = FXCollections.observableArrayList(tindakanDao.queryForAll());

    public CheckupDialog() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getInstance().register(this);
        cbTindakan.setItems(tindakanLists);
        cbTindakan.getSelectionModel().select(0);
    }

    public Tindakan getTindakan() {
        return cbTindakan.getSelectionModel().getSelectedItem();
    }

    public RiwayatTindakan getRiwayatTindakan() {
        RiwayatTindakan riwayatTindakan = new RiwayatTindakan();
        riwayatTindakan.setDiagnosis(taDiagnosis.getText());
        riwayatTindakan.setTarif(Integer.parseInt(tfTarif.getText()));
        return riwayatTindakan;
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
