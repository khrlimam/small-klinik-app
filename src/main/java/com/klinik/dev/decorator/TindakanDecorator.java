package com.klinik.dev.decorator;

import com.klinik.dev.contract.Comparable;
import com.klinik.dev.db.model.Tindakan;
import lombok.Data;

/**
 * Created by khairulimam on 18/03/17.
 */
@Data
public class TindakanDecorator implements Comparable {
  private Tindakan tindakan;

  public TindakanDecorator(Tindakan tindakan) {
    this.tindakan = tindakan;
  }

  @Override
  public String toString() {
    return this.tindakan.toString_();
  }

  @Override
  public int toBeCompared() {
    return this.tindakan.getId();
  }
}