package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.RiwayatTindakanEvent;
import com.klinik.dev.util.Log;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import lombok.Data;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by khairulimam on 04/02/17.
 */
@Data
public class MonitorPemasukan implements Initializable {

    @FXML
    private AnchorPane apChartContainer;

    @FXML
    private LineChart<Number, Number> lcLineChart;
    @FXML
    private NumberAxis xAxis, yAxis;

    private Dao<RiwayatTindakan, Integer> riwayatTindakanDao = DaoManager.createDao(DB.getDB(), RiwayatTindakan.class);
    private List<RiwayatTindakan> all = riwayatTindakanDao.queryForAll();
    private ObservableList<XYChart.Series<Integer, String>> chartSeries;
    private Map<Integer, Map<Integer, Double>> pendapatanSetiapTahun;


    public MonitorPemasukan() throws SQLException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getInstance().register(this);
        populatePendapatanSetiapTahunMap();
        int firstYear = getYears().get(0);
        populateLineChartSeriesData(firstYear);
    }

    private void populatePendapatanSetiapTahunMap() {
        pendapatanSetiapTahun = all.stream()
                .collect(Collectors
                        .groupingBy(RiwayatTindakan::getYear,
                                Collectors.groupingBy(RiwayatTindakan::getMonth,
                                        Collectors.summingDouble(RiwayatTindakan::getTarif))));
    }

    private void populateLineChartSeriesData(int year) {
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(1d);
        xAxis.setLowerBound(1d);
        xAxis.setUpperBound(12);
        lcLineChart.setCursor(Cursor.CROSSHAIR);
        XYChart.Series series = new XYChart.Series();
        series.setName(String.format("Jumlah pemasukan tahun %d", year));
        pendapatanSetiapTahun.get(year).entrySet().stream().forEach(pendapatanPerbulanEntry ->
                series.getData().add(new XYChart.Data(pendapatanPerbulanEntry.getKey(), pendapatanPerbulanEntry.getValue())));
        lcLineChart.getData().add(series);
    }

    private List<Integer> getYears() {
        return pendapatanSetiapTahun.entrySet().stream().map(integerMapEntry -> integerMapEntry.getKey()).collect(Collectors.toList());
    }

    @Subscribe
    public void onRiwayatTindakan(RiwayatTindakanEvent riwayatTindakanEvent) {
        // riwayat tindakan only moving forward, so we only accapting add eventtype lets assume that as default
        all.add(riwayatTindakanEvent.getRiwayatTindakan());
        // i dont know if this is best practice or not
        populatePendapatanSetiapTahunMap();
    }

    public void chartTahunSebelumnya(ActionEvent event) {

    }

    public void chartTahunSelanjutnya(ActionEvent event) {

    }
}
