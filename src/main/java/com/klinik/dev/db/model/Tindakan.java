package com.klinik.dev.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.bussiness.Rules;
import com.klinik.dev.contract.Comparable;
import lombok.Data;

/**
 * Created by khairulimam on 25/01/17.
 */

@DatabaseTable(tableName = "tindakan")
@Data
public class Tindakan implements Comparable {
    @DatabaseField(generatedId = true)
    private int id;
    @ForeignCollectionField
    private ForeignCollection<RiwayatTindakan> riwayatTindakans;
    @DatabaseField(width = 20)
    private String namaTindakan;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Rules rules;

    @Override
    public String toString() {
        return getNamaTindakan();
    }

    @Override
    public int toBeCompared() {
        return this.id;
    }
}
