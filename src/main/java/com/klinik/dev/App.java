package com.klinik.dev;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.klinik.dev.db.DB;
import com.klinik.dev.db.model.Pasien;

import java.sql.SQLException;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    public static void josh(String[] args) {


        try {
            Dao<Pasien, Integer> pasienDao = DaoManager.createDao(DB.getDB(), Pasien.class);
            Pasien pasien = new Pasien();
            pasien.setNama("Sapri Al-Islah");
            pasienDao.create(pasien);
            List<Pasien> allPasien = pasienDao.queryForAll();
            for (Pasien p : allPasien) {
                System.out.println(String.format("%w\n", p.getNoRekamMedis()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

//
//
//        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
//        Date date = new Date();
//        System.out.println(df.format(date));
//        DateTime dateNow = new DateTime();
//        DateTime dateAfterNow = dateNow.plusDays(1);
//        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
//        System.out.println(dateNow.toString(dateTimeFormat));
//        System.out.println(dateAfterNow.toString(dateTimeFormat));
    }
}
