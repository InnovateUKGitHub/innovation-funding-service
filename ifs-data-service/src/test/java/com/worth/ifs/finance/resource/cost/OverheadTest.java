package com.worth.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OverheadTest {
    private Long id;
    private OverheadRateType rateType;
    private Integer customRate;
    private Overhead overhead;
    private BigDecimal agreedRate;

    @Before
    public void setUp() throws Exception {
        rateType = OverheadRateType.NONE;
        id = 0L;
        customRate = 200;
        agreedRate = new BigDecimal(25);
        overhead = new Overhead(id, rateType, customRate, agreedRate);
    }

    @Test
    public void overheadShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(overhead.getId().equals(id));
        assert(overhead.getRateType().equals(rateType));
        assert(overhead.getCustomRate().equals(customRate));
    }

    @Test
    public void totalForOverheadShouldAlwaysBeZeroTest() throws Exception {
        assertEquals(BigDecimal.ZERO, overhead.getTotal());
    }
}
