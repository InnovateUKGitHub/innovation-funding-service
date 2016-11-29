package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.OtherCost;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OtherCostTest {
    private Long id;
    private String description;
    private BigDecimal cost;
    private OtherCost otherCost;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        description = "Other cost item";
        cost = new BigDecimal(1000);
        otherCost = new OtherCost(id, description, cost);
    }

    @Test
    public void otherCostShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(otherCost.getId().equals(id));
        assert(otherCost.getDescription().equals(description));
        assert(otherCost.getCost().equals(cost));
    }

    @Test
    public void calculateTotalsForOtherCostTest() throws Exception {
        BigDecimal expected = new BigDecimal(1000);
        assertEquals(expected, otherCost.getTotal());
    }
}
