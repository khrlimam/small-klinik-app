package com.klinik.dev.bussiness;

import com.google.common.eventbus.Subscribe;
import com.klinik.dev.db.model.Rule;
import com.klinik.dev.events.RuleEvent;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class Rules implements Serializable {
    private List<Rule> rules;

    public Rules(List<Rule> rules) {
        this.rules = rules;
    }
}
