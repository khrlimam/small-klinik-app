package com.klinik.dev.events;

import com.klinik.dev.db.model.Tindakan;
import lombok.Data;

/**
 * Created by khairulimam on 31/01/17.
 */
@Data
public class TindakanEvent {
    private Tindakan tindakan;
    private OperationType OPERATION_TYPE;

    public TindakanEvent(Tindakan tindakan, OperationType OPERATION_TYPE) {
        this.tindakan = tindakan;
        this.OPERATION_TYPE = OPERATION_TYPE;
    }
}
