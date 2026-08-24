package com.globetrotter.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverter {

    public static final BigDecimal USD_TO_INR_RATE = new BigDecimal("100");

    /**
     * Converts USD amount to INR.
     */
    public static BigDecimal convertUsdToInr(BigDecimal usdAmount) {
        if (usdAmount == null) return null;
        return usdAmount.multiply(USD_TO_INR_RATE).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Converts a USD amount represented as a double to INR BigDecimal.
     */
    public static BigDecimal convertUsdToInr(double usdAmount) {
        return convertUsdToInr(BigDecimal.valueOf(usdAmount));
    }
}
