package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.TravelCost;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class TravelCostTest {
    private Long id;
    private String item;
    private BigDecimal cost;
    private Integer quantity;
    private TravelCost travelCost;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        item = "Travel to America for research consultancy";
        cost = new BigDecimal(600);
        quantity = 12;
        travelCost = new TravelCost(id, item, cost, quantity);
    }

    @Test
    public void travelCostShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(travelCost.getId().equals(id));
        assert(travelCost.getItem().equals(item));
        assert(travelCost.getCost().equals(cost));
        assert(travelCost.getQuantity().equals(quantity));
    }

    @Test
    public void calculateTotalsForTravelCostTest() throws Exception {
        BigDecimal expected = new BigDecimal(600).multiply(new BigDecimal(12));
        assertEquals(expected, travelCost.getTotal());
    }

    @Test
    public void calculatedTotalMustBeZeroWhenQuantityOrCostAreNotSetTest() throws Exception {
        TravelCost travelCostWithoutValues = new TravelCost();
        assertEquals(BigDecimal.ZERO, travelCostWithoutValues.getTotal());
    }
}
