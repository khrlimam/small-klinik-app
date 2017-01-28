package com.klinik.dev.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import org.joda.time.DateTime;

/**
 * Created by khairulimam on 23/01/17.
 */
@DatabaseTable(tableName = "pasien")
public @Data class Pasien {
    @DatabaseField(generatedId = true, width = 4)
    private int noRekamMedis;
    @DatabaseField(width = 30)
    private String nama;
    @DatabaseField(width = 10)
    private String namaPanggilan;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime tglLahir;
    @DatabaseField
    private String alamat;
    @DatabaseField(dataType = DataType.ENUM_STRING, unknownEnumName = "BELUM_MENIKAH")
    private STATUS status;
    @DatabaseField(width = 15)
    private String noTelepon;
    @DatabaseField
    private String pekerjaan;
    @DatabaseField(dataType = DataType.ENUM_STRING, unknownEnumName = "_")
    private AGAMA agama;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime tglRegister;
    @DatabaseField(dataType = DataType.DATE_TIME)
    private DateTime jadwalCheckUpSelanjutnya;
    @ForeignCollectionField
    private ForeignCollection<RiwayatTindakan> riwayatTindakans;

    public boolean apakahHariIniJadwalCheckUp() {
        DateTime dateTimeNow = new DateTime();
        return dateTimeNow
                .withTimeAtStartOfDay()
                .isEqual(this.getJadwalCheckUpSelanjutnya().withTimeAtStartOfDay());
    }

    public enum STATUS {
        MENIKAH,
        BELUM_MENIKAH
    }

    public enum AGAMA {
        _,
        ISLAM,
        KRISTEN_PROTESTAN,
        KRISTEN_KATOLIK,
        HINDU,
        BUDDHA,
        KONGHUCU,
        YAHUDI;
    }


}
