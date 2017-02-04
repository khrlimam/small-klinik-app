package com.klinik.dev.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by khairulimam on 04/02/17.
 */
public class NumberFormatUtil {
    public static DecimalFormat getRupiahFormat() {
        DecimalFormat IDR = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols rpSimbol = new DecimalFormatSymbols();
        rpSimbol.setCurrencySymbol("Rp ");
        rpSimbol.setMonetaryDecimalSeparator(',');
        rpSimbol.setGroupingSeparator('.');
        IDR.setDecimalFormatSymbols(rpSimbol);
        return IDR;
    }
}
