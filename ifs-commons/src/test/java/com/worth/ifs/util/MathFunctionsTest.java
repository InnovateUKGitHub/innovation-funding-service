package com.worth.ifs.util;

import org.junit.Test;

import java.math.BigDecimal;

import static com.worth.ifs.util.MathFunctions.percentage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests around helper utilities related to math
 */
public class MathFunctionsTest {

    @Test
    public void testPercentage() {
        assertEquals(percentage(0, 10), new BigDecimal("0.00")); // zero
        assertEquals(percentage(10, 10), new BigDecimal("100.00")); // one hundred
        assertEquals(percentage(1, 3), new BigDecimal("33.33")); // round down
        assertEquals(percentage(2, 3), new BigDecimal("66.67")); // round up
        assertEquals(percentage(2, 1), new BigDecimal("200.00")); // over one hundred
        assertEquals(percentage(-1, 10), new BigDecimal("-10.00")); // less than zero
        assertNotEquals(percentage(0, 10), new BigDecimal("0")); // decimal places incorrect
        assertEquals(percentage(0, 0), new BigDecimal("0.00")); // both numerator and denominator zero assumes to be zero percent

    }
}
