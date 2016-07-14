package com.worth.ifs.util;


import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

/**
 * Math utils
 */
public class MathFunctions {

    /**
     * Consistent scale
     * @param numerator
     * @param denominator
     * @return
     */
    public static BigDecimal percentage(long numerator, long denominator) {
        int scale = 2;
        if (denominator != 0) {
            return valueOf(numerator).multiply(valueOf(100)).divide(valueOf(denominator), scale, HALF_UP);
        }
        else {
            return ZERO.setScale(scale);
        }
    }
}
