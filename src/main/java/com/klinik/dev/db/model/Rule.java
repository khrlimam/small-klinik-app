package com.klinik.dev.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.contract.Comparable;
import com.klinik.dev.util.Util;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

/**
 * Created by khairulimam on 29/01/17.
 */
@DatabaseTable(tableName = "rule")
@Data
public class Rule implements Comparable, Serializable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(width = 20)
    private String ruleName;
    @DatabaseField
    private int intervalDays;
    @ForeignCollectionField
    private ForeignCollection<TindakanRule> tindakanRules;

    public DateTime getNextCheckUp(DateTime lastCheckup) {
        return lastCheckup.plusDays(this.intervalDays);
    }

    public boolean isTodayCheckup(DateTime lastCheckup) {
        return getNextCheckUp(lastCheckup)
                .isEqual(lastCheckup);
    }

    public String toStringDate(DateTime lastCheckup) {
        return getNextCheckUp(lastCheckup).toString(DateTimeFormat.forPattern(Util.DATE_PATTERN));
    }

    public boolean isMoreThanPeriod(DateTime lastCheckup) {
        int daysAbsenceFromNextCheckup = getIntervalDays() * Util.MAX_TIME_ABSENCE;
        DateTime now = DateTime.now().withTimeAtStartOfDay();
        DateTime absenceDay = lastCheckup.plusDays(daysAbsenceFromNextCheckup);
        int deltaDay = Days.daysBetween(absenceDay, now).getDays();
        return (deltaDay > 0);
    }

    @Override
    public String toString() {
        return String.format("%s (%d hari)", getRuleName(), getIntervalDays());
    }


    @Override
    public int toBeCompared() {
        return this.id;
    }
}
