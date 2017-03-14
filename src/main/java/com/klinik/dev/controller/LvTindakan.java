package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.klinik.dev.contract.Comparable;
import com.klinik.dev.datastructure.ComparableCollections;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.enums.OPERATION_TYPE;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.TindakanEvent;
import com.klinik.dev.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyEvent;
import lombok.Data;
import tray.notification.NotificationType;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 20/02/17.
 */
@Data
public class LvTindakan implements Initializable {

    private Dao<Tindakan, Integer> tindakanDao = Tindakan.getDao();
    private ObservableList<TindakanDecorator> listTindakan = FXCollections.observableArrayList();

    @FXML
    private ListView<TindakanDecorator> lvTindakan;

    public LvTindakan() throws SQLException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getInstance().register(this);
        try {
            tindakanDao.queryForAll().stream().forEach(tindakan -> listTindakan.add(new TindakanDecorator(tindakan)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lvTindakan.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvTindakan.setItems(listTindakan);
    }

    @Subscribe
    public void onTindakan(TindakanEvent tindakanEvent) {
        switch (tindakanEvent.getOPERATION_TYPE()) {
            case CREATE:
                listTindakan.add(new TindakanDecorator(tindakanEvent.getTindakan()));
                break;
            case DELETE:
                int index = ComparableCollections.binarySearch(listTindakan, tindakanEvent.getTindakan());
                if (index > -1)
                    listTindakan.remove(index);
                break;
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case D:
                deleteTindakanItems();
                break;
        }
    }

    @FXML
    private void deleteTindakan() {
        deleteTindakanItems();
    }

    private void deleteTindakanItems() {
        Optional<ButtonType> decision = Util.deleteConfirmation().showAndWait();
        boolean isOK = decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE);
        if (isOK) {
            List<Tindakan> deletedTindakan = new ArrayList<>();
            lvTindakan.getSelectionModel().getSelectedItems().forEach(tindakanDecorator -> {
                try {
                    deletedTindakan.add(tindakanDecorator.getTindakan());
                    tindakanDao.delete(tindakanDecorator.getTindakan());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            deletedTindakan.forEach(tindakan -> EventBus.getInstance().post(new TindakanEvent(tindakan, OPERATION_TYPE.DELETE)));
            Util.showNotif("Sukses", "Tindakan telah dihapus", NotificationType.SUCCESS);
        }
    }

    @Data
    public class TindakanDecorator implements Comparable {
        private Tindakan tindakan;

        public TindakanDecorator(Tindakan tindakan) {
            this.tindakan = tindakan;
        }

        @Override
        public String toString() {
            return this.tindakan.toString_();
        }

        @Override
        public int toBeCompared() {
            return this.tindakan.getId();
        }
    }

}
