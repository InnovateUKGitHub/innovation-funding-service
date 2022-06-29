package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class HecpIndirectCostsTest {
    private Long id;
    private OverheadRateType rateType;
    private Integer rate;
    private HecpIndirectCosts hecpIndirectCosts;

    @Before
    public void setUp() throws Exception {
        rateType = OverheadRateType.NONE;
        id = 0L;
        rate = 200;
        hecpIndirectCosts = new HecpIndirectCosts(id, rateType, rate, 1L);
    }

    @Test
    public void overheadShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(hecpIndirectCosts.getId().equals(id));
        assert(hecpIndirectCosts.getRateType().equals(rateType));
        assert(hecpIndirectCosts.getRate().equals(rate));
    }

    @Test
    public void totalForOverheadShouldAlwaysBeZeroTest() throws Exception {
        assertEquals(BigDecimal.ZERO, hecpIndirectCosts.getTotal());
    }
}
