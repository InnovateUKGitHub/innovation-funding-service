package org.innovateuk.ifs.finance.resource.cost;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class IndirectCostTest {
    private Long id;
    private BigInteger cost;
    private IndirectCost indirectCost;

    @Before
    public void setUp() {
        id = 0L;
        cost = BigInteger.ONE;
        indirectCost = new IndirectCost(1L, id, cost);
    }

    @Test
    public void indirectCostShouldReturnCorrectBaseAttributesTest() {
        assert(indirectCost.getId().equals(id));
        assert(indirectCost.getCost().equals(cost));
    }

    @Test
    public void calculateTotalsForIndirectCostTest() {
        BigDecimal expected = new BigDecimal(cost).multiply(BigDecimal.ONE);
        assertEquals(expected, indirectCost.getTotal());
    }
}
