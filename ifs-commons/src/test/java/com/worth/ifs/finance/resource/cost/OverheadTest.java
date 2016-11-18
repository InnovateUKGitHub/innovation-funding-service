package com.worth.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OverheadTest {
    private Long id;
    private OverheadRateType rateType;
    private Integer rate;
    private Overhead overhead;

    @Before
    public void setUp() throws Exception {
        rateType = OverheadRateType.NONE;
        id = 0L;
        rate = 200;
        overhead = new Overhead(id, rateType, rate);
    }

    @Test
    public void overheadShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(overhead.getId().equals(id));
        assert(overhead.getRateType().equals(rateType));
        assert(overhead.getRate().equals(rate));
    }

    @Test
    public void totalForOverheadShouldAlwaysBeZeroTest() throws Exception {
        assertEquals(BigDecimal.ZERO, overhead.getTotal());
    }
}
