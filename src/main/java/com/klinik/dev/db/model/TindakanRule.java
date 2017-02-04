package com.klinik.dev.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

/**
 * Created by khairulimam on 02/02/17.
 */
@Data
@DatabaseTable(tableName = "tindakanrule")
public class TindakanRule {
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Tindakan tindakan;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Rule rule;
}
