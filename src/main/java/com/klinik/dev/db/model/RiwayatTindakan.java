package com.klinik.dev.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.contract.Comparable;
import com.klinik.dev.util.Util;
import lombok.Data;

/**
 * Created by khairulimam on 25/01/17.
 */
@DatabaseTable(tableName = "riwayat_tindakan")
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
    private int tarif;

    @Override
    public String toString() {
        String tgl = pasien.getCheckupTerakhirActualDate().toString("dd.MM.yyyy");
        String jam = pasien.getCheckupTerakhirActualDate().toString("HH:mm:ss");
        return String.format("Melakukan %s pada tgl %s jam %s", tindakan.getNamaTindakan(), tgl, jam);
    }

    public String getTanggal() {
        return pasien.getCheckupTerakhirActualDate().toString(Util.DATE_PATTERN);
    }

    @Override
    public int toBeCompared() {
        return this.id;
    }
}
