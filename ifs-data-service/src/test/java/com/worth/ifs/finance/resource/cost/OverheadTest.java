package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.Overhead;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OverheadTest {
    private Long id;
    private String acceptRate;
    private Integer customRate;
    private Overhead overhead;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        acceptRate = "Yes";
        customRate = 200;
        overhead = new Overhead(id, acceptRate, customRate);
    }

    @Test
    public void overheadShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(overhead.getId().equals(id));
        assert(overhead.getAcceptRate().equals(acceptRate));
        assert(overhead.getCustomRate().equals(customRate));
    }

    @Test
    public void totalForOverheadShouldAlwaysBeZeroTest() throws Exception {
        assertEquals(BigDecimal.ZERO, overhead.getTotal());
    }
}
