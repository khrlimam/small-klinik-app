package com.klinik.dev.bussiness;

import com.klinik.dev.Log;
import com.klinik.dev.Util;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class BRule implements Serializable {
    private String ruleName;
    private int intervalDays;

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

}
