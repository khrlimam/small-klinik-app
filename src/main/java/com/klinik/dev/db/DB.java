package com.klinik.dev.db;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.DerbyEmbeddedDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Created by khairulimam on 23/01/17.
 */
public class DB {

    private static ConnectionSource connectionSource;

    public static ConnectionSource getDB() {
        if (connectionSource != null)
            return connectionSource;
        try {
            DatabaseType databaseType = new DerbyEmbeddedDatabaseType();
            String protocol = "jdbc:derby:";
            String dbUrl = String.format("%sklinikDB;create=true", protocol);
            connectionSource = new JdbcConnectionSource(dbUrl, databaseType);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connectionSource;
    }

}
