package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.App;
import com.klinik.dev.datastructure.ComparableCollections;
import com.klinik.dev.db.DB;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Data;
import tray.notification.NotificationType;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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

    private void setUpTableColumnItems() {
        noRmColumn.setCellValueFactory(new PropertyValueFactory<>("noRekamMedis"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        tindakanColumn.setCellValueFactory(new PropertyValueFactory<>("tindakan"));
        jadwalCheckupColumn.setCellValueFactory(new PropertyValueFactory<>("pasien"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
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
}
