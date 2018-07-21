package com.klinik.dev.controller;

import com.j256.ormlite.dao.ForeignCollection;
import com.klinik.dev.db.model.RiwayatTindakan;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import lombok.Data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by khairulimam on 01/02/17.
 */
@Data
public class RekamMedisDialog implements Initializable {

  @FXML
  private Label lblPatientname;
  @FXML
  private ListView lvMedicalRecord;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void setLvMedicalRecordItems(ForeignCollection<RiwayatTindakan> recordItems) {
    List<String> lvItems = new ArrayList<>();
    for (RiwayatTindakan riwayatTindakan : recordItems) {
      lvItems.add(riwayatTindakan.toString());
    }
    this.lvMedicalRecord.setItems(FXCollections.observableArrayList(lvItems));
  }
}
