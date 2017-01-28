package com.klinik.dev.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by khairulimam on 25/01/17.
 */

@DatabaseTable(tableName = "tindakan")
public class JenisTindakan {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(width = 20)
    private String jenisTindakan;
    @ForeignCollectionField
    private ForeignCollection<RiwayatTindakan> riwayatTindakans;

    public ForeignCollection<RiwayatTindakan> getRiwayatTindakans() {
        return riwayatTindakans;
    }

    public void setRiwayatTindakans(ForeignCollection<RiwayatTindakan> riwayatTindakans) {
        this.riwayatTindakans = riwayatTindakans;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJenisTindakan() {
        return jenisTindakan;
    }

    public void setJenisTindakan(String jenisTindakan) {
        this.jenisTindakan = jenisTindakan;
    }
}
