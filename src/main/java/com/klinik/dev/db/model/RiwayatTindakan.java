package com.klinik.dev.db.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.contract.Comparable;
import com.klinik.dev.util.Util;
import lombok.Data;
import org.joda.time.DateTime;

/**
 * Created by khairulimam on 25/01/17.
 */
@DatabaseTable(tableName = RiwayatTindakan.TABLE_NAME)
@Data
public class RiwayatTindakan implements Comparable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Pasien pasien;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Tindakan tindakan;
    @DatabaseField
    private String diagnosis;
    @DatabaseField
    private double tarif;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime tglCheckup;

    public static final String TABLE_NAME = "riwayat_tindakan";

    @Override
    public String toString() {
        String tgl = pasien.getCheckupTerakhirActualDate().toString("dd.MM.yyyy");
        String jam = pasien.getCheckupTerakhirActualDate().toString("HH:mm:ss");
        return String.format("Melakukan %s pada tgl %s jam %s", tindakan.getNamaTindakan(), tgl, jam);
    }

    public String getTanggal() {
        return tglCheckup.toString(Util.DATE_PATTERN);
    }

    public int getYear() {
        return tglCheckup.getYear();
    }

    public int getMonth() {
        return tglCheckup.getMonthOfYear();
    }

    public double getTotal() {
        return 0d;
    }

    @Override
    public int toBeCompared() {
        return this.id;
    }
}
