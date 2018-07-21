package com.klinik.dev.events;

import com.klinik.dev.db.model.Rule;
import lombok.Data;

/**
 * Created by khairulimam on 31/01/17.
 */
@Data
public class RuleEvent {
  private Rule rule;
  private com.klinik.dev.enums.OPERATION_TYPE OPERATION_TYPE;

  public RuleEvent(Rule rule, com.klinik.dev.enums.OPERATION_TYPE OPERATION_TYPE) {
    this.rule = rule;
    this.OPERATION_TYPE = OPERATION_TYPE;
  }
}
