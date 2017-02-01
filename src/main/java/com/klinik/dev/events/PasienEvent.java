package com.klinik.dev.events;

import com.klinik.dev.db.model.Pasien;
import lombok.Data;

/**
 * Created by khairulimam on 31/01/17.
 */
@Data
public class PasienEvent {
    private Pasien pasien;
    private OperationType OPERATION_TYPE;

    public PasienEvent(Pasien pasien, OperationType OPERATION_TYPE) {
        this.pasien = pasien;
        this.OPERATION_TYPE = OPERATION_TYPE;
    }
}
