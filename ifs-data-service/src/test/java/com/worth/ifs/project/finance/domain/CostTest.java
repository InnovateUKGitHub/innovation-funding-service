package com.worth.ifs.project.finance.domain;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static com.worth.ifs.project.finance.domain.CostTimePeriod.TimeUnit.DAY;
import static com.worth.ifs.project.finance.domain.CostTimePeriod.TimeUnit.MONTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertFalse(cost.getCostTimePeriod().isPresent());
        assertFalse(cost.getCostCategory().isPresent());
    }

    @Test
    public void testCostWithTimePeriod() {

        Cost cost = new Cost("12.34").withTimePeriod(1, DAY, 2, MONTH);
        ReflectionTestUtils.setField(cost, "id", 2L);

        assertTrue(cost.getCostTimePeriod().isPresent());
        assertFalse(cost.getCostCategory().isPresent());
        assertEquals(new BigDecimal("12.34"), cost.getValue());
        assertEquals(Long.valueOf(2), cost.getId());

        assertEquals(Integer.valueOf(1), cost.getCostTimePeriod().get().getOffsetAmount());
        assertEquals(DAY, cost.getCostTimePeriod().get().getOffsetUnit());
        assertEquals(Integer.valueOf(2), cost.getCostTimePeriod().get().getDurationAmount());
        assertEquals(MONTH, cost.getCostTimePeriod().get().getDurationUnit());
    }

    @Test
    public void testCostWithCostCategory() {

        CostCategory category = new CostCategory("My category");

        Cost cost = new Cost("12.34").withCategory(category);
        ReflectionTestUtils.setField(cost, "id", 2L);

        assertFalse(cost.getCostTimePeriod().isPresent());
        assertTrue(cost.getCostCategory().isPresent());
        assertEquals(new BigDecimal("12.34"), cost.getValue());
        assertEquals(Long.valueOf(2), cost.getId());

        assertEquals(category, cost.getCostCategory().get());
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

        assertTrue(cost.getCostTimePeriod().isPresent());
        assertTrue(cost.getCostCategory().isPresent());
        assertEquals(new BigDecimal("12.34"), cost.getValue());
        assertEquals(Long.valueOf(2), cost.getId());

        assertEquals(Integer.valueOf(1), cost.getCostTimePeriod().get().getOffsetAmount());
        assertEquals(DAY, cost.getCostTimePeriod().get().getOffsetUnit());
        assertEquals(Integer.valueOf(2), cost.getCostTimePeriod().get().getDurationAmount());
        assertEquals(MONTH, cost.getCostTimePeriod().get().getDurationUnit());

        assertEquals(category, cost.getCostCategory().get());
        assertEquals(costGroup, cost.getCostGroup().get());
    }
}
