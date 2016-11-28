package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.SubContractingCost;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class SubContractingCostTest {
    private Long id;
    private BigDecimal cost;
    private String country;
    private String name;
    private String role;
    private SubContractingCost subContractingCost;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        cost = new BigDecimal(10023);
        country = "England";
        name = "Factory";
        role = "Production";
        subContractingCost = new SubContractingCost(id, cost, country, name, role);
    }

    @Test
    public void subContractingCostShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(subContractingCost.getId().equals(id));
        assert(subContractingCost.getCost().equals(cost));
        assert(subContractingCost.getCountry().equals(country));
        assert(subContractingCost.getName().equals(name));
        assert(subContractingCost.getRole().equals(role));
    }

    @Test
    public void calculateTotalsForSubContractingTest() throws Exception {
        BigDecimal expected = new BigDecimal(10023);
        assertEquals(expected, subContractingCost.getTotal());
    }
}
