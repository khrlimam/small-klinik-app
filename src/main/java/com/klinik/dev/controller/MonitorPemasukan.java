package com.klinik.dev.controller;

import com.google.common.eventbus.Subscribe;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.customui.HoveredLineChartNode;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.RiwayatTindakan;
import com.klinik.dev.events.EventBus;
import com.klinik.dev.events.RiwayatTindakanEvent;
import com.klinik.dev.util.NumberFormatUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import lombok.Data;

import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
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
    private Button thnSelanjutnya, thnSebelumnya;

    @FXML
    private LineChart<Number, Number> lcLineChart;
    @FXML
    private NumberAxis xAxis, yAxis;

    private Dao<RiwayatTindakan, Integer> riwayatTindakanDao = DaoManager.createDao(DB.getDB(), RiwayatTindakan.class);
    private List<RiwayatTindakan> all = riwayatTindakanDao.queryForAll();
    private ObservableList<XYChart.Series<Number, Number>> chartSeries;
    private Map<Integer, Map<Integer, Double>> pendapatanSetiapTahun;
    Map<Integer, Map<Integer, List<RiwayatTindakan>>> riwayatTindakanPerbulan;
    private int indexYearPosition = 0;


    public MonitorPemasukan() throws SQLException {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getInstance().register(this);
        populatePendapatanAndTransaksiRiwayatTindakanSetiapTahun();
        if (getYears() != null && getYears().size() > 0) {
            xAxis.setAutoRanging(false);
            xAxis.setTickUnit(1d);
            xAxis.setLowerBound(0d);
            xAxis.setUpperBound(14d);
            toggleBtnTahunSelanjutnya();
            toggleBtnTahunSebelumnya();
            int firstYear = getYears().get(0);
            populateLineChartSeriesData(firstYear);
        } else {
            xAxis.setLabel("Tidak ada data");
            thnSebelumnya.setDisable(true);
            thnSelanjutnya.setDisable(true);
            lcLineChart.setDisable(true);
        }
    }

    private void populatePendapatanAndTransaksiRiwayatTindakanSetiapTahun() {
        pendapatanSetiapTahun = all.stream().parallel()
                .collect(Collectors
                        .groupingBy(RiwayatTindakan::getYear,
                                Collectors.groupingBy(RiwayatTindakan::getMonth,
                                        Collectors.summingDouble(RiwayatTindakan::getTarif)
                                )));
        riwayatTindakanPerbulan = all.stream().parallel()
                .collect(Collectors
                        .groupingBy(RiwayatTindakan::getYear,
                                Collectors.groupingBy(RiwayatTindakan::getMonth)));
    }

    private void populateLineChartSeriesData(int year) {
        XYChart.Series series = new XYChart.Series();
        series.setName(String.format("Jumlah pemasukan tahun %d", year));
        pendapatanSetiapTahun.get(year).entrySet().stream().forEach(pendapatanPerbulanEntry -> {
            int x = pendapatanPerbulanEntry.getKey();
            Double y = pendapatanPerbulanEntry.getValue();
            int jumlahTransaksi = riwayatTindakanPerbulan.get(year).get(pendapatanPerbulanEntry.getKey()).size();
            String popup = String.format("%s\n%s\n%d Pasien",
                    DateFormatSymbols.getInstance().getMonths()[x - 1],
                    NumberFormatUtil.getRupiahFormat().format(y),
                    jumlahTransaksi);
            XYChart.Data data = new XYChart.Data(x, y);
            data.setNode(new HoveredLineChartNode(popup));
            series.getData().add(data);
        });
        chartSeries = FXCollections.observableArrayList(series);
        lcLineChart.setData(chartSeries);
    }

    private List<Integer> getYears() {
        if (pendapatanSetiapTahun != null && pendapatanSetiapTahun.size() > 0)
            return pendapatanSetiapTahun.entrySet().stream().map(integerMapEntry -> integerMapEntry.getKey()).collect(Collectors.toList());
        return null;
    }

    @Subscribe
    public void onRiwayatTindakan(RiwayatTindakanEvent riwayatTindakanEvent) {
        // riwayat tindakan only moving forward, so we only accepting add eventtype let's assume that as default event
        all.add(riwayatTindakanEvent.getRiwayatTindakan());
        // i dont know if this is best practice
        populatePendapatanAndTransaksiRiwayatTindakanSetiapTahun();
    }

    public void chartTahunSebelumnya() {
        int showThisYear = getDecrementalIndex();
        if (showThisYear > -1)
            populateLineChartSeriesData(getYears().get(showThisYear));
        toggleBtnTahunSebelumnya();
        toggleBtnTahunSelanjutnya();
    }

    public void chartTahunSelanjutnya() {
        int showThisYear = getIncrementalIndex();
        if (showThisYear > -1)
            populateLineChartSeriesData(getYears().get(showThisYear));
        toggleBtnTahunSelanjutnya();
        toggleBtnTahunSebelumnya();
    }

    private void toggleBtnTahunSebelumnya() {
        thnSebelumnya.setDisable(indexYearPosition - 1 < 0);
    }

    private void toggleBtnTahunSelanjutnya() {
        thnSelanjutnya.setDisable(indexYearPosition + 1 > getYears().size() - 1);
    }

    private int getIncrementalIndex() {
        if (++indexYearPosition < getYears().size())
            return indexYearPosition;
        return -1;
    }

    private int getDecrementalIndex() {
        if (--indexYearPosition > -1)
            return indexYearPosition;
        return -1;
    }
}
