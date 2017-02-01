package com.klinik.dev.db.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.bussiness.BRule;
import com.klinik.dev.contract.Searchable;
import lombok.Data;

/**
 * Created by khairulimam on 29/01/17.
 */
@DatabaseTable(tableName = "rule")
@Data
public class Rule implements Searchable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private BRule rule;

    @Override
    public int getInt() {
        return this.id;
    }
}
