package com.klinik.dev.db.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.db.DB;
import lombok.Data;

import java.sql.SQLException;

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

  public static Dao<TindakanRule, Void> getDao() {
    try {
      return DaoManager.createDao(DB.getDB(), TindakanRule.class);
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
