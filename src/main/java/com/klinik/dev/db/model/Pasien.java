package com.klinik.dev.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.klinik.dev.contract.Comparable;
import com.klinik.dev.enums.STATUS;
import com.klinik.dev.util.Util;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by khairulimam on 23/01/17.
 */
@DatabaseTable(tableName = "pasien")
@Data
public class Pasien implements Comparable {
    @DatabaseField(generatedId = true, width = 4)
    private int noRekamMedis;
    @DatabaseField(width = 30)
    private String nama;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime tglLahir;
    @DatabaseField
    private String alamat;
    @DatabaseField(dataType = DataType.ENUM_STRING, unknownEnumName = "BELUM_MENIKAH", width = 20)
    private STATUS status;
    @DatabaseField(width = 15)
    private String noTelepon;
    @DatabaseField(width = 20)
    private String pekerjaan;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime tglRegister;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime checkupTerakhir;
    @DatabaseField
    private String diagnosis;
    @DatabaseField
    private String fotoPath;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Tindakan tindakan;
    @ForeignCollectionField
    private ForeignCollection<RiwayatTindakan> riwayatTindakans;

    public DateTime getCheckupTerakhir() {
        return checkupTerakhir.withTimeAtStartOfDay();
    }

    public String getTglRegisterToString() {
        return this.tglRegister.toString(DateTimeFormat.forPattern(Util.DATE_PATTERN));
    }

    public DateTime getCheckupTerakhirActualDate() {
        return this.checkupTerakhir;
    }

    public Pasien getPasien() {
        return this;
    }

    public String getJadwalSelanjutnya() {
        if (tindakan == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        ForeignCollection<TindakanRule> rules = tindakan.getTindakanrules();
        rules.forEach(tindakanRule -> {
            Rule rule = tindakanRule.getRule();
            if (rule != null)
                stringBuilder.append(String.format("%s, %s\n", rule.getRuleName(), rule.toStringDate(checkupTerakhir)));
        });
        return stringBuilder.toString();
    }

    @Override
    public int toBeCompared() {
        return this.noRekamMedis;
    }
}
