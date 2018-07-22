package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.klinik.dev.App;
import com.klinik.dev.datastructure.ComparableCollections;
import com.klinik.dev.db.model.Pasien;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.db.model.Tindakan;
import com.klinik.dev.enums.FILTERABLE;
import com.klinik.dev.enums.OPERATION_TYPE;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.PasienEvent;
import com.klinik.dev.events.RiwayatTindakanEvent;
import com.klinik.dev.events.TindakanEvent;
import com.klinik.dev.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Data;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.style.BorderBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import tray.notification.NotificationType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class TblSemuaPasien implements Initializable {

  private Dao<Pasien, Integer> pasienDao = Pasien.getDao();
  private Dao<Tindakan, Integer> tindakans = Tindakan.getDao();
  private Dao<RiwayatTindakan, Integer> riwayatTindakans = RiwayatTindakan.getDao();

  private ObservableList<Tindakan> tindakanList = FXCollections.observableArrayList(tindakans.queryForAll());
  private ObservableList<Pasien> listPasiens = FXCollections.observableArrayList(pasienDao.queryForAll());
  private FilteredList<Pasien> pasienFilteredList = new FilteredList<>(listPasiens, pasien -> true);

  private Label label;

  @FXML
  private DatePicker dpFilterTable;
  @FXML
  TextField tfFilterTable;
  @FXML
  private TableView<Pasien> tblPasien;
  @FXML
  private TableColumn<Pasien, String> noRmColumn, namaColumn, diagnosisColumn;
  @FXML
  private TableColumn<Pasien, Tindakan> tindakanColumn;
  @FXML
  private TableColumn<Pasien, Pasien> jadwalCheckupColumn;
  @FXML
  private ChoiceBox<Tindakan> cbFilter;
  @FXML
  private Label lblFilterTable;

  public TblSemuaPasien() throws SQLException {
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    com.klinik.dev.events.EventBus.getInstance().register(this);
    tblPasien.setEditable(true);
    tblPasien.setItems(setSortedListPasien());
    tblPasien.setTooltip(Util.tableControlTooltip());
    StringConverter<LocalDate> dpFilterConverter = new StringConverter<LocalDate>() {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Util.DATE_PATTERN);

      @Override
      public String toString(LocalDate object) {
        if (object != null) {
          return formatter.format(object);
        }
        return "";
      }

      @Override
      public LocalDate fromString(String string) {
        if (string != null && !string.isEmpty()) {
          return LocalDate.parse(string);
        }
        return null;
      }
    };
    cbFilter.setItems(tindakanList);
    cbFilter.getSelectionModel().select(0);
    lblFilterTable.setText("");
    dpFilterTable.setConverter(dpFilterConverter);
    jadwalCheckupColumn.setCellFactory((TableColumn<Pasien, Pasien> column) -> new TableCell<Pasien, Pasien>() {
      @Override
      protected void updateItem(Pasien item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty || item.getTindakan() == null) {
          setText(null);
          setStyle("");
        } else {
          setText(item.getJadwalSelanjutnya());
          boolean isAnyOfJadwalCheckupMoreThanPeriodTime = item.getTindakan().getTindakanrules()
              .stream()
              .anyMatch(tindakanRule ->
                  tindakanRule.getRule() != null &&
                      tindakanRule.getRule().isMoreThanPeriod(item.getCheckupTerakhir()));
          if (isAnyOfJadwalCheckupMoreThanPeriodTime) {
            setStyle("-fx-background-color: #e74c3c");
          } else {
            setStyle("");
          }
        }
      }
    });
    setUpTableColumnItems();
  }

  public static final String NO_REKAM_MEDIS_FIELD_NAME = "noRekamMedis";
  public static final String NAMA_FIELD_NAME = "nama";
  public static final String TINDAKAN_FIELD_NAME = "tindakan";
  public static final String PASIEN_FIELD_NAME = "pasien";
  public static final String DIAGNOSIS_FIELD_NAME = "diagnosis";

  private void setUpTableColumnItems() {
    noRmColumn.setCellValueFactory(new PropertyValueFactory<>(NO_REKAM_MEDIS_FIELD_NAME));
    namaColumn.setCellValueFactory(new PropertyValueFactory<>(NAMA_FIELD_NAME));
    tindakanColumn.setCellValueFactory(new PropertyValueFactory<>(TINDAKAN_FIELD_NAME));
    jadwalCheckupColumn.setCellValueFactory(new PropertyValueFactory<>(PASIEN_FIELD_NAME));
    diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>(DIAGNOSIS_FIELD_NAME));

    namaColumn.setCellFactory(TextFieldTableCell.forTableColumn());

    namaColumn.setOnEditCommit(t -> {
      Pasien p = t.getTableView().getSelectionModel().getSelectedItem();
      p.setNama(t.getNewValue());
      try {
        pasienDao.update(p);
        EventBus.getInstance().post(new PasienEvent(p, OPERATION_TYPE.UPDATE));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

  private SortedList<Pasien> setSortedListPasien() {
    lblFilterTable.textProperty().addListener((observable, oldValue, newValue) -> pasienFilteredList.setPredicate(pasien -> {
      String[] whatFieldToFilter = newValue.split(",");
      if (newValue == null || newValue.isEmpty())
        return true;
      if (String.valueOf(pasien.getNoRekamMedis()).contains((whatFieldToFilter[0])) && whatFieldToFilter[1].equals(FILTERABLE.FILTER_BY_NAME_OR_RM.name()))
        return true;
      else if (pasien.getNama().toLowerCase().contains(whatFieldToFilter[0].toLowerCase()) && whatFieldToFilter[1].equals(FILTERABLE.FILTER_BY_NAME_OR_RM.name()))
        return true;
      else if (pasien.getJadwalSelanjutnya().contains(whatFieldToFilter[0]) && whatFieldToFilter[1].equals(FILTERABLE.FILTER_BY_TANGGAL.name()))
        return true;
      else if (pasien.getTindakan() != null && pasien.getTindakan().toString().contains(whatFieldToFilter[0]) && whatFieldToFilter[1].equals(FILTERABLE.FILTER_BY_TINDAKAN.name()))
        return true;
      return false;
    }));
    SortedList<Pasien> pasienSortedList = new SortedList<>(pasienFilteredList);
    return pasienSortedList;
  }

  public void overrideItems(List<Pasien> pasiens) {
    listPasiens = FXCollections.observableArrayList(pasiens);
    pasienFilteredList = new FilteredList<>(listPasiens, pasien -> true);
    tblPasien.setItems(setSortedListPasien());
  }

  public void onKeyPressed(KeyEvent keyEvent) throws SQLException, IOException {
    switch (keyEvent.getCode()) {
      case S:
        showPatient();
        break;
      case D:
        deletePatient();
        return;
      case C:
        checkupPatient();
        break;
    }
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
    int index = ComparableCollections.binarySearch(tindakanList, tindakanEvent.getTindakan());
    switch (tindakanEvent.getOPERATION_TYPE()) {
      case UPDATE:
        if (index > -1)
          tindakanList.set(index, tindakan);
        break;
      case DELETE:
        if (index > -1) {
          listPasiens.stream()
              .filter(pasien -> pasien.getTindakan() != null && pasien.getTindakan().getId() == tindakanEvent.getTindakan().getId())
              .forEach(pasien -> pasien.setTindakan(null));
          listPasiens.forEach(pasien -> EventBus.getInstance().post(new PasienEvent(pasien, OPERATION_TYPE.UPDATE)));
          tindakanList.remove(index);
        }
        break;
      case CREATE:
        tindakanList.add(tindakan);
        break;
    }
  }

  public void showPatient() throws IOException, SQLException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/uis/showpatient.fxml"));
    Pasien selectedPasien = tblPasien.getSelectionModel().getSelectedItem();
    checkPatient(selectedPasien);
    AnchorPane pane = loader.load();
    ShowPatient showPatient = loader.getController();
    pasienDao.refresh(selectedPasien);
    showPatient.reinitialize(selectedPasien);
    Stage stage = new Stage();
    stage.setTitle("Detail Pasien");
    stage.initModality(Modality.WINDOW_MODAL);
    stage.initOwner(App.PRIMARY_STAGE);
    Scene scene = new Scene(pane);
    stage.setScene(scene);
    stage.showAndWait();
  }

  private void checkPatient(Pasien selectedPasien) {
    if (selectedPasien == null) {
      Util.showNotif("Error", "Pilih pasien dulu", NotificationType.ERROR);
      throw new NullPointerException("Pilih pasien dulu!");
    }
  }

  public void checkupPatient() throws IOException {
    Pasien selectedPasien = tblPasien.getSelectionModel().getSelectedItem();
    checkPatient(selectedPasien);
    FXMLLoader checkupdialog = new FXMLLoader(getClass().getResource("/uis/checkupdialog.fxml"));
    AnchorPane checkdialogpane = checkupdialog.load();
    CheckupDialog checkupDialogController = checkupdialog.getController();
    checkupDialogController.setOnOkFormContract(() -> {
      try {
        Tindakan tindakan = checkupDialogController.getTindakan();
        selectedPasien.setTindakan(tindakan);
        RiwayatTindakan riwayatTindakan = checkupDialogController.getRiwayatTindakan();
        selectedPasien.setDiagnosis(riwayatTindakan.getDiagnosis());
        selectedPasien.setCheckupTerakhir(riwayatTindakan.getTglCheckup());
        riwayatTindakan.setTindakan(tindakan);
        riwayatTindakan.setPasien(selectedPasien);
        pasienDao.update(selectedPasien);
        riwayatTindakans.create(riwayatTindakan);
        EventBus.getInstance().post(new PasienEvent(selectedPasien, OPERATION_TYPE.UPDATE));
        EventBus.getInstance().post(new RiwayatTindakanEvent(riwayatTindakan, OPERATION_TYPE.DEFAULT));
        Util.showNotif("Sukses", "Data pasien telah disimpan", NotificationType.SUCCESS);
      } catch (SQLException e) {
        Util.showNotif("Error", "Ada kesalahan", NotificationType.ERROR);
        e.printStackTrace();
      }
    });
    Stage checkupstage = new Stage();
    checkupstage.setTitle("Checkup Pasien");
    checkupstage.initModality(Modality.WINDOW_MODAL);
    checkupstage.initOwner(App.PRIMARY_STAGE);
    Scene checkupscene = new Scene(checkdialogpane);
    checkupstage.setScene(checkupscene);
    checkupstage.showAndWait();
  }

  public void deletePatient() throws SQLException {
    Pasien selectedPasien = tblPasien.getSelectionModel().getSelectedItem();
    checkPatient(selectedPasien);
    Optional<ButtonType> decision = Util.deleteConfirmation().showAndWait();
    boolean isOK = decision.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE);
    if (isOK) {
      int deleted = pasienDao.delete(selectedPasien);
      if (deleted == 1) {
        EventBus.getInstance().post(new PasienEvent(selectedPasien, OPERATION_TYPE.DELETE));
        Util.showNotif("Berhasil", "Data telah dihapus", NotificationType.SUCCESS);
      }
    }
  }

  public void filterByDate() {
    String whatToFilter = String.format("%s,%s", dpFilterTable.getEditor().getText(), FILTERABLE.FILTER_BY_TANGGAL.name());
    lblFilterTable.setText(whatToFilter);
  }

  public void filterByTindakan() {
    String whatToFilter = String.format("%s,%s", cbFilter.getValue().toString(), FILTERABLE.FILTER_BY_TINDAKAN.name());
    lblFilterTable.setText(whatToFilter);
  }

  public void filterTableByName() {
    String whatToFilter = String.format("%s,%s", tfFilterTable.getText(), FILTERABLE.FILTER_BY_NAME_OR_RM.name());
    lblFilterTable.setText(whatToFilter);
  }

  public void getReport() {
    try {
      FileChooser fileChooser = new FileChooser();
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
      File file = fileChooser.showSaveDialog(App.PRIMARY_STAGE);
      OutputStream outputStream = new FileOutputStream(file);
      BorderBuilder border = stl.border();
      border
          .setBottomPen(stl.penThin())
          .setLeftPen(stl.penThin())
          .setTopPen(stl.penThin())
          .setRightPen(stl.penThin());
      StyleBuilder textCenterAllBorderStyle = stl.style()
          .setName("style1")
          .setVerticalTextAlignment(VerticalTextAlignment.MIDDLE)
          .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT)
          .setPadding(1)
          .setLeftPadding(2)
          .setBorder(border);
      StyleBuilder columnStyle = stl.style()
          .setName("columnStyle")
          .setPadding(1)
          .setBottomPadding(2)
          .setVerticalAlignment(VerticalAlignment.MIDDLE);
      StyleBuilder columnTitleStyle = stl.style(columnStyle)
          .setName("columnTitleStyle")
          .setBorder(stl.pen1Point())
          .setHorizontalAlignment(HorizontalAlignment.CENTER);
      ReportTemplateBuilder template = template()
          .templateStyles(textCenterAllBorderStyle, columnStyle, columnTitleStyle);
      report()
          .title(Components.text("Laporan Data Pasien")
              .setHorizontalAlignment(HorizontalAlignment.CENTER).setStyle(stl.style().setBottomPadding(10)))
          .setTemplate(template)
          .columns(
              col.column("No RM", NO_REKAM_MEDIS_FIELD_NAME, type.integerType()).setStyle(textCenterAllBorderStyle).setWidth(40),
              col.column("Nama", NAMA_FIELD_NAME, type.stringType()).setStyle(textCenterAllBorderStyle).setWidth(70),
              col.column("Tindakan", TINDAKAN_FIELD_NAME, type.stringType()).setStyle(textCenterAllBorderStyle).setWidth(125),
              col.column("Diagnosis", DIAGNOSIS_FIELD_NAME, type.stringType()).setStyle(textCenterAllBorderStyle).setWidth(125)
          )
          .setDataSource(generateDataFromFilteredList())
          .toPdf(outputStream);
      Util.showNotif("Sukses", "File laporan pasien berhasil disimpan", NotificationType.SUCCESS);
    } catch (NullPointerException | IOException | DRException e) {
      e.printStackTrace();
    }
  }

  private List<Map> generateDataFromFilteredList() {
    List<Map> data = new ArrayList<>();
    setSortedListPasien().forEach(pasien -> {
      Map<String, String> row = new HashMap<>();
      row.put(NO_REKAM_MEDIS_FIELD_NAME, String.valueOf(pasien.getNoRekamMedis()));
      row.put(NAMA_FIELD_NAME, pasien.getNama());
      row.put(TINDAKAN_FIELD_NAME, pasien.getTindakan().toString());
      row.put(DIAGNOSIS_FIELD_NAME, pasien.getDiagnosis());
      data.add(row);
    });
    return data;
  }
}
