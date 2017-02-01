package com.klinik.dev.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.contract.Comparable;
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
    private boolean status;

    @Override
    public String toString() {
        String tgl = pasien.getCheckupTerakhirActualDate().toString("dd.MM.yyyy");
        String jam = pasien.getCheckupTerakhirActualDate().toString("HH:mm:ss");
        return String.format("Melakukan %s pada tgl %s jam %s", tindakan.getNamaTindakan(), tgl, jam);
    }

    @Override
    public int toBeCompared() {
        return this.id;
    }
}
