package com.klinik.dev.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by khairulimam on 25/01/17.
 */
@DatabaseTable(tableName = "riwayat_tindakan")
public class RiwayatTindakan {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pasien getPasien() {
        return pasien;
    }

    public void setPasien(Pasien pasien) {
        this.pasien = pasien;
    }

    public JenisTindakan getJenisTindakan() {
        return jenisTindakan;
    }

    public void setJenisTindakan(JenisTindakan jenisTindakan) {
        this.jenisTindakan = jenisTindakan;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Pasien pasien;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private JenisTindakan jenisTindakan;
    @DatabaseField
    private boolean status;

}
