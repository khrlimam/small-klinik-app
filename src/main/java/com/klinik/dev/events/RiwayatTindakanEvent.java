package com.klinik.dev.events;

import com.klinik.dev.db.model.RiwayatTindakan;
import lombok.Data;

/**
 * Created by khairulimam on 31/01/17.
 */
@Data
public class RiwayatTindakanEvent {
  private RiwayatTindakan riwayatTindakan;
  private com.klinik.dev.enums.OPERATION_TYPE OPERATION_TYPE;

  public RiwayatTindakanEvent(RiwayatTindakan riwayatTindakan, com.klinik.dev.enums.OPERATION_TYPE OPERATION_TYPE) {
    this.riwayatTindakan = riwayatTindakan;
    this.OPERATION_TYPE = OPERATION_TYPE;
  }
}
