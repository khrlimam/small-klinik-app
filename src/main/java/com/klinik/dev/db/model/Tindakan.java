package com.klinik.dev.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.bussiness.BTindakan;
import lombok.Data;

/**
 * Created by khairulimam on 25/01/17.
 */

@DatabaseTable(tableName = "tindakan")
@Data
public class Tindakan {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private BTindakan tindakan;
    @ForeignCollectionField
    private ForeignCollection<RiwayatTindakan> riwayatTindakans;
}
