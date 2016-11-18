package com.worth.ifs.project.finance.domain;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static com.worth.ifs.project.finance.resource.TimeUnit.DAY;
import static com.worth.ifs.project.finance.resource.TimeUnit.MONTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CostTest {

    @Test
    public void testStandaloneCostWithStringValue() {
        Cost cost = new Cost("12.34");
        assertEquals(new BigDecimal("12.34"), cost.getValue());
    }

    @Test
    public void testStandaloneCostWithBigDecimalValue() {
        Cost cost = new Cost(new BigDecimal("12.34"));
        assertEquals(new BigDecimal("12.34"), cost.getValue());
    }

    @Test
    public void testStandaloneCostWithEmptyOptionals() {
        Cost cost = new Cost("12.34");
        assertNull(cost.getCostTimePeriod());
        assertNull(cost.getCostCategory());
    }

    @Test
    public void testCostWithTimePeriod() {

        Cost cost = new Cost("12.34").withTimePeriod(1, DAY, 2, MONTH);
        ReflectionTestUtils.setField(cost, "id", 2L);

        assertNotNull(cost.getCostTimePeriod());
        assertNull(cost.getCostCategory());
        assertEquals(new BigDecimal("12.34"), cost.getValue());
        assertEquals(Long.valueOf(2), cost.getId());

        assertEquals(Integer.valueOf(1), cost.getCostTimePeriod().getOffsetAmount());
        assertEquals(DAY, cost.getCostTimePeriod().getOffsetUnit());
        assertEquals(Integer.valueOf(2), cost.getCostTimePeriod().getDurationAmount());
        assertEquals(MONTH, cost.getCostTimePeriod().getDurationUnit());
    }

    @Test
    public void testCostWithCostCategory() {

        CostCategory category = new CostCategory("My category");

        Cost cost = new Cost("12.34").withCategory(category);
        ReflectionTestUtils.setField(cost, "id", 2L);

        assertNull(cost.getCostTimePeriod());
        assertNotNull(cost.getCostCategory());
        assertEquals(new BigDecimal("12.34"), cost.getValue());
        assertEquals(Long.valueOf(2), cost.getId());

        assertEquals(category, cost.getCostCategory());
    }

    @Test
    public void testCostWithTimePeriodAndCategory() {

        CostCategory category = new CostCategory("My category");
        CostGroup costGroup = new CostGroup();

        Cost cost = new Cost("12.34").
                withTimePeriod(1, DAY, 2, MONTH).
                withCategory(category);

        ReflectionTestUtils.setField(cost, "id", 2L);
        cost.setCostGroup(costGroup);

        assertNotNull(cost.getCostTimePeriod());
        assertNotNull(cost.getCostCategory());
        assertEquals(new BigDecimal("12.34"), cost.getValue());
        assertEquals(Long.valueOf(2), cost.getId());

        assertEquals(Integer.valueOf(1), cost.getCostTimePeriod().getOffsetAmount());
        assertEquals(DAY, cost.getCostTimePeriod().getOffsetUnit());
        assertEquals(Integer.valueOf(2), cost.getCostTimePeriod().getDurationAmount());
        assertEquals(MONTH, cost.getCostTimePeriod().getDurationUnit());

        assertEquals(category, cost.getCostCategory());
        assertEquals(costGroup, cost.getCostGroup());
    }
}
