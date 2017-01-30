package com.klinik.dev.bussiness;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khairulimam on 29/01/17.
 */
@Data
public class BTindakan implements Serializable {
    private String namaTindakan;
    private List<BRule> bRules;
}
