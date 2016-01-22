package com.worth.ifs.finance.resource.cost;

import com.worth.ifs.finance.resource.cost.CapitalUsage;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CapitalUsageTest {
    private Long id;
    private Integer deprecation;
    private String description;
    private String existing;
    private BigDecimal npv;
    private BigDecimal residualValue;
    private Integer utilisation;
    private CapitalUsage capitalUsage;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        deprecation = 10;
        description = "Other cost item";
        existing = "New" ;
        npv = new BigDecimal(200);
        residualValue = new BigDecimal(100);
        utilisation = 5;

        capitalUsage = new CapitalUsage(id, deprecation, description, existing, npv, residualValue, utilisation);
    }

    @Test
    public void capitalUsageShouldReturnCorrectBaseAttributesTest() throws Exception {
        assert(capitalUsage.getId().equals(id));
        assert(capitalUsage.getDeprecation().equals(deprecation));
        assert(capitalUsage.getDescription().equals(description));
        assert(capitalUsage.getExisting().equals(existing));
        assert(capitalUsage.getNpv().equals(npv));
        assert(capitalUsage.getResidualValue().equals(residualValue));
        assert(capitalUsage.getUtilisation().equals(utilisation));
    }

    @Test
    public void calculateTotalForCapitalUsageTest() throws Exception {
        BigDecimal expected = new BigDecimal(5).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        assertEquals(expected, capitalUsage.getTotal());
    }

    @Test
    public void totalMustBeZeroWhenDataIsNotAvailableTest() throws Exception {
        CapitalUsage emptyCapitalUsage= new CapitalUsage();
        assertEquals(BigDecimal.ZERO, emptyCapitalUsage.getTotal());
    }
}
